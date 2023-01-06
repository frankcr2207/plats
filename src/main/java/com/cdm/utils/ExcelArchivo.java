package com.cdm.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cdm.entities.Servicio;
import com.cdm.entities.Usuario;

public class ExcelArchivo {
	public static ByteArrayInputStream listaUsuarios(List<Usuario> usuarios) {
		try(Workbook workbook = new XSSFWorkbook()){
			Sheet sheet = workbook.createSheet("electores_sin_voto");
			
			Row row = sheet.createRow(0);
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
	        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        // Creating header
	        Cell cell = row.createCell(0);
	        cell.setCellValue("DNI");
	        cell.setCellStyle(headerCellStyle);
	        
	        cell = row.createCell(1);
	        cell.setCellValue("NOMBRES COMPLETOS");
	        cell.setCellStyle(headerCellStyle);
	        
	        // Creating data rows for each customer
	        for(int i = 0; i < usuarios.size(); i++) {
	        	Row dataRow = sheet.createRow(i + 1);
	        	dataRow.createCell(0).setCellValue(usuarios.get(i).getDni());
	        	dataRow.createCell(1).setCellValue(usuarios.get(i).getNombres());
	        }
	
	        // Making size of column auto resize to fit with data
	        sheet.autoSizeColumn(0);
	        sheet.autoSizeColumn(1);
	        sheet.autoSizeColumn(2);
	        sheet.autoSizeColumn(3);
	        
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static ByteArrayInputStream listaServicio(List<Servicio> registros) {
		try(Workbook workbook = new XSSFWorkbook()){
			Sheet sheet = workbook.createSheet("Registros");
			
			Row row = sheet.createRow(0);
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
	        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        // Creating header
	        Cell cell = row.createCell(0);
	        cell.setCellValue("REGISTRO");
	        cell.setCellStyle(headerCellStyle);    
	        cell = row.createCell(1);
	        cell.setCellValue("D.N.I.");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(2);
	        cell.setCellValue("NOMBRES");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(3);
	        cell.setCellValue("APELLIDOS");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(4);
	        cell.setCellValue("EXPEDIENTE");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(5);
	        cell.setCellValue("SEDE");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(6);
	        cell.setCellValue("INSTANCIA");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(7);
	        cell.setCellValue("PEDIDO");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(8);
	        cell.setCellValue("ESTADO");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(9);
	        cell.setCellValue("FECHA ATENCION");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(10);
	        cell.setCellValue("RESPUESTA");
	        cell.setCellStyle(headerCellStyle);
	        
	        // Creating data rows for each customer
	        for(int i = 0; i < registros.size(); i++) {
	        	Row dataRow = sheet.createRow(i + 1);
	        	dataRow.createCell(0).setCellValue(registros.get(i).getRegistro());
	        	dataRow.createCell(1).setCellValue(registros.get(i).getS_dni());
	        	dataRow.createCell(2).setCellValue(registros.get(i).getS_nombres());
	        	dataRow.createCell(3).setCellValue(registros.get(i).getS_apellidos());
	        	dataRow.createCell(4).setCellValue(registros.get(i).getN_expediente());
	        	dataRow.createCell(5).setCellValue(registros.get(i).getSede());
	        	dataRow.createCell(6).setCellValue(registros.get(i).getInstancia());
	        	dataRow.createCell(7).setCellValue(registros.get(i).getS_observacion());
	        	if(registros.get(i).getS_estado().equals("A"))
	        		dataRow.createCell(8).setCellValue("ATENDIDO");
	        	else
	        		dataRow.createCell(8).setCellValue("PENDIENTE");
	        	dataRow.createCell(9).setCellValue(registros.get(i).getAtencion());
	        	dataRow.createCell(10).setCellValue(registros.get(i).getS_respuesta());
	        }
	
	        // Making size of column auto resize to fit with data
	        sheet.autoSizeColumn(0);
	        sheet.autoSizeColumn(1);
	        sheet.autoSizeColumn(2);
	        sheet.autoSizeColumn(3);
	        sheet.autoSizeColumn(4);
	        sheet.autoSizeColumn(5);
	        sheet.autoSizeColumn(6);
	        sheet.autoSizeColumn(7);
	        sheet.autoSizeColumn(8);
	        sheet.autoSizeColumn(9);
	        sheet.autoSizeColumn(10);
	        
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
