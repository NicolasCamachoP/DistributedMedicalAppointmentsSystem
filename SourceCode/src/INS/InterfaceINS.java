/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package INS;

import Cliente.Paciente;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public interface InterfaceINS extends Remote
{
    int evaluarPaciente(Paciente p) throws RemoteException;
    
}
