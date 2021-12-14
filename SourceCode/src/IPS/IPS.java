/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IPS;

import Cliente.InterfaceCliente;
import Cliente.Paciente;
import EPS.InterfaceEPS;
import INS.InterfaceINS;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class IPS 
{
    
    public ObjectOutputStream out;
    public ObjectInputStream in;
    public ServerSocket serverS;
    public int puerto;
    public int puertoCliente = 6789;
    public Map<String,String[]> registrosEPS;
    public static final String NAMEFILE = "IPS.txt";
    public String ipINS;
    public int puertoINS = 5678;
    public Map<String,AgendaDia> agenda;
    public Map<String, String> ip_pacientes;
    public GUIIPS gui;

    public IPS(GUIIPS gui) 
    {
        this.gui = gui;
        registrosEPS = new HashMap<>();
        ip_pacientes = new HashMap<>();
        agenda = new HashMap<>();
        
        registrosEPS = Collections.synchronizedMap(registrosEPS);
        ip_pacientes = Collections.synchronizedMap(ip_pacientes);
        agenda = Collections.synchronizedMap(agenda);
        
        leerArchivo();
        crearHiloEscucha();
        crearHiloRegistro();
    }
    
    private void crearHiloRegistro() {
        //Se crea u hilo que va a escuchar todos los mensajes que lleguen al socket leído en el archivo de configuración
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try 
                {
                    while(true)
                    {
                        Thread.sleep(20000);
                        escribirCitas();
                    }
                } 
                catch (Exception e) 
                {
                    System.out.println(e.getMessage());
                }
            }
        });
        hilo.start();
    }
    
    public void escribirCitas()
    {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            
            fichero = new FileWriter("Citas.txt");
            pw = new PrintWriter(fichero);
            for (String s: agenda.keySet()) 
            {
                for (Cita c : agenda.get(s).citas) 
                {
                    pw.println("CitaAsignada: "+ c.toString());
                }
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
           try 
           {
                if (null != fichero)
                    fichero.close();
           } 
           catch (Exception e2) 
           {
              e2.printStackTrace();
           }
        }
    }
    
    private boolean crearHiloEscucha() {
        //Se crea u hilo que va a escuchar todos los mensajes que lleguen al socket leído en el archivo de configuración
        IPS ips = this;
        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverS = new ServerSocket(puerto);
                    System.out.println("IPS Escuchando - CrearHiloEscucha");

                    while (true) {
                        System.out.print("");
                        try {
                            Socket clientSocket = serverS.accept();                            
//                            System.out.println("Solicitud recibida - IPS!!!");
                            //Se utiliza una clase ConnectionB para gesitonar los mensajes recibidos de otros brokers
                            ConnectionIPS c = new ConnectionIPS(clientSocket, ips);
                        } catch (SocketTimeoutException e) {
                            System.out.println("Esuchando solicitudes");
                        }

                    }

                } catch (IOException ex) {
                    Logger.getLogger(IPS.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        });
        hiloEscucha.start();
        return true;
    }

    private void leerArchivo() 
    {
        try 
        {
            File f = new File(NAMEFILE);    
                
            Scanner input = new Scanner(f);
                
            while (input.hasNextLine()) 
            {  
                String line = input.nextLine();
                if (line.equals("EPS:")) 
                {
                    line = input.nextLine();
                    while(!line.equals("INS:"))
                    {
                        
                        String[] split = line.split(";"), aux = new String[2];
                        aux[0]= split[1];
                        aux[1]= "1234";
                        registrosEPS.put(split[0], aux);
                        line = input.nextLine();
                    }
                    if(line.equals("INS:"))
                    {
                        line = input.nextLine();
                        ipINS= line;
                    }
                }
                else if (line.equals("Puerto IPS:")) 
                {
                    line = input.nextLine();
                    puerto = Integer.parseInt(line);
                }
            }
            input.close();
            
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public InterfaceEPS rmiEPS(Paciente p)
    {
        
        String[] info = registrosEPS.get(p.eps);
        
//        System.setSecurityManager(new SecurityManager());
        InterfaceEPS eps = null;
        InterfaceEPS retorno =null;
        
        try{
            //InetAddress address1 = InetAddress.getByName("192.168.0.4");
            Registry registry = LocateRegistry.getRegistry(info[0], Integer.parseInt(info[1]));
            eps = (InterfaceEPS) registry.lookup("//eps."+p.eps);
            if(eps.siBeneficiario(p.documento))
            {
                retorno = eps;
            }
        }
        catch(RemoteException e){
            System.out.println(e.getMessage());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return retorno;
        
    }
    
    public void actualizarCita(Cita c)
    {
        try{
            //InetAddress address1 = InetAddress.getByName("192.168.0.4");
            Registry registry = LocateRegistry.getRegistry(ip_pacientes.get(c.paciente.documento), puertoCliente);
            InterfaceCliente cliente = (InterfaceCliente) registry.lookup("//cliente");
            cliente.imprimirActualizacion(c);
            InterfaceEPS eps = rmiEPS(c.paciente);
            eps.ActualizarCita(c);
            
            gui.actualizarCita(c);
        }
        catch(RemoteException e){
            System.out.println(e.getMessage());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        
    }
    
    public InterfaceINS rmiINS()
    {
//        System.setSecurityManager(new SecurityManager());
        InterfaceINS ins = null;
        
        try{
            //InetAddress address1 = InetAddress.getByName("192.168.0.4");
            Registry registry = LocateRegistry.getRegistry(ipINS, puertoINS);
            ins = (InterfaceINS) registry.lookup("//ins");
        }
        catch(RemoteException e){
            System.out.println(e.getMessage());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return ins;
        
    }
    
    public ArrayList<Cita> asignarCita(Paciente p, int puntaje, InterfaceEPS eps)
    {    
        System.out.println("Paciente: "+p.nombre + " puntaje: "+puntaje);
        ArrayList<Cita> citas = new ArrayList<>();
        Calendar calendario = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yy hh");
        int dia = calendario.get(Calendar.DAY_OF_MONTH);          
        int hora = 0;
        Cita c = null;
        boolean agendado = false;
        if(puntaje < 90)
        {
            dia += 3;
            while(!agendado)
            {
                dia += 1;
                calendario.set(Calendar.DAY_OF_MONTH, dia);
                Date fecha = calendario.getTime();
                String s = dateFormat.format(fecha);
                if(!agenda.containsKey(s))
                {
                    agenda.put(s, new AgendaDia(s));
                    hora = 8;
                }
                else
                {
                    String fecha2 = agenda.get(s).citas.get(agenda.get(s).citas.size()-1).fecha;
                    try 
                    {
                        Date d= dateFormat2.parse(fecha2);
                        hora = d.getHours()+1;
                    }
                    catch (ParseException ex) 
                    {
                        Logger.getLogger(IPS.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                c = new Cita(puntaje, p, s+ " "+hora+":00");
                if(agenda.get(s).agendarCita(c))
                {
                    agendado = true;
                    citas.add(c);
                    try {
                        eps.registrarCita(c);
                    } catch (RemoteException ex) {
                        Logger.getLogger(IPS.class.getName()).log(Level.SEVERE, null, ex);
                    }                                        
                }
            }
        }
        else
        {
            boolean agendadoP = false;
            
            while(!agendadoP)
            {
                dia += 1;
                calendario.set(Calendar.DAY_OF_MONTH, dia);
                Date fecha = calendario.getTime();
                String s = dateFormat.format(fecha);
                if(!agenda.containsKey(s))
                {
                    agenda.put(s, new AgendaDia(s));
                    hora = 8;
                }
                else
                {
                    String fecha2 = agenda.get(s).citas.get(agenda.get(s).citas.size()-1).fecha;
                    try 
                    {
                        Date d= dateFormat2.parse(fecha2);
                        hora = d.getHours()+1;
                    }
                    catch (ParseException ex) 
                    {
                        Logger.getLogger(IPS.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                c = new Cita(puntaje, p, s+ " "+hora+":00");
                if(agenda.get(s).agendarCita(c))
                {
                    agendadoP = true;
                }
                else
                {
                    Cita c2 = agenda.get(s).agendarCitaUrgente(c);
                    if(c2 != null)
                    {
                        agendadoP = true;
                        agendado = false;
                        while(!agendado)
                        {
                            dia += 1;
                            calendario.set(Calendar.DAY_OF_MONTH, dia);
                            fecha = calendario.getTime();
                            s = dateFormat.format(fecha);
                            if(!agenda.containsKey(s))
                            {
                                agenda.put(s, new AgendaDia(s));
                                hora = 8;
                            }
                            else
                            {
                                String fecha2 = agenda.get(s).citas.get(agenda.get(s).citas.size()-1).fecha;
                                try 
                                {
                                    Date d= dateFormat2.parse(fecha2);
                                    hora = d.getHours()+1;
                                }
                                catch (ParseException ex) 
                                {
                                    Logger.getLogger(IPS.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                            c2.fecha = s+ " "+hora+":00";
                            if(agenda.get(s).agendarCita(c2))
                            {
                                agendado = true;
                            }
                        }
                        try {
                            eps.registrarCita(c2);
                            citas.add(c2);
                        } catch (RemoteException ex) {
                            Logger.getLogger(IPS.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            try {
                eps.registrarCita(c);
                citas.add(c);
            } catch (RemoteException ex) {
                Logger.getLogger(IPS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return citas;
    }
    
}
