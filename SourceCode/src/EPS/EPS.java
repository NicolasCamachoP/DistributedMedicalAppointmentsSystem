/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EPS;

import IPS.Cita;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class EPS implements InterfaceEPS {

    public String nombre;
    public Map<String, String> pacientesID;
    public Map<String, Cita> historiaClinica;
    public static final String NAMEFILE = "EPS.txt";
    public Cubrimiento[] planes;
    public GUIEPS gui;

    public EPS(GUIEPS gui) {
        this.gui = gui;
        planes = Cubrimiento.values();
        pacientesID = new HashMap<>();
        historiaClinica = new HashMap<>();

        /*pacientesID = Collections.synchronizedMap(pacientesID);
        historiaClinica = Collections.synchronizedMap(historiaClinica);*/
        leerArchivo();
        crearHiloRegistro();
    }

    private void crearHiloRegistro() {
        //Se crea u hilo que va a escuchar todos los mensajes que lleguen al socket leído en el archivo de configuración
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.print("");
                        Thread.sleep(20000);
                        escribirHistoriaClinica();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        hilo.start();
    }

    public void escribirHistoriaClinica() {
        HashMap<String, Cita> auxMap = new HashMap<String, Cita>(historiaClinica);

        for (String cedula : auxMap.keySet()) {
            FileWriter fichero = null;
            PrintWriter pw = null;
            try {

                fichero = new FileWriter(cedula + ".txt", true);
                pw = new PrintWriter(fichero);

                pw.println("Cita: " + auxMap.get(cedula).toStringArchivo());
                auxMap.remove(cedula);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != fichero) {
                        fichero.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

    }

    @Override
    public String getName() {
        return nombre;
    }

    @Override
    public boolean siBeneficiario(String cedula) throws RemoteException {
        boolean bandera = pacientesID.containsKey(cedula);
        String s = "Llegó solicitud de servicio 'siBeneficiario', respuesta: ";
        if (bandera) {
            s += "El paciente con cédula " + cedula + " SI es beneficiario";
        } else {
            s += "El paciente con cédula " + cedula + " NO es beneficiario";
        }

        gui.escribirEstado(s);

        return bandera;
    }

    private void leerArchivo() {
        try {
            File f = new File(NAMEFILE);

            Scanner input = new Scanner(f);

            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.equals("Nombre:")) {
                    line = input.nextLine();
                    nombre = line;
//                        System.out.println("Date: " + fecha);
                } else if (line.equals("Pacientes:")) {
                    while (input.hasNextLine()) {
                        line = input.nextLine();
                        String[] split = line.split(";");
                        pacientesID.put(split[0], split[1]);
                    }
                }
            }
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean autorizar(String cedula, int puntaje) throws RemoteException {
        System.out.println("Cedula en autorizar IPS: " + cedula + " Puntaje: " + puntaje);
        boolean bandera = false;
        Cubrimiento c = null;
        for (Cubrimiento plan : planes) {
            if (pacientesID.get(cedula).equals(plan.nombre)) {
                c = plan;
                if (plan.nivelMax >= puntaje) {
                    bandera = true;
                    break;
                }
            }
        }

        String s = "Llegó solicitud de servicio 'autorizar', respuesta: ";
        if (bandera) {
            s += "Se autoriza al paciente con cédula " + cedula + ", con puntaje de " + puntaje + " y plan de Cubrimiento " + c.nombre;
        } else {
            s += "No se autoriza al paciente con cédula " + cedula + ", con puntaje de " + puntaje + " y plan de Cubrimiento " + c.nombre + ", su plan no cubre su puntaje de gravedad";
        }

        gui.escribirEstado(s);

        return bandera;
    }

    @Override
    public void registrarCita(Cita c) throws RemoteException {
        historiaClinica.put(c.paciente.documento, c);
        gui.agregarCita(c);
        gui.escribirEstado("Cita registrada: " + c.toString());
    }

    @Override
    public void ActualizarCita(Cita c) throws RemoteException {
        historiaClinica.put(c.paciente.documento, c);
        gui.actualizarCita(c);
        gui.escribirEstado("Cita actualizada: " + c.toString());
    }

}
