package Infrastructure;

import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ReadData {
	
	/**
	 * getCellValue() function Retrieves the value of a specific cell from an Excel file.
	*/
	 public static String getCellValue(String filePath, String sheetName, int rowNum, int cellNum) {
	        try (FileInputStream fis = new FileInputStream(filePath);
	             Workbook workbook = WorkbookFactory.create(fis)) {
	             
	            Sheet sheet = workbook.getSheet(sheetName);
	            Row row = sheet.getRow(rowNum);
	            Cell cell = row.getCell(cellNum);
	            return cell.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
}
