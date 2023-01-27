package com.cdm.service1;

import java.io.OutputStream;
import java.util.Map;

public interface ExcelService {

	void generarDocumento(OutputStream outStream, String templateName, Map<String, Object> data);
	
}
