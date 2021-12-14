/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EPS;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class EPS_Servidor 
{
    public InterfaceEPS eps;
    public GUIEPS gui;
    public EPS_Servidor(GUIEPS gui)
    {
        this.gui = gui;
        try{            
            Registry registry = LocateRegistry.createRegistry(1234);
            eps = new EPS(gui);
            InterfaceEPS stub = 
                    (InterfaceEPS) UnicastRemoteObject.exportObject(eps, 0);
            System.out.println(eps.getName());
            registry.rebind("//eps."+eps.getName(), stub);
            System.out.println("ESP Servidor ready");
        }catch(Exception e){
            System.out.println("ESP Servidor main "+e.getMessage());
        }
    }

    
}
