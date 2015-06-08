package speedtesting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class RunAll {
	/**
	 * *****VARIABLES*****
	 * Make sure these are what you want
	 */
	public static final String DOMAIN = "https://demoh.acciodata.com/";
	public static final String VERSION = "3.10";

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.OFF);

//		WebDriverTester mTest = new WebDriverTester(Constants.FIREFOX, DOMAIN, VERSION, Constants.NO_ADD_ONS);
//		runAll(mTest);
		WebDriverTester mTest = new WebDriverTester(Constants.CHROME, DOMAIN, VERSION, Constants.NO_ADD_ONS);
		WebDriverTester.REPEAT = 25;
		runAll(mTest);
		mTest = new WebDriverTester(Constants.IE, DOMAIN, VERSION, Constants.NO_ADD_ONS);
		WebDriverTester.REPEAT = 25;
		runAll(mTest);
	}
	
	/**
	 * Runs every page given from Page.getAllPages();
	 * @param mTest
	 */
	public static void runAll(WebDriverTester mTest) {
		List<Page> pages = Page.getAllPages();
		
		long millStart = Calendar.getInstance().getTimeInMillis();
		for (Page page : pages){
			try {
				mTest.run(page.name, page.url);
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
	
	/**
	 * Runs pages from start to end (exclusive)
	 * @param mTest
	 * @param start Where to start (numbering starts at 0)
	 * @param end Where to end (end is not included)
	 */
	public static void runSome(WebDriverTester mTest, int start, int end) {
		List<Page> pages = Page.getAllPages();
		
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
