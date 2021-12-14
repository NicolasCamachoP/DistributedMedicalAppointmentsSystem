/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package INS;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class INS_Servidor 
{
    public GUIINS gui;
    
    public INS_Servidor(GUIINS gui)
    {
        this.gui = gui;
        try{
            Registry registry = LocateRegistry.createRegistry(5678);
            InterfaceINS ins = new INS(gui);
            InterfaceINS stub = 
                    (InterfaceINS) UnicastRemoteObject.exportObject(ins, 0);
            registry.rebind("//ins", stub);
            System.out.println("INS Servidor ready");
        }catch(Exception e){
            System.out.println("INS Servidor main "+e.getMessage());
        }
    }
    
}
