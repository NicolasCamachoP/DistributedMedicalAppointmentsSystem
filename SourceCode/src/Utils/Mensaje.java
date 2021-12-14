package Utils;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class Mensaje implements Serializable
{
    public Tipo tipo; 
    public Object contenido;
    
    public Mensaje(Tipo t, Object c)
    {
        this.tipo = t;
        this.contenido = c;
    }
}
