/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EPS;

import IPS.Cita;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public interface InterfaceEPS extends Remote
{
    String getName() throws RemoteException;
    boolean siBeneficiario(String cedula) throws RemoteException;
    boolean autorizar(String cedula, int puntaje) throws RemoteException;
    void registrarCita(Cita c) throws RemoteException;
    void ActualizarCita(Cita c) throws RemoteException;
}
