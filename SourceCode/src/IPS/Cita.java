/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IPS;

import Cliente.Paciente;
import java.io.Serializable;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class Cita implements Serializable
{
    public int puntaje;
    public Paciente paciente;
    public String fecha;

    public Cita(int puntaje, Paciente p, String fecha) 
    {
        this.puntaje = puntaje;
        this.paciente = p;
        this.fecha = fecha;
    }
    
    @Override
    public String toString()
    {
        return "\n---------------------------------\nPaciente: "+paciente.nombre+"\nCedula: "+ paciente.documento +"\nTiene un puntaje de: "+ puntaje +"\nSu cita fue asignada en la fecha: "+ fecha+"\n---------------------------------";
    }
    
    public String toStringArchivo()
    {
        return "Paciente: "+paciente.nombre+" Cedula: "+ paciente.documento +" Tiene un puntaje de: "+ puntaje +" Su cita fue asignada en la fecha: "+ fecha;
    }
    
}
