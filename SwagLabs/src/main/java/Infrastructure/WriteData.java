package Infrastructure;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class WriteData {
	
	public static String actualResult;
	
	/*
	 * writeResult() function is write the Result into the Excel file.
	 */
	public static void writeResult(String status, String testcaseFilepath, String sheetName, String testcaseName, String actualResult, String executionTime) {
		
		try {
			FileInputStream fileInput = new FileInputStream(testcaseFilepath);
			Workbook workBook = new XSSFWorkbook(fileInput);
			Sheet sheet = workBook.getSheet(sheetName);
			fileInput.close();
			
			if(sheet == null) {
				System.out.println("Sheet" + sheetName + "does not exists.");
				workBook.close();
				return;
			}
			
			boolean isUpdated = false;
			
			for (Row row : sheet) {
				Cell scriptTestcaseCell = row.getCell(5);
				
			if(scriptTestcaseCell != null && scriptTestcaseCell.getCellType() == CellType.STRING) {
				if(scriptTestcaseCell.getStringCellValue().trim().equalsIgnoreCase(testcaseName)) {
					
					// Write Execution Date
					Cell executionDateCell = row.getCell(2);
					if(executionDateCell == null) executionDateCell = row.createCell(2);
					executionDateCell.setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
					
					// Write Script Result
					Cell resultCell = row.getCell(3);
					if(resultCell == null) resultCell = row.createCell(3);
					resultCell.setCellValue(status);
					
					// Write Current execution time of script
					Cell timeCell = row.getCell(4);
					if(timeCell == null) timeCell = row.createCell(4);
					timeCell.setCellValue(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
					
					// Write Actual Result
					Cell actualResultCell = row.getCell(6);
					if(actualResultCell == null) actualResultCell = row.createCell(6);
					actualResultCell.setCellValue(actualResult);
					
					// Write Execution Time
					Cell totalTimeCell = row.getCell(7);
					if(totalTimeCell == null) totalTimeCell = row.createCell(7);
					totalTimeCell.setCellValue(executionTime);
					
					isUpdated = true;
					break;
				}
			}		
		}
			if(isUpdated) {
				FileOutputStream outputStream = new FileOutputStream(testcaseFilepath);
				workBook.write(outputStream);
				outputStream.close();
				System.out.println("Updated for testcase :" + testcaseName);
			}else {
				System.out.println("TestCase is not found");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
