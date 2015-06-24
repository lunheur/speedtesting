package speedtesting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TestRunner {
	/**
	 * *****VARIABLES*****
	 * Make sure these are what you want
	 */
	public static final int REPEATS = 50; // Number of times to load each page, first without cache, then with
	public static final String DOMAIN = Constants.DEMOH; // Must have '/' at the end
	public static final String VERSION = Constants.VER3_10;

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.OFF);
		
		Page choosingPlatform = new Page(Constants.CHOOSING_PLATFORM_NAME, Constants.CHOOSING_PLATFORM_URL);
		Page trainingResources = new Page(Constants.TRAINING_NAME, Constants.TRAINING_URL);
		
		runPage(new AccioDriver(Constants.CHROME, Constants.SALES_DOMAIN, "Sales", Constants.ADBLOCKPLUS, REPEATS), trainingResources);
		runPage(new AccioDriver(Constants.FIREFOX, Constants.SALES_DOMAIN, "Sales", Constants.ADBLOCKPLUS, REPEATS), trainingResources);
		runPage(new AccioDriver(Constants.IE, Constants.SALES_DOMAIN, "Sales", Constants.ADBLOCKPLUS, REPEATS), choosingPlatform);
		runPage(new AccioDriver(Constants.IE, Constants.SALES_DOMAIN, "Sales", Constants.ADBLOCKPLUS, REPEATS), trainingResources);
		
		runAccio(new AccioDriver(Constants.CHROME, Constants.BRAVO3_0, "3.0", Constants.NO_ADD_ONS, REPEATS));
		runAccio(new AccioDriver(Constants.CHROME, Constants.BRAVO2_45, "2.45", Constants.NO_ADD_ONS, REPEATS));
		runAccio(new AccioDriver(Constants.FIREFOX, Constants.BRAVO3_0, "3.0", Constants.NO_ADD_ONS, REPEATS));
		runAccio(new AccioDriver(Constants.FIREFOX, Constants.BRAVO2_45, "2.45", Constants.NO_ADD_ONS, REPEATS));
		runAccio(new AccioDriver(Constants.IE, Constants.BRAVO3_0, "3.0", Constants.NO_ADD_ONS, REPEATS));
		runAccio(new AccioDriver(Constants.IE, Constants.BRAVO2_45, "2.45", Constants.NO_ADD_ONS, REPEATS));
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
			mTest.run(page.name, page.url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
				mTest.run(page.name, page.url);
			} catch (Exception pageEx) {
				pageEx.printStackTrace();
				System.out.println(page.name + " failed! Trying to close...");
				try{
					mTest.quit();
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
				mTest.run(pages.get(i).name, pages.get(i).url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
