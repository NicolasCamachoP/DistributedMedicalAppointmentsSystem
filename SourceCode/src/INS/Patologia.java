/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package INS;

/**
 *
* @author Nicolas Camacho & Sebastián Prado
 */
public enum Patologia 
{
    asma("asma"),diabetes("diabetes"),cancer("cancer"),neumonia("neumonia"),bronquitis("bronquitis");
    
    public String nombre;
    
    private Patologia(String nombre)
    {
        this.nombre = nombre;
    }
}
