package speedtesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

public class WebDriverTester {
	private String mAccount, mUser, mPassword;
	private WebDriver driver;
	private Map<String, Long> timings;
	private List<WebResult> results;
	private String mBrowser;
	private String mBrowserVersion;
	private String mBrowserVersionShort;
	private String date;
	private String pageName;
	private String pageURL;
	boolean isLogInPage = false;
	private static final int C_VERSION = 0;
	private static final int C_DATE = 1;
	private static final int C_PAGE_NAME = 2;
	private static final int C_PAGE_URL = 3;
	private static final int C_CACHE = 4;
	private static final int C_BROWSER = 5;
	private static final int C_BROWSER_VERSION = 6;
	private static final int C_ADD_ONS = 7;
	private static final int C_TIME = 8;
	private static final int C_RAM = 9;

	/**
	 * *****VARIABLES*****
	 * Make sure these are what you want
	 */
	public static final String VERSION = "3.1";
	public static final String BROWSER = "Firefox";
	public static final String ADD_ONS = "No";
	public static final double RAM = 6.0;
	/**These too**/
	public static final int REPEAT = 5; //must be more than 0
	public static final String FILEOUT = "C:/Users/Victor/Documents/Speed/Performance Testing Results Accio Data Example.xlsm";
	/**End of variables**/

	/**
	 * Initializes driver, defaults to firefox
	 * @param browser "firefox", "chrome", or "ie"
	 */
	public WebDriverTester(String browser){
		switch (browser) {
		case "Firefox" : 	mBrowser = "Firefox";
		break;
		case "Chrome" : 	mBrowser = "Chrome";
		break;
		case "IE" : 		mBrowser = "IE";
		break;
		default : 			mBrowser = "Firefox";
		break;
		}
		
		getLogin();

		//date field
		Calendar calendar = Calendar.getInstance();
		Integer month = calendar.get(Calendar.MONTH) + 1;
		Integer day = calendar.get(Calendar.DAY_OF_MONTH);
		Integer year = calendar.get(Calendar.YEAR);
		date = month.toString() + "/" + day.toString() + "/" + year.toString();

		results = new ArrayList<WebResult>();
		
		getNewDriver();
		Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
		mBrowserVersion = cap.getVersion();
		mBrowserVersionShort = mBrowserVersion.substring(0, mBrowserVersion.indexOf("."));
		driver.close();
	}

	/**
	 * Create new driver from mBrowser string
	 */
	private void getNewDriver() {
		switch (mBrowser) {
		case "Firefox" : 	driver = new FirefoxDriver();
		break;
		case "Chrome" : 	driver = new ChromeDriver();
		break;
		case "IE" : 		driver = new InternetExplorerDriver();
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

	public void run(String name, String url){
		pageURL = url;
		pageName = name;
		run();
	}
	/**
	 * Run REPEAT times from fresh window, then reload REPEAT times
	 * Output data to spreadsheet
	 * @param url URL of page to test
	 */
	public void run(){
		getNewDriver();
		checkIsLogIn();
		
		for(int x = 0; x < REPEAT; x++){
			logIn();
			getTimings("No");
			if(x != (REPEAT - 1)){
				driver.close();
				getNewDriver();
			}
		}

		for(int x = 0; x < REPEAT; x++){
			refreshAndWait();
			getTimings("Yes");
		}
		driver.close();
		writeToExcel();
	}	

	private void checkIsLogIn() {
		String[] urlArray = pageURL.split("\\.");
		if ( urlArray[urlArray.length - 1].equals("com") ||
			 urlArray[urlArray.length - 1].equals("com/") )
			isLogInPage = true;
		else
			isLogInPage = false;
	}

	private void refreshAndWait() {
		driver.navigate().refresh();
		waitForLoad(driver);		
	}

	@SuppressWarnings("unchecked")
	private void getTimings(String cache) {
		//get timings Map and calculate time
		timings = (Map<String, Long>) ((JavascriptExecutor)driver).executeScript("var performance = window.performance || {};" + 
				"var timings = performance.timing || {};" +
				"return timings;");
		long mTotal = (long) timings.get("loadEventEnd") - (long) timings.get("navigationStart");
		System.out.println(cache + " cache: " + mTotal);
		results.add(new WebResult(mTotal, cache));
	}

	private void logIn() {
		//load log-in page
		driver.get(pageURL);

		waitForLoad(driver);	

		if( !isLogInPage ){
			//log in
			driver.findElement(By.id("account")).sendKeys(mAccount);
			driver.findElement(By.id("userid")).sendKeys(mUser);
			driver.findElement(By.id("password")).sendKeys(mPassword+"\n");

			waitForLoad(driver);
		}
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

	private void createOutputFile() {
		try {

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.getSheetAt(1);

			HSSFRow rowhead = sheet.createRow((short)0);
			rowhead.createCell(C_VERSION).setCellValue("Version");
			rowhead.createCell(C_DATE).setCellValue("Date");
			rowhead.createCell(C_PAGE_NAME).setCellValue("Page Name");
			rowhead.createCell(C_PAGE_URL).setCellValue("URL");
			rowhead.createCell(C_CACHE).setCellValue("Cache");
			rowhead.createCell(C_BROWSER).setCellValue("Browser");
			rowhead.createCell(C_BROWSER_VERSION).setCellValue("Browser Version");
			rowhead.createCell(C_ADD_ONS).setCellValue("Add-ons");
			rowhead.createCell(C_TIME).setCellValue("Time(s)");
			rowhead.createCell(C_RAM).setCellValue("RAM(GB)");

			FileOutputStream fileOut = new FileOutputStream(new File(FILEOUT));
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeToExcel(){
		try {
			if( !(new File(FILEOUT).exists()) )
				createOutputFile();

			FileInputStream input_document = new FileInputStream(new File(FILEOUT));
			XSSFWorkbook workbook = new XSSFWorkbook(input_document); 
			XSSFSheet sheet = workbook.getSheetAt(1);

			for (WebResult webRes : results){
				int row = sheet.getLastRowNum() + 1;
				XSSFRow rowhead = sheet.createRow((short)row);
				rowhead.createCell(C_VERSION).setCellValue(VERSION);
				rowhead.createCell(C_DATE).setCellValue(date);
				rowhead.createCell(C_PAGE_NAME).setCellValue(pageName);
				rowhead.createCell(C_PAGE_URL).setCellValue(pageURL);
				rowhead.createCell(C_CACHE).setCellValue(webRes.cache);
				rowhead.createCell(C_BROWSER).setCellValue(BROWSER);
				rowhead.createCell(C_BROWSER_VERSION).setCellValue(mBrowserVersionShort);
				rowhead.createCell(C_ADD_ONS).setCellValue(ADD_ONS);
				rowhead.createCell(C_TIME).setCellValue(webRes.seconds);
				rowhead.createCell(C_RAM).setCellValue(RAM);
			}

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

	public static void main(String[] args) throws IOException{
		Logger.getRootLogger().setLevel(Level.OFF);

		WebDriverTester mTest = new WebDriverTester(BROWSER);
		mTest.run(Pages.SYSOP_HOME_NAME, Pages.SYSOP_HOME_URL);
	}

}
