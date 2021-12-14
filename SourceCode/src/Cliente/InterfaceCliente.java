/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import IPS.Cita;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public interface InterfaceCliente extends Remote
{
    void imprimirActualizacion(Cita c) throws RemoteException;
    
}
