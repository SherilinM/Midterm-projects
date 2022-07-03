package com.module.Application.classes;

import java.time.LocalDate;
import java.time.Period;

public class Utils {

    /** Metodo para calcular la cantidad entera de años entre dos fechas **/
    public static Integer calculateYears(LocalDate date){
        Period age = Period.between(date, LocalDate.now());
        return age.getYears();
    }

    /** Metodo para calcular la cantidad entera de meses entre dos fechas **/
    public static Integer calculateMonths(LocalDate lastUpdate){
        Period age = Period.between(lastUpdate, LocalDate.now());
        return age.getMonths();
    }
}