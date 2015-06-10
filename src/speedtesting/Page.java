package speedtesting;

import java.util.ArrayList;
import java.util.List;

public class Page {
	public String url;
	public String name;

	/**Returns Page object
	 * @param name Page Name
	 * @param url Page URL
	 */
	public Page(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	public static List<Page> getAllPages(){
		List<Page> pages = new ArrayList<Page>();
		
		pages.add(new Page(Constants.LOGIN_NAME, Constants.LOGIN_URL));
		pages.add(new Page(Constants.SYSOP_HOME_NAME, Constants.SYSOP_HOME_URL));
		pages.add(new Page(Constants.ORDER_NAME, Constants.ORDER_URL));
		pages.add(new Page(Constants.REPORTS_NAME, Constants.REPORTS_URL));
		pages.add(new Page(Constants.ALL_ORDERS_NAME, Constants.ALL_ORDERS_URL));
		pages.add(new Page(Constants.DOCS_NAME, Constants.DOCS_URL));
		pages.add(new Page(Constants.ADMIN_NAME, Constants.ADMIN_URL));
		pages.add(new Page(Constants.OPS_NAME, Constants.OPS_URL));
		pages.add(new Page(Constants.BACKLOG_ALL_NAME, Constants.BACKLOG_ALL_URL));
		pages.add(new Page(Constants.REVIEW_RELEASE_LIST_NAME, Constants.REVIEW_RELEASE_LIST_URL));
		pages.add(new Page(Constants.BILLING_NAME, Constants.BILLING_URL));
		pages.add(new Page(Constants.ALL_ACCOUNTS_NAME, Constants.ALL_ACCOUNTS_URL));
		pages.add(new Page(Constants.VENDOR_RULES_NAME, Constants.VENDOR_RULES_URL));
		pages.add(new Page(Constants.VENDOR_TABLE_NAME, Constants.VENDOR_TABLE_URL));
		pages.add(new Page(Constants.PRICING_WEB_NAME, Constants.PRICING_WEB_URL));
		pages.add(new Page(Constants.PRICING_EXCEL_NAME, Constants.PRICING_EXCEL_URL));
		pages.add(new Page(Constants.PRODUCTS_NAME, Constants.PRODUCTS_URL));
		pages.add(new Page(Constants.DOCS_MANAGEMENT_NAME, Constants.DOCS_MANAGEMENT_URL));
		pages.add(new Page(Constants.SITE_SETUP_NAME, Constants.SITE_SETUP_URL));
		pages.add(new Page(Constants.SYSTEM_VARIABLES_NAME, Constants.SYSTEM_VARIABLES_URL));
		pages.add(new Page(Constants.SYTEM_MESSAGES_NAME, Constants.SYTEM_MESSAGES_URL));
		
		return pages;
	}

}
