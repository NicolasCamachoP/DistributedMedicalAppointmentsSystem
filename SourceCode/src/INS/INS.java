/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package INS;

import Cliente.Paciente;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nicolas Camacho & Sebastián Prado
 */
public class INS implements InterfaceINS
{
    public List<Caso> casos;
    public Patologia[] patologias;
    public Sintoma[] sintomas;
    public static final int EDAD_MINIMA = 20;
    public static final int EDAD_AGRAVANTE = 60;
    public GUIINS gui;

    public INS(GUIINS gui) 
    {
        this.gui = gui;
        casos = new ArrayList<>();
        casos = Collections.synchronizedList(casos);
        patologias = Patologia.values();
        sintomas = Sintoma.values();
        crearHiloRegistro();
    }
    
    private void crearHiloRegistro() {
        //Se crea u hilo que va a escuchar todos los mensajes que lleguen al socket leído en el archivo de configuración
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try 
                {
                    while(true)
                    {
                        Thread.sleep(20000);
                        escribirCasosReportados();
                    }
                } 
                catch (Exception e) 
                {
                    System.out.println(e.getMessage());
                }
            }
        });
        hilo.start();
    }
    
    public void escribirCasosReportados()
    {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            
            fichero = new FileWriter("CasosReportados.txt");
            pw = new PrintWriter(fichero);
            for (Caso c: casos) 
            {
                pw.println("Caso Reportado: "+ c.toString());
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
           try 
           {
                if (null != fichero)
                    fichero.close();
           } 
           catch (Exception e2) 
           {
              e2.printStackTrace();
           }
        }
    }
   
    @Override
    public int evaluarPaciente(Paciente p) throws RemoteException 
    {
        String str = "Llegó solicitud de servicio 'evaluarPaciente', respuesta: ";
        int puntaje = 0;
        int leves = 0;
        boolean sintomaGrave = false;
        boolean patologiaGrave = false;
        boolean patologiaOtra = false;
        System.out.println(p.sintomas);
        
        try {
            wait(1000);
        } catch (Exception e) {
        }
        
        for (Sintoma s: sintomas) 
        {
            if(s.gravedad.equals("leve") && p.sintomas.contains(s.nombre))
            {
                System.out.println(s.nombre);
                leves ++;
            }
            else if(s.gravedad.equals("grave") && p.sintomas.contains(s.nombre))
            {
                sintomaGrave = true;
            }
        }
        
        if(leves<4)
        {
            Caso c = new Caso(p, 0, "No Clasifica");
            casos.add(c);
            str += c.toString2();
            gui.escribirEstado(str);
            gui.agregarCaso(c);
            return 0;
        }
        
        if(leves == 4)
        {
            puntaje +=20;
        }
        
        if(p.edad >= EDAD_MINIMA)
        {
            puntaje+=20;
        }
        
        for (Patologia pato: patologias) 
        {
            if(p.patologias.contains(pato.nombre))
            {
                patologiaGrave = true;
            }
            else
            {
                patologiaOtra = true;
            }     
        }
        
        if(patologiaOtra)
        {
            puntaje +=20;
        }
        
        if(p.edad >= EDAD_AGRAVANTE)
        {
            puntaje += 10;
        }
        
        if(patologiaGrave)
        {
            puntaje +=10;
        }
        
        if(p.cantCirugias > 0)
        {
            puntaje +=10;
        }
        
        if(sintomaGrave)
        {
            puntaje += 10;
        }
        
        Caso c = new Caso(p, puntaje, (puntaje>=70)?"Grave":"Leve");
        
        casos.add(c);
        
        str += c.toString2();
        gui.escribirEstado(str);
        gui.agregarCaso(c);
        
        return puntaje;
    }
    
}
