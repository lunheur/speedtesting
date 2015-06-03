package speedtesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
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
	private static final String FIREFOX = "Firefox";
	private static final String CHROME = "Chrome";
	private static final String IE = "IE";
	private static final String NO_ADD_ONS = "No";

	/**
	 * *****VARIABLES*****
	 * Make sure these are what you want
	 */
	public static final String VERSION = "3.1";
	public static final String ADD_ONS = NO_ADD_ONS;
	public static final double RAM = 6.0;
	/**These too**/
	public static final int REPEAT = 10; //must be more than 0
	public static final String FILEOUT = "C:/Users/Victor/Documents/Speed/Performance Testing.xls";
	public static final String DATA_SHEET = "Raw Data"; //Sheet name to look for
	public static final String CHROMEDRIVER = "C:/Users/Victor/Downloads/chromedriver.exe";
	public static final String IEDRIVER = "C:/Users/Victor/Downloads/iedriverserver.exe";
	public static final String LOGFILE = "C:/Users/Victor/Documents/Speed/Log.txt";
	/**End of variables**/

	/**
	 * Initializes driver, defaults to firefox
	 * @param browser "firefox", "chrome", or "ie"
	 */
	public WebDriverTester(String browser){
		switch (browser) {
		case FIREFOX : 	mBrowser = FIREFOX;
						break;
		case CHROME : 	mBrowser = CHROME;
						System.setProperty("webdriver.chrome.driver", CHROMEDRIVER);
//						System.setProperty("webdriver.chrome.driver.loglevel", "FATAL");
//						System.setProperty("webdriver.chrome.driver.logfile", LOGFILE);
						break;
		case IE : 		mBrowser = IE;
						System.setProperty("webdriver.ie.driver", IEDRIVER);
//						System.setProperty("webdriver.ie.driver.loglevel", "TRACE");
//						System.setProperty("webdriver.ie.driver.logfile", LOGFILE);
						break;
		default : 		mBrowser = FIREFOX;
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
		
		getBrowserVersion();
	}

	private void getBrowserVersion() {
		getNewDriver();
		Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
		mBrowserVersion = cap.getVersion();
		
		if (mBrowser.equals(IE)){
			mBrowserVersionShort = mBrowserVersion; 
			String uAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
			//uAgent return as "MSIE 8.0 Windows" for IE8
			if (uAgent.contains("MSIE") && uAgent.contains("Windows")) {
				mBrowserVersion = uAgent.substring(uAgent.indexOf("MSIE")+5, uAgent.indexOf("Windows")-2);
			} else if (uAgent.contains("Trident/7.0")) {
				mBrowserVersion = "11.0";
			} else {
				mBrowserVersion = "0.0";
			}
		} else{
			mBrowserVersionShort = mBrowserVersion.substring(0, mBrowserVersion.indexOf("."));
		}
		System.out.println(mBrowser + " Version: " + mBrowserVersionShort);
		driver.quit();
	}

	/**
	 * Create new driver from mBrowser string
	 */
	private void getNewDriver() {
		switch (mBrowser) {
		case FIREFOX : 	driver = new FirefoxDriver();
						break;
		case CHROME : 	driver = new ChromeDriver();
						break;
		case IE : 		DesiredCapabilities cap = new DesiredCapabilities();
						cap.setCapability("ie.ensureCleanSession", true);
						cap.setCapability("nativeEvents", false);
						driver = new InternetExplorerDriver(cap);
						break;
		default : 		driver = new FirefoxDriver();
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
	private void run(){
		System.out.println("\n" + pageName + "\n--------------------");
		getNewDriver();
		checkIsLogIn();
		
		for(int x = 0; x < REPEAT; x++){
			logIn();
			getTimings("No");
			if(x != (REPEAT - 1)){
				driver.quit();
				getNewDriver();
			}
		}

		for(int x = 0; x < REPEAT; x++){
			refreshAndWait();
			getTimings("Yes");
		}
		driver.quit();
		writeToExcel();
		results.clear();
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
		((JavascriptExecutor)driver).executeScript("document.location.reload()"); // True refresh? Not Ctrl+F5
		waitForLoad(driver);
	}

	@SuppressWarnings({ "unchecked" })
	private void getTimings(String cache) {
		long mTotal, mNoLoad;
		
		//get timings Map and calculate time
		if (!mBrowser.equals(IE)) {
			timings = (Map<String, Long>) ((JavascriptExecutor) driver)
					.executeScript("var performance = window.performance || {};"
							+ "var timings = performance.timing || {};"
							+ "return timings;");
			mTotal = (long) timings.get("loadEventEnd")
					- (long) timings.get("navigationStart");
			mNoLoad = mTotal + (long) timings.get("fetchStart")
					- (long) timings.get("responseEnd");
		} else { // IE shenanigans
			List<Long> ieTimes = (ArrayList<Long>) ((JavascriptExecutor)driver)
					.executeScript("var performance = window.performance || {};"
							+ "var timings = performance.timing || {};"
							+ "var navigationStart = timings.navigationStart || {};"
							+ "var fetchStart = timings.fetchStart || {};"
							+ "var responseEnd = timings.responseEnd || {};"
							+ "var loadEventEnd = timings.loadEventEnd || {};"
							+ "var times = [navigationStart, fetchStart, responseEnd, loadEventEnd] || {};"
							+ "return times;");
			

			mTotal = ieTimes.get(3) - ieTimes.get(0);
			mNoLoad = mTotal + ieTimes.get(1) - ieTimes.get(2);
		}
		
		if (mTotal > 0){
			System.out.println(cache + " cache: " + mTotal + ", " + mNoLoad);
			results.add(new WebResult(mTotal, mNoLoad, cache));
		} else {
			System.out.println("Error! " + cache + " cache run");
			System.out.println("loadEventEnd = " + timings.get("loadEventEnd"));
			System.out.println("navigationStart = " + timings.get("navigationStart"));
		}
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
				boolean readyState;
				try {
					readyState = ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
					return readyState;
				} catch (WebDriverException e) {
					System.out.print("Javascript Error, please wait. ");
					return false;
				}
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 90);
		wait.until(pageLoadCondition);
	}

	private void createOutputFile() {
		try {

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(DATA_SHEET);

			HSSFRow rowhead = sheet.createRow((short)1);
			rowhead.createCell(Constants.COL_VERSION).setCellValue("Version");
			rowhead.createCell(Constants.COL_DATE).setCellValue("Date");
			rowhead.createCell(Constants.COL_PAGE_NAME).setCellValue("Page Name");
			rowhead.createCell(Constants.COL_PAGE_URL).setCellValue("URL");
			rowhead.createCell(Constants.COL_CACHE).setCellValue("Cache");
			rowhead.createCell(Constants.COL_BROWSER).setCellValue("Browser");
			rowhead.createCell(Constants.COL_BROWSER_VERSION).setCellValue("Browser Version");
			rowhead.createCell(Constants.COL_ADD_ONS).setCellValue("Add-ons");
			rowhead.createCell(Constants.COL_RAM).setCellValue("RAM(GB)");
			rowhead.createCell(Constants.COL_TIME).setCellValue("Time(s)");
			rowhead.createCell(Constants.COL_TIME_NO_LOAD).setCellValue("Time No Load(s)");

			FileOutputStream fileOut = new FileOutputStream(new File(FILEOUT));
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			System.out.println("Excel file generated!");

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
			HSSFWorkbook workbook = new HSSFWorkbook(input_document); 
			HSSFSheet sheet = workbook.getSheet(DATA_SHEET);

			for (WebResult webRes : results){
				int row = sheet.getPhysicalNumberOfRows() + 2;
				int physRows = sheet.getPhysicalNumberOfRows();
				if((short)row < 2) row = 2;
				if(row < physRows) row = physRows + 1;
				
				HSSFRow rowhead = sheet.createRow((short)row);
				rowhead.createCell(Constants.COL_VERSION).setCellValue(VERSION);
				rowhead.createCell(Constants.COL_DATE).setCellValue(date);
				rowhead.createCell(Constants.COL_PAGE_NAME).setCellValue(pageName);
				rowhead.createCell(Constants.COL_PAGE_URL).setCellValue(pageURL);
				rowhead.createCell(Constants.COL_CACHE).setCellValue(webRes.cache);
				rowhead.createCell(Constants.COL_BROWSER).setCellValue(mBrowser);
				rowhead.createCell(Constants.COL_BROWSER_VERSION).setCellValue(mBrowserVersionShort);
				rowhead.createCell(Constants.COL_ADD_ONS).setCellValue(ADD_ONS);
				rowhead.createCell(Constants.COL_RAM).setCellValue(RAM);
				rowhead.createCell(Constants.COL_TIME).setCellValue(webRes.seconds);
				rowhead.createCell(Constants.COL_TIME_NO_LOAD).setCellValue(webRes.seconds_no_load);
			}

			input_document.close();
			FileOutputStream fileOut = new FileOutputStream(new File(FILEOUT));
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			System.out.println("Excel file updated!");

		} catch ( Exception ex ) {
			System.out.println(ex);
		}
	}

	public static void main(String[] args) throws IOException{
		Logger.getRootLogger().setLevel(Level.OFF);

		WebDriverTester mTest = new WebDriverTester(FIREFOX);
		runAll(mTest);
//		runSome(mTest, 0, 1);
		mTest = new WebDriverTester(CHROME);
		runAll(mTest);
//		runSome(mTest, 0, 1);
		mTest = new WebDriverTester(IE);
		runAll(mTest);
//		runSome(mTest, 0, 1);
	}

	public static void runAll(WebDriverTester mTest) {
		List<Page> pages = Page.getAllPages();
		
		long millStart = Calendar.getInstance().getTimeInMillis();
		for (Page page : pages){
			mTest.run(page.name, page.url);
		}
		long millEnd = Calendar.getInstance().getTimeInMillis();
		
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(millEnd - millStart - TimeZone.getTimeZone("CST").getRawOffset());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		System.out.println("\nTest Finished!\nTotal time: " + formatter.format(time.getTime()) + "\n");
	}
	
	public static void runSome(WebDriverTester mTest, int start, int end) {
		List<Page> pages = Page.getAllPages();
		
		long millStart = Calendar.getInstance().getTimeInMillis();
		for (int i = start; i < pages.size() && i < end; i++){
			mTest.run(pages.get(i).name, pages.get(i).url);
		}
		long millEnd = Calendar.getInstance().getTimeInMillis();
		
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(millEnd - millStart - TimeZone.getTimeZone("CST").getRawOffset());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		System.out.println("\nTest Finished!\nTotal time: " + formatter.format(time.getTime()) + "\n");
	}
}