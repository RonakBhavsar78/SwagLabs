package Infrastructure;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.microsoft.playwright.Page;

public class VisualTestUtil {
	
	/**
     * Captures screenshots and performs visual comparison.
     *
     * @param page  :     Playwright page instance
     * @param screenName : Folder name
     * @param fileName :  Base name for screenshots
     */
    public static void runVisualTest(Page page, String screenName, String fileName) throws IOException {
    	
    	Path folder = Paths.get("TestResult", "Screenshots", screenName);

        Path baselinePath = folder.resolve("Baseline_" + fileName + ".png");
        Path actualPath = folder.resolve("Actual_" + fileName + ".png");
        Path diffPath = folder.resolve("Diff_" + fileName + ".png");
        
        String excelPath = "TestResult/saucevisual.xlsx";

        // First run to save baseline image
        if (!Files.exists(baselinePath)) {
            System.out.println("No baseline found. Saving baseline image...");
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(baselinePath)
                    .setFullPage(true));
            writeVisualResultToExcel(screenName, "Passed", "Baseline image saved sucessfully.", excelPath);
            return;
        }

        // Second time run to store actual image
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(actualPath)
                .setFullPage(true));

        // Compare
        boolean isMatch = compareImages(baselinePath, actualPath, diffPath);

        if (!isMatch) {
            System.out.println("Visual difference found! See: " + diffPath);
            writeVisualResultToExcel(screenName, "Failed", "Difference is found while comparing baseline and actual screenshots", excelPath);

        } else {
            System.out.println("No difference found in both images.");
            Files.deleteIfExists(diffPath);
            writeVisualResultToExcel(screenName, "Passed", "No difference found while comparing baseline and actual screenshots", excelPath);

        }
    }

    /**
     * Compares two images and creates a diff image if mismatched.
     */
    private static boolean compareImages(Path baselinePath, Path actualPath, Path diffPath) throws IOException {
    	
        BufferedImage img1 = ImageIO.read(baselinePath.toFile());
        BufferedImage img2 = ImageIO.read(actualPath.toFile());

        int width = Math.max(img1.getWidth(), img2.getWidth());
        int height = Math.max(img1.getHeight(), img2.getHeight());

        BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        boolean same = true;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = getPixelSafe(img1, x, y, Color.BLACK);
                int rgb2 = getPixelSafe(img2, x, y, Color.BLACK);

                if (rgb1 != rgb2) {
                	// show differences in screenshots with red color.
                    diff.setRGB(x, y, Color.RED.getRGB()); 
                    same = false;
                } else {
                	// keep matching area from baseline
                    diff.setRGB(x, y, rgb1); 
                }
            }
        }

        if (!same) {
            ImageIO.write(diff, "png", diffPath.toFile());
        }

        return same;
    }	

    private static int getPixelSafe(BufferedImage img, int x, int y, Color fallback) {
        if (x >= img.getWidth() || y >= img.getHeight()) {
            return fallback.getRGB();
        }
        return img.getRGB(x, y);
    }
    
    /**
     * writeVisualResultToExcel() function write the result in excel sheet.
     */
    private static void writeVisualResultToExcel(String screenName, String status, String message, String excelPath) throws IOException {
        Workbook workbook;
        Sheet sheet;
        Path path = Paths.get(excelPath);

        if (Files.exists(path)) {
            workbook = WorkbookFactory.create(Files.newInputStream(path));
            sheet = workbook.getSheetAt(0);
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Visual Test Results");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Scenario");      
            header.createCell(1).setCellValue("Script Result");  
            header.createCell(2).setCellValue("Actual Result");  
            header.createCell(3).setCellValue("Script Remarks"); 
        }
        int rowNum = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(screenName);      
        row.createCell(1).setCellValue(status);       
        row.createCell(2).setCellValue(message);      
        row.createCell(3).setCellValue(""); 

        try (FileOutputStream out = new FileOutputStream(excelPath)) {
            workbook.write(out);
        }
        workbook.close();
    }


} 