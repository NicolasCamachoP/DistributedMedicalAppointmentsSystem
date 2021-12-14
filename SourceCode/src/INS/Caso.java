/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package INS;

import Cliente.Paciente;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class Caso 
{
    public Paciente paciente;
    public int puntuacion;
    public String gravedad;

    public Caso(Paciente paciente, int puntuacion, String gravedad) {
        this.paciente = paciente;
        this.puntuacion = puntuacion;
        this.gravedad = gravedad;
    }
    
    @Override
    public String toString()
    {
        return "Paciente con nombre "+ paciente.nombre +" y cédula "+ paciente.documento+" tiene una puntuación de "+ puntuacion +" su estado de gravedad es " + gravedad;
    }
    
    public String toString2()
    {
        return "\n----------------------------\nPaciente con nombre "+ paciente.nombre +" \nCédula "+ paciente.documento+" \nPuntuación de "+ puntuacion +" \nEstado de gravedad es " + gravedad+"\n----------------------------";
    }
    
}
