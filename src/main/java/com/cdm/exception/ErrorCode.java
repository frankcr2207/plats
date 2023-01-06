package com.cdm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	
	BUSS_ERR_001("El registro ya fue atendido, actualice la lista."),
	BUSS_ERR_002("No se pudo conectar al repositorio SIJ."),
	BUSS_ERR_003("No se pudo enviar el archivo al repositorio SIJ"),
	
	//Excepciones especificas para multas
	BUSS_ERR_081("Los datos de la multa ya existen, verifique."),
	BUSS_ERR_082("La multa ya fue gestionada, actualice la lista.");
	
	private String mensaje;
	
}
