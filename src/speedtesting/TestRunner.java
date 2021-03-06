package speedtesting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TestRunner {
	static Domain demoh = new Domain(Constants.DEMOH, "devhead");
	static Domain integrationtest = new Domain(Constants.INTEGRATIONTEST, "integrationtest");
//	static Domain development = new Domain(Constants.DEVELOPMENT, "development");
//	static Domain bravo3_10_plus = new Domain(Constants.BRAVO3_10_PLUS, "3.10_plus");
	static Domain bravo3_10 = new Domain(Constants.BRAVO3_10, "3.10");
//	static Domain bravo3_0_plus = new Domain(Constants.BRAVO3_0_PLUS, "3.0_plus");
//	static Domain bravo3_0 = new Domain(Constants.BRAVO3_0, "3.0");
//	static Domain bravo2_45_plus = new Domain(Constants.BRAVO2_45_PLUS, "2.45_plus");
//	static Domain bravo2_45 = new Domain(Constants.BRAVO2_45, "2.45");
	static Domain bravo3_11 = new Domain(Constants.BRAVO3_11, "3.11");
	static Domain bravo3_11_plus = new Domain(Constants.BRAVO3_11_PLUS, "3.11_plus");
	
	/**
	 * *****VARIABLES*****
	 * Make sure these are what you want
	 */
	public static final int REPEATS = 10; // Number of times to load each page, first without cache, then with

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.OFF);
		String login = "login.txt";
		
		/**
		 * Quick Test to verify each browser works
		 * Delete the word "DELETE" to enable
		 * 
		 *DELETE/

		Page home = new Page(Constants.SECURE_HOME_NAME, Constants.SECURE_HOME_URL);
		
		AccioDriver driver = new AccioDriver(Constants.FIREFOX, bravo3_10_plus, Constants.NO_ADD_ONS, 1, admin);
		runPage(driver, home);
		driver.setBrowser(Constants.CHROME);
		runPage(driver, home);
		driver.setBrowser(Constants.IE);
		runPage(driver, home);
//		*/
		
		runAllBravos(Constants.IE, Constants.NO_ADD_ONS, REPEATS, login);
		runAllBravos(Constants.FIREFOX, Constants.NO_ADD_ONS, REPEATS, login);
		runAllBravos(Constants.CHROME, Constants.NO_ADD_ONS, REPEATS, login);
	}
	
	public static void runAllBravos(String browser, String addOns, int repeats, String credFile) {
		AccioDriver driver = new AccioDriver(browser, bravo3_10, addOns, repeats, credFile);
		runAccio(driver);
		
		driver.setDomain(bravo3_11);
		runAccio(driver);
		
		driver.setDomain(bravo3_11_plus);
		runAccio(driver);
	}

	public static void runAccio(AccioDriver mTest){
		runAll(mTest, Page.getAccioPages());
	}
	
	public static void runSales(AccioDriver mTest){
		runAll(mTest, Page.getSalesPages());
	}
	
	/**Runs a single Page
	 * @param mTest
	 * @param page Page to run
	 */
	public static void runPage(AccioDriver mTest, Page page) {
		long millStart = Calendar.getInstance().getTimeInMillis();
		try {
			mTest.run(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long millEnd = Calendar.getInstance().getTimeInMillis();

		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(millEnd - millStart - TimeZone.getTimeZone("CST").getRawOffset());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		System.out.println("\nTest Finished!\nTotal time: " + formatter.format(time.getTime()) + "\n");
	}

	/**
	 * Runs every page given
	 * @param mTest
	 * @param pages List of pages to run
	 */
	public static void runAll(AccioDriver mTest, List<Page> pages) {
		long millStart = Calendar.getInstance().getTimeInMillis();
		for (Page page : pages){
			try {
				mTest.run(page);
			} catch (Exception pageEx) {
				mTest.toExcel(); //print out what we got
				pageEx.printStackTrace();
				System.out.println(page.name + " failed! Trying to close...");
				try{
					mTest = mTest.getCloneAndClose();
				} catch (Exception quitEx) {
					System.out.println("Closing failed.");
					quitEx.printStackTrace();
				}
			}
		}
		long millEnd = Calendar.getInstance().getTimeInMillis();
		
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(millEnd - millStart - TimeZone.getTimeZone("CST").getRawOffset());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		System.out.println("\nTest Finished!\nTotal time: " + formatter.format(time.getTime()) + "\n");
	}
	
	/**
	 * Runs pages from start to end (exclusive)
	 * @param mTest
	 * @param start Where to start (numbering starts at 0)
	 * @param end Where to end (end is not included)
	 */
	public static void runSome(AccioDriver mTest, int start, int end) {
		List<Page> pages = Page.getAccioPages();
		
		long millStart = Calendar.getInstance().getTimeInMillis();
		for (int i = start; i < pages.size() && i < end; i++){
			try {
				mTest.run(pages.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long millEnd = Calendar.getInstance().getTimeInMillis();
		
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(millEnd - millStart - TimeZone.getTimeZone("CST").getRawOffset());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		System.out.println("\nTest Finished!\nTotal time: " + formatter.format(time.getTime()) + "\n");
	}
}