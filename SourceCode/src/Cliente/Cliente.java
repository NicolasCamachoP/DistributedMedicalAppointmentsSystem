/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import IPS.Cita;
import Utils.Mensaje;
import Utils.Tipo;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class Cliente implements InterfaceCliente
{
    public List<Paciente> pacientes;
    public static final String NAMEFILE = "paciente_";
    public static final String NAMEFILE2 = "ClienteConfig.txt";
    int puertoIPS;
    int puertoRMI = 6789;
    String ipIPS;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    public ServerSocket serverS;
    public GUICliente gui;

    public Cliente(GUICliente gui) 
    {
        this.gui = gui;
        this.pacientes = new ArrayList<>();
        pacientes = Collections.synchronizedList(pacientes);
        leerArchivoConfig();
        leerArchivoPacientes();
        vigilarHora();
    }
    
    public int obtenerPuerto()
    {
        return puertoRMI;
    }
    
    public void leerArchivoConfig()
    {
        try 
        {
            File f = new File(NAMEFILE2);
                  
            Scanner input = new Scanner(f);

            while (input.hasNextLine()) 
            {  
                String line = input.nextLine();
                if (line.equals("IP IPS:")) 
                {
                    line = input.nextLine();
                    ipIPS = line;
                } 
                else if (line.equals("Puerto IPS:")) 
                {
                    line = input.nextLine();
                    puertoIPS = Integer.parseInt(line);
                }
            }
            input.close();

        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void leerArchivoPacientes()
    {
        try 
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy h:mm:ss a");
            Date fecha = null;
            String nombre = "", documento ="", eps="";
            int edad = 0, cantCirugias = 0;
            ArrayList<String> patologias = new ArrayList<>(), sintomas = new ArrayList<>();
            int i = 1;
            File f = new File(NAMEFILE+i+".txt");
            while(f.exists())
            {       
                patologias = new ArrayList<>();
                sintomas = new ArrayList<>();
                Scanner input = new Scanner(f);
                
                while (input.hasNextLine()) 
                {  
                    String line = input.nextLine();
                    if (line.equals("Date:")) 
                    {
                        line = input.nextLine();
                        fecha = dateFormat.parse(line);
//                        System.out.println("Date: " + fecha);
                    } 
                    else if (line.equals("Nombre:")) 
                    {
                        line = input.nextLine();
                        nombre = line;
//                        System.out.println("Nombre: " + nombre);
                    }
                    else if (line.equals("Documento:")) 
                    {
                        line = input.nextLine();
                        documento = line;
//                        System.out.println("Nombre: " + nombre);
                    }
                    else if(line.equals("Edad:"))
                    {
                        edad = input.nextInt();
//                        System.out.println("Edad: " + edad);
                    }
                    else if (line.equals("EPS:")) 
                    {
                        line = input.nextLine();
                        eps = line;
//                        System.out.println("Nombre: " + nombre);
                    }
                    else if(line.equals("Patologias:"))
                    {
                        line = input.nextLine();
                        String[] split = line.split(",");
                        patologias.addAll(Arrays.asList(split));
//                        System.out.println("Patologias: " + patologias);
                    }
                    else if(line.equals("Sintomas:"))
                    {
                        line = input.nextLine();
                        String[] split = line.split(",");
                        sintomas.addAll(Arrays.asList(split));
//                        System.out.println("Sintomas: " + sintomas);
                    }
                    else if(line.equals("Antecendes Cirugias Importantes:"))
                    {
                        cantCirugias = input.nextInt();
                    }
                }
                pacientes.add(new Paciente(fecha, edad, nombre, documento, eps, sintomas, patologias, cantCirugias));
                input.close();
                i ++;
                f = new File(NAMEFILE+i+".txt");
            }
            gui.llenarPacientes(new ArrayList<>(pacientes));
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void vigilarHora()
    {
        ArrayList<Paciente> aux = new ArrayList<>();
        aux.addAll(pacientes);
        ArrayList<Paciente> index = new ArrayList<>();
        while(aux.size()>0)
        {
            for (int i=0; i<aux.size();i++) 
            {
                Calendar calendario = Calendar.getInstance();
                long ms = calendario.getTime().getTime()-aux.get(i).tiempo.getTime();
                if(ms>= 0)
                {
                    gui.escribirEstado("Pidiendo cita del paciente con documento "+aux.get(i).documento+", Hora programada de petición: "+aux.get(i).tiempo);
                    pedirCita(aux.get(i));
                    index.add(aux.get(i));
                }
            }
            for (Paciente j : index) 
            {              
                aux.remove(j);
            }
            index.clear();
        }
    }
    
    public void pedirCita(Paciente p)
    {   
        try {

            Socket s = new Socket(ipIPS, puertoIPS);
            out = new ObjectOutputStream(s.getOutputStream());
            System.out.println(p.documento);
            out.writeObject(new Mensaje(Tipo.DateRequest, p));
            in = new ObjectInputStream(s.getInputStream());
            Mensaje m = (Mensaje) in.readObject();
            if (m.tipo == Tipo.DateReply) 
            {
                System.out.println(m.contenido.toString());
                gui.escribirEstado("Respuesta a la petición del paciente con documento "+p.documento+": "+m.contenido.toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void imprimirActualizacion(Cita c) 
    {
        System.out.println("Update: " + c.toString());
        gui.escribirEstado("Se realizó una actualización de la cita del paciente con documento "+c.paciente.documento+",la información de su nueva cita es: "+c.toString());
    }
    
}
