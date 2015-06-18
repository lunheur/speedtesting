package speedtesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AccioDriver {
	private String mAccount, mUser, mPassword;
	private WebDriver driver;
	private Map<String, Long> timings;
	private List<WebResult> results;
	private String mBrowser, mBrowserVersion, mBrowserVersionShort;
	private String date;
	private String pageName, pageURL;
	private String mDomain, mVersion, mAddOns;
	private int mRepeat = 5; //must be more than 0
	private FirefoxProfile firefoxProfile;
	private boolean enableAddOns, isLogInPage = false;
	
	public static final String LOGFILE = "C:/Users/Victor/Documents/Speed/Log.txt"; //Used for debugging, doesn't need to be set

	/**Initializes driver, defaults to firefox
	 * @param browser Use constants FIREFOX, CHROME, or IE
	 * @param domain Website to test, must end in '/'
	 * @param version Version of the site. 3.10, 3.0, or 2.45
	 * @param addOns Add-ons that are activated. Use Constants.NO_ADD_ONS for none
	 * @param repeats # of times to repeat each test (at least 1)
	 */
	public AccioDriver(String browser, String domain, String version, String addOns, int repeats){
		mDomain = domain;
		mVersion = version;
		mAddOns = addOns;
		mRepeat = repeats;
		this.enableAddOns = !mAddOns.equals(Constants.NO_ADD_ONS);
		if(mRepeat < 1) mRepeat = 1;
		
		switch (browser) {
		case Constants.FIREFOX : 	mBrowser = Constants.FIREFOX;
									if( enableAddOns) getFirefoxProfile();
									break;
		case Constants.CHROME : 	mBrowser = Constants.CHROME;
									System.setProperty("webdriver.chrome.driver", 
										   Paths.get("lib","chromedriver.exe").toAbsolutePath().toString());
									System.setProperty("webdriver.chrome.silentOutput", "true");
//									System.setProperty("webdriver.chrome.driver.loglevel", "FATAL");
//									System.setProperty("webdriver.chrome.driver.logfile", LOGFILE);
									break;
		case Constants.IE : 		mBrowser = Constants.IE;
									System.setProperty("webdriver.ie.driver",
										   Paths.get("lib","IEDriverServer.exe").toAbsolutePath().toString());
									System.setProperty("webdriver.ie.driver.silent", "true");
//									System.setProperty("webdriver.ie.driver.loglevel", "TRACE");
//									System.setProperty("webdriver.ie.driver.logfile", LOGFILE);
									break;
		default : 					mBrowser = Constants.FIREFOX;
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
		
		if (mBrowser.equals(Constants.IE)){
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
		case Constants.FIREFOX : 	getFirefoxDriver();
									break;
		case Constants.CHROME : 	getChromeDriver();
									break;
		case Constants.IE : 		getIEDriver();
									break;
		default : 					getFirefoxDriver();
									break;
		}
	}

	private void getIEDriver() {
		DesiredCapabilities ieCap = new DesiredCapabilities();
		ieCap.setCapability("ie.ensureCleanSession", true);
		ieCap.setCapability("nativeEvents", false);
		driver = new InternetExplorerDriver(ieCap);
	}

	private void getChromeDriver() {
		if (enableAddOns){
			ChromeOptions options = new ChromeOptions();
			options.addExtensions(
					new File(Constants.EXTENSION));
			
			driver = new ChromeDriver(options);
			long startWait = System.currentTimeMillis();
			while(driver.getWindowHandles().size() <= 1){
				long endWait = System.currentTimeMillis();
				if ( (endWait - startWait) >= 30000 ) break;
			}
			Object[] handles = driver.getWindowHandles().toArray();
			for (int h = 1; h < handles.length; h++){
//				System.out.println("Closing handle " + (String) handles[1]);
				driver.switchTo().window((String) handles[1]);
				driver.close();
			}
			driver.switchTo().window((String) handles[0]);
			
		}else{
			driver = new ChromeDriver();
		}
	}

	private void getFirefoxDriver() {
		if (enableAddOns)
			driver = new FirefoxDriver(firefoxProfile);
		else
			driver = new FirefoxDriver();
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
	 * USE THIS to run specific websites
	 * Automatically adds domain to url param
	 * @param name Page Title
	 * @param url Page URL, NOT INCLUDING DOMAIN
	 */
	public void run(String name, String url){
		pageURL = mDomain + url;
		pageName = name;
		run();
	}
	/**
	 * Run REPEAT times from fresh window, then reload REPEAT times
	 * Output data to spreadsheet
	 * @param url URL of page to test
	 */
	private void run(){
		System.out.print("\n  " + pageName + "\n  "+ pageURL + "\n  --------------------\n  ");
		getNewDriver();
		checkIsLogInPage();
		
		for(int x = 0; x < mRepeat; x++){
			load();
			getTimings("No");
			if(x != (mRepeat - 1)){
				driver.quit();
				getNewDriver();
			} else if (enableAddOns && mBrowser.equals(Constants.FIREFOX)) {
				driver.quit();
				enableFFCache();
				getNewDriver();
				load();
			}
		}

		for(int x = 0; x < mRepeat; x++){
			refreshAndWait();
			getTimings("Yes");
		}
		driver.quit();
		if (enableAddOns && mBrowser.equals(Constants.FIREFOX))
			disableFFCache();
		writeToExcel();
		results.clear();
	}	

	private void checkIsLogInPage() {
		if ( endsInCom() || isSales() ){
			isLogInPage = true;
		}else{
			isLogInPage = false;
		}
	}

	private boolean isSales() {
		return mDomain.equals(Constants.SALES_DOMAIN);
	}

	private boolean endsInCom() {
		String[] urlArray = pageURL.split("\\.");
		return urlArray[urlArray.length - 1].equals("com/") || 
			   urlArray[urlArray.length - 1].equals("com");
	}

	private void refreshAndWait() {
		((JavascriptExecutor)driver).executeScript("document.location.reload()"); // True refresh? Not Ctrl+F5
		waitForLoad(driver);
	}

	/**
	 * Gets the timing information using Javascript and stores in results list
	 * @param cache Either "Yes" or "No" to indicate if there is a cache
	 */
	@SuppressWarnings({ "unchecked" })
	private void getTimings(String cache) {
		long mTotal, mNoLoad;
		
		//get timings Map and calculate time
		if (!mBrowser.equals(Constants.IE)) {
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
//			System.out.println(cache + " cache: " + mTotal + ", " + mNoLoad);
			System.out.print(".");
			results.add(new WebResult(mTotal, mNoLoad, cache));
		} else {
			System.out.println("Error! " + cache + " cache run");
			System.out.println("loadEventEnd = " + timings.get("loadEventEnd"));
			System.out.println("navigationStart = " + timings.get("navigationStart"));
		}
	}

	private void load() {
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

	/**
	 * Wait for page to load, uses Javascript
	 * @param driver
	 */
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

	/**
	 * Creates new FILEOUT spreadsheet
	 */
	private void createOutputFile() {
		try {

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(Constants.DATA_SHEET);

			HSSFRow rowhead = sheet.createRow((short)1);
			rowhead.createCell(Constants.COL_VERSION).setCellValue("Version");
			rowhead.createCell(Constants.COL_DOMAIN).setCellValue("Domain");
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
			rowhead.createCell(Constants.COL_LATENCY).setCellValue("Latency(s)");

			FileOutputStream fileOut = new FileOutputStream(new File(Constants.FILEOUT));
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			System.out.println("\nExcel file generated!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write everything in results List to FILEOUT
	 * FILEOUT is created if it doesn't exist
	 */
	private void writeToExcel(){
		try {
			if( !(new File(Constants.FILEOUT).exists()) )
				createOutputFile();

			FileInputStream input_document = new FileInputStream(new File(Constants.FILEOUT));
			HSSFWorkbook workbook = new HSSFWorkbook(input_document); 
			HSSFSheet sheet = workbook.getSheet(Constants.DATA_SHEET);

			long tot = 0;
			for (WebResult webRes : results){
				int row = sheet.getPhysicalNumberOfRows() + 2;
				int physRows = sheet.getPhysicalNumberOfRows();
				if((short)row < 2) row = 2;
				if(row < physRows) row = physRows + 1;
				
				HSSFRow rowhead = sheet.createRow((short)row);
				rowhead.createCell(Constants.COL_VERSION).setCellValue(mVersion);
				rowhead.createCell(Constants.COL_DOMAIN).setCellValue(mDomain);
				rowhead.createCell(Constants.COL_DATE).setCellValue(date);
				rowhead.createCell(Constants.COL_PAGE_NAME).setCellValue(pageName);
				rowhead.createCell(Constants.COL_PAGE_URL).setCellValue(pageURL);
				rowhead.createCell(Constants.COL_CACHE).setCellValue(webRes.cache);
				rowhead.createCell(Constants.COL_BROWSER).setCellValue(mBrowser);
				rowhead.createCell(Constants.COL_BROWSER_VERSION).setCellValue(mBrowserVersionShort);
				rowhead.createCell(Constants.COL_ADD_ONS).setCellValue(mAddOns);
				rowhead.createCell(Constants.COL_RAM).setCellValue(Constants.RAM);
				rowhead.createCell(Constants.COL_TIME).setCellValue(webRes.seconds);
				rowhead.createCell(Constants.COL_TIME_NO_LOAD).setCellValue(webRes.seconds_no_load);
				rowhead.createCell(Constants.COL_LATENCY).setCellValue(webRes.latency);
				
				tot += webRes.seconds;
			}

			input_document.close();
			FileOutputStream fileOut = new FileOutputStream(new File(Constants.FILEOUT));
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			double avg = tot / (long)results.size();
			System.out.println("\n  Average is: " + avg + "s" );
			System.out.println("  Excel file updated!");

		} catch ( Exception ex ) {
			System.out.println(ex);
		}
	}

	private void getFirefoxProfile(){
		File profileDir = new File(Constants.FFPROFILE);
		firefoxProfile = new FirefoxProfile(profileDir);
		disableFFCache();
	}

	private void disableFFCache() {
		firefoxProfile.setPreference("browser.cache.disk.enable", false);
		firefoxProfile.setPreference("browser.cache.memory.enable", false);
		firefoxProfile.setPreference("browser.cache.offline.enable", false);
		firefoxProfile.setPreference("network.http.use-cache", false);
	}
	
	private void enableFFCache() {
		firefoxProfile.setPreference("browser.cache.disk.enable", true);
		firefoxProfile.setPreference("browser.cache.memory.enable", true);
		firefoxProfile.setPreference("browser.cache.offline.enable", true);
		firefoxProfile.setPreference("network.http.use-cache", true);
	}
	
//	public static void main(String[] args) throws IOException, InterruptedException{
//		Logger.getRootLogger().setLevel(Level.OFF);
//	}

}