/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class Paciente implements Serializable
{
    public Date tiempo;
    public int edad;
    public int cantCirugias;
    public String nombre;
    public String documento;
    public String eps;
    public ArrayList<String> sintomas;
    public ArrayList<String> patologias;

    public Paciente(Date tiempo, int edad, String nombre, String documento, String eps, ArrayList<String> sintomas, ArrayList<String> patologias, int cantCirugias) {
        this.tiempo = tiempo;
        this.edad = edad;
        this.nombre = nombre;
        this.documento = documento;
        this.eps = eps;
        this.sintomas = sintomas;
        this.patologias = patologias;
        this.cantCirugias = cantCirugias;
    }

    
    
    
    
    
}
