package com.cdm.utils;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.entities.Calculo;
import com.cdm.entities.CalculoCriteria;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class WebServiceCalculo {
	@RequestMapping(value = "/restaFechas", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Calculo restaFechas(@RequestBody CalculoCriteria calc) {
		
		Calculo calculo= new Calculo();
		
		int calcAnio = 0, calcMes = 0, calcDia = 0;
		
		calcAnio = calc.getAnio1() - calc.getAnio2();
		calcMes = calc.getMes1() - calc.getMes2();
		calcDia = calc.getDia1() - calc.getDia2();
		
		if(calcMes < calc.getMes2()) {
			calcMes = calcMes + 12;
			calcAnio = calcAnio - 1;
		}
		if(calcDia < calc.getDia2()) {
			calcDia = calcDia + 30;
			calcMes = calcMes - 1;
		}
		
		calculo.setAnio(calcAnio);
		calculo.setMes(calcMes);
		calculo.setDia(calcDia);
		return calculo;
    }
	
	@RequestMapping(value = "/calculoFecha", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Calculo calculoFecha(@RequestBody Calculo calc) {
		
		Calculo calculo= new Calculo();
		String[] parts = calc.getFecha().split("-");

		int calcAnio = 0, calcMes = 0, calcDia = 0;
		int opModDia = opModDias(Integer.parseInt(parts[1]),Integer.parseInt(parts[2]));
		
		if(calc.getOperacion().equals("1")) {
			calcAnio = calc.getAnio() + Integer.parseInt(parts[0]);
			calcMes = calc.getMes() + Integer.parseInt(parts[1]);
			calcDia = calc.getDia() + Integer.parseInt(parts[2]);
			//System.out.println("MOD DIA: " + calcDia % opModDia);
			if((calcDia % opModDia) > 0) {
				calcMes = calcMes + (calcDia / 30);
				calcDia = calcDia % opModDia;
			}
			//System.out.println("MOD MES: " + calcMes % 30);
			if((calcMes % 12) > 0) {
				calcAnio = calcAnio + (calcMes / 12);
				calcMes = calcMes % 12;
			}
		}
		else if(calc.getOperacion().equals("0")) {
			calcAnio = calc.getAnio() + Integer.parseInt(parts[0]);
			calcMes = calc.getMes() + Integer.parseInt(parts[1]);
			calcDia = calc.getDia() + Integer.parseInt(parts[2]);
		}
		/*System.out.println("INICIO: " + calc.getFecha());
		System.out.println("ANIO: " + calc.getAnio());
		System.out.println("MES: " + calc.getMes());
		System.out.println("DIA: " + calc.getDia());
		System.out.println("RESULTADO ANIO: " + calcAnio);
		System.out.println("RESULTADO MES: " + calcMes);
		System.out.println("RESULTADO DIA: " + calcDia);*/
		
		String addDia, addMes;
		if(calcDia < 10) addDia = "0" + calcDia;
		else addDia = "" + calcDia;
		
		if(calcMes < 10) addMes = "0" + calcMes;
		else addMes = "" + calcMes;
			
		calculo.setFecha(addDia + "-" + addMes + "-" + calcAnio);
		//System.out.println("FINAL: " + addDia + "-" + addMes + "-" + calcAnio);
		return calculo;
    }
	
	public int opModDias(int mes, int dia) {
		int dias = 0;
		
		if(mes == 4 || mes == 6 || mes == 9 || mes == 11) 
			dias = 30;
		else if(mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 10 || mes == 12)
			dias = 31;
		else if(mes == 2 && dia == 29)
			dias = 29;
		else if(mes == 2 && dia == 28)
			dias = 28;
		
		return dias;
	}
}
