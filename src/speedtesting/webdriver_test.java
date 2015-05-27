package speedtesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class webdriver_test {
	public static void main(String[] args){
		Logger.getRootLogger().setLevel(Level.OFF);
		WebDriver driver = new FirefoxDriver();
		for(int x = 0; x < 3; x++){
			driver.get("https://demoh.acciodata.com/");
			waitForLoad(driver);

			Map mapTime = (Map) ((JavascriptExecutor)driver).executeScript("var performance = window.performance || {};" + 
					"var timings = performance.timing || {};" +
					"return timings;");
			long mTotal = (long) mapTime.get("loadEventEnd") - (long) mapTime.get("navigationStart");
			System.out.println("No cache: " + mTotal);
			
			if(x != 2){
				driver.close();
				driver = new FirefoxDriver();
			}
		}
		
		for(int x = 0; x < 3; x++){
			driver.navigate().refresh();
			waitForLoad(driver);

			Map mapTime = (Map) ((JavascriptExecutor)driver).executeScript("var performance = window.performance || {};" + 
					"var timings = performance.timing || {};" +
					"return timings;");
			long mTotal = (long) mapTime.get("loadEventEnd") - (long) mapTime.get("navigationStart");
			System.out.println("Cache: " + mTotal);
		}
		driver.close();
		writeToExcel();
	}
	
	

	private static void waitForLoad(WebDriver driver) {
		ExpectedCondition<Boolean> pageLoadCondition = new
				ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(pageLoadCondition);
	}
	
	private static void writeToExcel(){
		try {
            String filename = "C:/Users/Victor/Documents/Speed/Data.xls" ;
            FileInputStream input_document = new FileInputStream(new File(filename));
            HSSFWorkbook workbook = new HSSFWorkbook(input_document); 
            HSSFSheet sheet = workbook.getSheetAt(0);

//            HSSFRow rowhead = sheet.createRow((short)0);
//            rowhead.createCell(0).setCellValue("Page URL");
//            rowhead.createCell(1).setCellValue("Page Name");
//            rowhead.createCell(2).setCellValue("Browser");
//            rowhead.createCell(3).setCellValue("Time(s)");
            
            int row = sheet.getLastRowNum() + 1;
            HSSFRow rowhead = sheet.createRow((short)row);
            rowhead.createCell(0).setCellValue("https://demoh.acciodata.com");
            rowhead.createCell(1).setCellValue("Log-in Page");
            rowhead.createCell(2).setCellValue("Firefox");
            rowhead.createCell(3).setCellValue(".59");

            input_document.close();
            FileOutputStream fileOut = new FileOutputStream(new File(filename));
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            System.out.println("Your excel file has been generated!");

        } catch ( Exception ex ) {
            System.out.println(ex);
        }
	}

}
