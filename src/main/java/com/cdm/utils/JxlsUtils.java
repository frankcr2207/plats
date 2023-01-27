package com.cdm.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

public class JxlsUtils {
    
	public void generarDocumento(OutputStream outStream, String templateName, Map<String, Object> data) {

		String pathTemplateName = ("/reports/").concat(templateName).concat(".xls");
		try(InputStream input = this.getClass().getResourceAsStream(pathTemplateName)) {//1
		
            Context context = new Context();
            
            for (Entry<String, Object> element : data.entrySet()) { // 2
            	context.putVar(element.getKey(), element.getValue());
			}
            
            JxlsHelper.getInstance().processTemplate(input, outStream, context); // 3

		} catch (Exception exception) {

		} finally {
			closeAndFlushOutput(outStream); // 4
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
