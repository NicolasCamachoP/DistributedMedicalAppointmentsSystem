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
public enum Sintoma 
{
    fiebre("fiebre","leve"), tos("tos","leve"), cansancio("cansancio","leve"), dolor("dolor","leve")
    
    ,dificultad_para_respirar("falta de aire y dificultad para respirar","grave"), insuficiencia_pulmonar("insuficiencia_pulmonar","grave"), shock_septico("shock_septico","grave"), 
    falla_multiorganica("falla_multiorganica","grave");
    
    private Sintoma(String nombre, String gravedad)
    {
        this.gravedad = gravedad;
        this.nombre = nombre;
    }
    
    public String gravedad;
    public String nombre;
}
