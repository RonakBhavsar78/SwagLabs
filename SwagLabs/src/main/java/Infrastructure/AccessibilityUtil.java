package Infrastructure;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.microsoft.playwright.Page;

public class AccessibilityUtil {
	
	/**
	 * Scanning the given web page using axe-core for accessibility violations,
	 * then generates a human-readable accessibility report (.txt) containing:
	 * - Rule name
	 * - Description and impact
	 * - Suggested fix based on rule ID
	 * - Affected HTML elements
	 *
	 * @param page  :  Playwright page object representing the loaded web page.
	 * @param pageName :  Name of the output file to save the report.
	 */
	public static void handleAccessibility(Page page, String pageName) throws IOException {
		
		String axeScript = Files.readString(Paths.get("src/main/resources/axe.min.js"));
	    page.evaluate(axeScript);
	     
        String jsonString = (String) page.evaluate("async () => JSON.stringify(await axe.run())");
        JSONObject result = new JSONObject(jsonString);
        JSONArray violations = result.getJSONArray("violations");

        Map<String, String> fixMap = getFixAdviceMap();

        // Write to .txt file (per page)
        writeTxtReport(pageName, violations, fixMap);

        //Append to central Excel file
        writeExcelReport(pageName, violations, fixMap, "TestResult/sauceaccesiblity.xlsx");

        System.out.println("Accessibility reports generated for: " + pageName);
		
	}
	
	
	private static void writeTxtReport(String pageName, JSONArray violations, Map<String, String> fixMap) throws IOException {
        Path logPath = Paths.get("TestResult/Accesiblitylog");
        Files.createDirectories(logPath);

        try (FileWriter writer = new FileWriter(logPath.resolve(pageName + ".txt").toString())) {
            writer.write("Accessibility Report for " + pageName + "\n");
            writer.write("Total Violations: " + violations.length() + "\n\n");

            for (int i = 0; i < violations.length(); i++) {
                JSONObject v = violations.getJSONObject(i);
                writer.write("--------------------------------------------------\n");
                writer.write("Issue " + (i + 1) + ": " + v.getString("help") + "\n");
                writer.write("Description: " + v.getString("description") + "\n");
                writer.write("Impact: " + v.getString("impact") + "\n");
                writer.write("More Info: " + v.getString("helpUrl") + "\n");
                writer.write("Fix: " + fixMap.getOrDefault(v.getString("id"), "See documentation.") + "\n");

                JSONArray nodes = v.getJSONArray("nodes");
                writer.write("Affected Elements:\n");
                for (int j = 0; j < nodes.length(); j++) {
                    JSONArray targets = nodes.getJSONObject(j).getJSONArray("target");
                    writer.write(" - " + targets.join(", ") + "\n");
                }
                writer.write("\n");
            }
            
            // Uncomment the section below to include the full JSON output in the report:
            /*
            writer.write("--------------------------------------------------\n");
            writer.write("Full JSON Output:\n");
            writer.write(json.toString(2)); */
        }
    }
	
	
	private static void writeExcelReport(String pageName, JSONArray violations, Map<String, String> fixMap, String excelPath) throws IOException {
        Workbook workbook;
        Sheet sheet;
        Path path = Paths.get(excelPath);

        if (Files.exists(path)) {
            workbook = WorkbookFactory.create(Files.newInputStream(path));
            sheet = workbook.getSheetAt(0);
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Accessibility Report");
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            String[] headers = {"Page", "Issue", "Description", "Impact", "Help URL", "Fix", "Elements"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = sheet.getLastRowNum() + 1;

        for (int i = 0; i < violations.length(); i++) {
            JSONObject v = violations.getJSONObject(i);
            String ruleId = v.getString("id");
            Set<String> targets = new HashSet<>();
            JSONArray nodes = v.getJSONArray("nodes");
            for (int j = 0; j < nodes.length(); j++) {
                JSONArray t = nodes.getJSONObject(j).getJSONArray("target");
                for (int k = 0; k < t.length(); k++) targets.add(t.getString(k));
            }

            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(pageName);
            row.createCell(1).setCellValue(v.getString("help"));
            row.createCell(2).setCellValue(v.getString("description"));
            row.createCell(3).setCellValue(v.getString("impact"));
            row.createCell(4).setCellValue(v.getString("helpUrl"));
            row.createCell(5).setCellValue(fixMap.getOrDefault(ruleId, "See documentation."));
            row.createCell(6).setCellValue(String.join(", ", targets));
        }

        try (FileOutputStream out = new FileOutputStream(excelPath)) {
            workbook.write(out);
        }
        workbook.close();
    }
	
	/**
	 * This map is used to provide developers with meaningful guidance on how to resolve
	 * accessibility issues detected by axe during automated scans.
	 * Each key in the map is a rule ID (e.g., "color-contrast", "label"), and the value
	 * is a short, human-readable fix suggestion.
	 *
	 * @return A map containing rule IDs and their corresponding fix recommendations.
	 */
	private static Map<String, String> getFixAdviceMap() {
		
	    Map<String, String> map  = new HashMap<>();
	    map.put("landmark-one-main", "Wrap the main content of the page in a <main> tag.");
	    map.put("page-has-heading-one", "Include a single <h1> element on the page for screen reader clarity.");
	    map.put("color-contrast", "Ensure sufficient contrast between text and background (min 4.5:1).");
	    map.put("label", "Add a <label> for every input or form element.");
	    map.put("region", "Use landmark regions like <main>, <nav>, <header> to structure content.");
	    map.put("image-alt", "Include descriptive alt text for all images.");
	    map.put("button-name", "Ensure all buttons have accessible names using text, alt, or aria-label.");
	    return map ;
	}

}
