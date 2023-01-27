package com.cdm.service1.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.stereotype.Service;

import com.cdm.service1.ExcelService;
import com.cdm.utils.Constantes;

@Service
public class ExcelServiceImpl implements ExcelService {

	@Override
	public void generarDocumento(OutputStream outStream, String templateName, Map<String, Object> data) {
		String pathTemplateName = ("/reports/").concat(templateName).concat(Constantes.EXTENSION_EXCEL);
		try(InputStream input = this.getClass().getResourceAsStream(pathTemplateName)) {
		
            Context context = new Context();
            for (Entry<String, Object> element : data.entrySet()) { 
            	context.putVar(element.getKey(), element.getValue());
			}
            JxlsHelper.getInstance().processTemplate(input, outStream, context);

		} catch (Exception exception) {

		} finally {
			closeAndFlushOutput(outStream);
		}
	}
	
	private void closeAndFlushOutput(OutputStream outStream) {
		try {
			outStream.flush();
			outStream.close();
		} catch (IOException exception) {

		}
	}

}
