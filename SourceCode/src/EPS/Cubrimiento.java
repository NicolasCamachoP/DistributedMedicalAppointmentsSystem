/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EPS;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public enum Cubrimiento 
{
    basico("basico",70), prepagada("prepagada",80), complementario("complementario",100);
            
    public String nombre;
    public int nivelMax;
    
    private Cubrimiento(String nombre, int nivelMax)
    {
        this.nombre = nombre;
        this.nivelMax = nivelMax;
    }
}
