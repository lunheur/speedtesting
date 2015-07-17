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
	
	public static List<Page> getAccioPages(){
		List<Page> pages = new ArrayList<Page>();
		
		pages.add(new Page(Constants.LOGIN_NAME, Constants.LOGIN_URL));
		pages.add(new Page(Constants.SECURE_HOME_NAME, Constants.SECURE_HOME_URL));
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
	
	public static List<Page> getSalesPages(){
		List<Page> pages = new ArrayList<Page>();
		
		pages.add(new Page(Constants.HOME_NAME, Constants.HOME_URL));
		pages.add(new Page(Constants.ENTERPRISE_NAME, Constants.ENTERPRISE_URL));
		pages.add(new Page(Constants.TRAINING_NAME, Constants.TRAINING_URL));
		pages.add(new Page(Constants.CHOOSING_PLATFORM_NAME, Constants.CHOOSING_PLATFORM_URL));
		pages.add(new Page(Constants.FAQ_NAME, Constants.FAQ_URL));
		pages.add(new Page(Constants.DATA_VENDORS_NAME, Constants.DATA_VENDORS_URL));
		pages.add(new Page(Constants.RIGHT_PLAN_NAME, Constants.RIGHT_PLAN_URL));
		pages.add(new Page(Constants.NEWS_NAME, Constants.NEWS_URL));
		pages.add(new Page(Constants.MANAGEMENT_NAME, Constants.MANAGEMENT_URL));
		pages.add(new Page(Constants.SERIOUS_SECURITY_NAME, Constants.SERIOUS_SECURITY_URL));
		pages.add(new Page(Constants.CONTACT_NAME, Constants.CONTACT_URL));
		pages.add(new Page(Constants.JOBS_NAME, Constants.JOBS_URL));
		pages.add(new Page(Constants.CO_OP_NAME, Constants.CO_OP_URL));
		pages.add(new Page(Constants.REQUEST_DEMO_NAME, Constants.REQUEST_DEMO_URL));
		
		return pages;
	}

}
