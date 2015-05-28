package speedtesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

public class WebDriverTester {
	private String mAccount, mUser, mPassword;
	private WebDriver driver;
	private Map timings;
	private String mBrowser;
	
	public static final String BROWSER = "firefox";
	public static final int REPEAT = 3;
	public static final String FILEOUT = "C:/Users/Victor/Documents/Speed/Data.xls";
	
	/**
	 * Initializes driver, defaults to firefox
	 * @param browser "firefox", "chrome", or "ie"
	 */
	public WebDriverTester(String browser){
		switch (browser) {
		case "firefox" : 	mBrowser = "firefox";
							break;
		case "chrome" : 	mBrowser = "chrome";
							break;
		case "ie" : 		mBrowser = "ie";
							break;
		default : 			mBrowser = "firefox";
							break;
		}
		getNewDriver();
		getLogin();
	}
	
	/**
	 * Create new driver from mBrowser string
	 */
	private void getNewDriver() {
		switch (mBrowser) {
		case "firefox" : 	driver = new FirefoxDriver();
							break;
		case "chrome" : 	driver = new ChromeDriver();
							break;
		case "ie" : 		driver = new InternetExplorerDriver();
							break;
		default : 			driver = new FirefoxDriver();
							break;
		}
	}
	
	/**
	 * Create new driver from param
	 * @param browser "firefox", "chrome", or "ie"
	 */
	private void getNewDriver(String browser) {
		switch (browser) {
		case "firefox" : 	driver = new FirefoxDriver();
							break;
		case "chrome" : 	driver = new ChromeDriver();
							break;
		case "ie" : 		driver = new InternetExplorerDriver();
							break;
		default : 			driver = new FirefoxDriver();
							break;
		}
	}

	/**
	 * Reads in login data from /src/login.txt
	 */
	private void getLogin() {
		try {
			List<String> loginList = 
					Files.readAllLines(Paths.get("src","login.txt"));
			mAccount = loginList.get(0);
			mUser = loginList.get(1);
			mPassword = loginList.get(2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Run REPEAT times from fresh window, then reload REPEAT times
	 * Output data to spreadsheet
	 * @param url URL of page to test
	 */
	public void run(String url){
		for(int x = 0; x < REPEAT; x++){
			//load log-in page
			driver.get(url);
			
			waitForLoad(driver);
			
			//log in
			driver.findElement(By.id("account")).sendKeys(mAccount);
			driver.findElement(By.id("userid")).sendKeys(mUser);
			driver.findElement(By.id("password")).sendKeys(mPassword+"\n");
			
			waitForLoad(driver);
			
			//get timings Map and calculate time
			timings = (Map) ((JavascriptExecutor)driver).executeScript("var performance = window.performance || {};" + 
					"var timings = performance.timing || {};" +
					"return timings;");
			long mTotal = (long) timings.get("loadEventEnd") - (long) timings.get("navigationStart");
			System.out.println("No cache: " + mTotal);
			
			if(x != (REPEAT - 1)){
				driver.close();
				driver = new FirefoxDriver();
			}
		}
		
		for(int x = 0; x < 3; x++){
			driver.navigate().refresh();
			waitForLoad(driver);

			timings = (Map) ((JavascriptExecutor)driver).executeScript("var performance = window.performance || {};" + 
					"var timings = performance.timing || {};" +
					"return timings;");
			long mTotal = (long) timings.get("loadEventEnd") - (long) timings.get("navigationStart");
			System.out.println("Cache: " + mTotal);
		}
		driver.close();
		writeToExcel();
	}	

	private void waitForLoad(WebDriver driver) {
		ExpectedCondition<Boolean> pageLoadCondition = new
				ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 90);
		wait.until(pageLoadCondition);
	}
	
	private void writeToExcel(){
		try {
            FileInputStream input_document = new FileInputStream(new File(FILEOUT));
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
            FileOutputStream fileOut = new FileOutputStream(new File(FILEOUT));
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            System.out.println("Your excel file has been generated!");

        } catch ( Exception ex ) {
            System.out.println(ex);
        }
	}
	
	public static void main(String[] args){
		Logger.getRootLogger().setLevel(Level.OFF);
		WebDriverTester mTest = new WebDriverTester(BROWSER);
		mTest.run("https://demoh.acciodata.com/sysops/sysop_home5.html");
	}

}
