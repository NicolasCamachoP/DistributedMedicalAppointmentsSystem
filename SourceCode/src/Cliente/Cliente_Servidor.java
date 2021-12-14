/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class Cliente_Servidor 
{
    public Cliente_Servidor(GUICliente gui){
//        System.setSecurityManager(new SecurityManager());
        try{
            InterfaceCliente cliente = new Cliente(gui);
            Registry registry = LocateRegistry.createRegistry(6789);
            InterfaceCliente stub = 
                    (InterfaceCliente) UnicastRemoteObject.exportObject(cliente, 0);
            registry.rebind("//cliente", stub);
            System.out.println("Cliente Servidor ready");
        }catch(Exception e){
            System.out.println("Cliente Servidor main "+e.getMessage());
        }
    }
    
}
