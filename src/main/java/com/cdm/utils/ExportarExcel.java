package com.cdm.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import com.cdm.domain.vo.RequestActaVO;
import com.cdm.service.external.vo.ResponseAudienciaActaVO;

@Service
public class ExportarExcel {
	
	 private final String OPEN_API="https://api.publicapis.org/entries";

	    @Value("${jxls.template.path}")
	    private String templatePath;

	    @Value("${jxls.template.output.path}")
	    private String outputPath;

	    private static final String templateName="api_data.xls";

	    public ResponseData getFetchDataFromOpenAPI(){
	    	RestTemplate restTemplate = new RestTemplate();
	        return restTemplate.getForEntity(OPEN_API, ResponseData.class).getBody();
	     }

	    /*public boolean generateXls(ResponseData responseData,String templateName,String fileName) throws IOException {
	        InputStream in =null;
	        OutputStream out =null;
	        try{
	            if(!ObjectUtils.isEmpty(responseData)){

	                out = new FileOutputStream(fileName);
	                in =  new FileInputStream(ResourceUtils.getFile(templatePath+templateName));

	                Map<String,Object> map = new HashMap<>();
	                map.put("apiData", responseData.getEntries());
	                map.put("count", responseData.getCount());

	                XLXUtils.exportExcel(in,out,map);
	                return true;
	            }
	        } catch (IOException e) {
	            throw e;
	        } finally {
	            try {
	                if (out != null) {
	                    out.flush();
	                    out.close();
	                }
	                if (in != null) {
	                    in.close();
	                }
	            } catch (IOException e) {
	                throw e;
	            }
	        }
	        return false;
	    }



	    public boolean downloadXls(ResponseData responseData, HttpServletResponse httpServletResponse,String templateName) throws IOException {
	        InputStream in =null;
	        OutputStream out =null;
	        try{
	            if(!ObjectUtils.isEmpty(responseData)){
	                httpServletResponse.reset();
	                httpServletResponse.addHeader("Content-disposition", "attachment;filename="+"api_data_"+ new Random().nextInt(100)+".xls");
	                httpServletResponse.setContentType("application/octet-stream;charset=UTF-8");

	                out = httpServletResponse.getOutputStream();
	                in =  new FileInputStream(ResourceUtils.getFile(templatePath+templateName));

	                Map<String,Object> map = new HashMap<>();
	                map.put("apiData", responseData.getEntries());
	                map.put("count", responseData.getCount());

	                XLXUtils.exportExcel(in,out,map);
	                return true;
	            }
	        } catch (IOException e) {
	            throw e;
	        } finally {
	            try {
	                if (out != null) {
	                    out.flush();
	                    out.close();
	                }
	                if (in != null) {
	                    in.close();
	                }
	            } catch (IOException e) {
	                throw e;
	            }
	        }
	        return false;
	    }*/


	    public ByteArrayOutputStream downloadXls(String templateName, List<RequestActaVO> responseAudienciaActaVO, String usuario) throws IOException {
	        InputStream in =null;
	        ByteArrayOutputStream out =null;
	        try{
	            if(!ObjectUtils.isEmpty(responseAudienciaActaVO)){


	                out = new ByteArrayOutputStream();
	                in =  new FileInputStream(ResourceUtils.getFile(templatePath+templateName));

	                Map<String,Object> map = new HashMap<>();
	                map.put("actas", responseAudienciaActaVO);
	                map.put("usuario", usuario);

	                XLXUtils.exportExcel(in,out,map);

	            }
	        } catch (IOException e) {

	            System.out.println(e.getMessage());
	        } finally {
	            try {
	                if (out != null) {
	                    out.flush();
	                    out.close();
	                }
	                if (in != null) {
	                    in.close();
	                }
	            } catch (IOException e) {

	                System.out.println(e.getMessage());
	            }
	        }
	        return out;
	    }
	
}
