/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IPS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class AgendaDia 
{
    public String fecha;
    public List<Cita> citas;
    
    public AgendaDia(String fecha)
    {
        citas = new ArrayList<Cita>();
        citas = Collections.synchronizedList(citas);
        this.fecha = fecha;
    }
    
    public boolean agendarCita(Cita c)
    {
        if(citas.size() <10)
        {
            citas.add(c);
            return true;
        }
        return false;
    }
    
    public Cita agendarCitaUrgente(Cita c)
    {
        int menor = 101;
        Cita menorC = null;
        
        for (Cita cita : citas) 
        {
            if(cita.puntaje<menor)
            {
                menor = cita.puntaje;
                menorC = cita;
            }
        }
        
        if(menor>=90)
        {
            return null;
        }
        c.fecha = menorC.fecha;
        citas.remove(menorC);
        citas.add(c);
        
        return menorC;
        
    }
    
}
