/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IPS;

import Cliente.Cliente;
import Cliente.Paciente;
import EPS.InterfaceEPS;
import INS.InterfaceINS;
import Utils.Mensaje;
import Utils.Tipo;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class ConnectionIPS extends Thread 
{
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    IPS ips;

    public ConnectionIPS(Socket aClientSocket, IPS ips) 
    {
        //Creo un hilo que va a vigilar el socket recibio del broker recibido
        try 
        {
            this.ips =ips;
            clientSocket = aClientSocket;
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            //Vigilo constantemente los mensajes
            this.start();
        } 
        catch (IOException e) 
        {
            System.out.println("Connection:" + e.getMessage());
        }
    } // end Connection
    
    
    public void run() 
    {
        try 
        {	   
            Mensaje m = (Mensaje) in.readObject();
            if(m.tipo.equals(Tipo.DateRequest))
            {
                Paciente p = (Paciente)m.contenido;
                ips.ip_pacientes.put(p.documento, clientSocket.getInetAddress().getHostAddress());
                String str = "Solicitud recibida por un Cliente con IP: "+clientSocket.getInetAddress().getHostAddress();
                str+="\nConexión remota con EPS "+ p.eps +" realizada\n";
                InterfaceEPS eps = ips.rmiEPS(p);
                String s = "";
                if(eps==null)
                {
                    s = "El paciente con documento: "+p.documento+" no es beneficiario de la EPS "+ p.eps;
                    str += ", Respuesta: \n" + s;
                    
                    ips.gui.agregarCita(new Cita(-1, p, "No es Beneficiario"));
                    
                    out.writeObject(new Mensaje(Tipo.DateReply, s));
                }
                else
                {
                    s="El paciente con documento: "+p.documento+" es beneficiario de la EPS "+ p.eps;
                    str += ", Respuesta: \n" + s;
                    str+="\nConexión remota con INS realizada\n";
                    InterfaceINS ins = ips.rmiINS();
                    int puntaje = ins.evaluarPaciente(p);
                    if(puntaje>=70)
                    {
                        s = "Cumple los requisitos para pedir una cita";
                        System.out.println(s);
                        str += ", "+s;
                        if(eps.autorizar(p.documento, puntaje))
                        {
                            s = "La EPS "+p.eps+" cubre al paciente "+p.nombre;
                            System.out.println(s);
                            str += ", "+s;
                            str += "\nAsignando Cita ... \n";
                            ArrayList<Cita> citas = ips.asignarCita(p, puntaje, eps);
                            System.out.println(citas);
                            for (Cita cita : citas) 
                            {                             
                                try 
                                {
                                    if(cita.paciente.documento == p.documento)
                                    {
                                        ips.gui.agregarCita(cita);
                                        str += "\nSe asigno la cita: "+ cita.toString();
                                        out.writeObject(new Mensaje(Tipo.DateReply, cita));
                                    }   
                                    else
                                    {
                                        ips.actualizarCita(cita);
                                        str += "\nSe actualizó la cita: " + cita.toString();
                                    }
                                    
                                } catch (IOException ex) {
                                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        else
                        {
                            s="La EPS "+p.eps+" NO cubre al paciente "+p.nombre;
                            str += ", pero "+s;
                            ips.gui.agregarCita(new Cita(puntaje, p, "El plan no cubre"));
                            System.out.println(s);
                            out.writeObject(new Mensaje(Tipo.DateReply, s));
                        }
                        
                    }
                    else
                    {
                        s="El paciente "+p.nombre+" no parece tener Coronavirus o es asintomático";
                        str += ", pero "+s;
                        ips.gui.agregarCita(new Cita(puntaje, p, "No requiere cita"));
                        System.out.println(s);
                        out.writeObject(new Mensaje(Tipo.DateReply, s));
                    }
                }
                ips.gui.escribirEstado(str);
//                m = new Mensaje(Tipo.DateReply, s);
//                out.writeObject(m);
            }
        } 
        catch (EOFException e) 
        {
            System.out.println("EOF:" + e.getMessage());
        } 
        catch (IOException e) 
        {
            System.out.println("Breadline:" + e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionIPS.class.getName()).log(Level.SEVERE, null, ex);
        } 
//        catch (InterruptedException ex) {
//            Logger.getLogger(ConnectionIPS.class.getName()).log(Level.SEVERE, null, ex);
//        }
        finally 
        {
            try 
            {
                clientSocket.close();
            } 
            catch (IOException e) 
            {
                /*close failed*/
            }
        }
    } // end run
    
}
