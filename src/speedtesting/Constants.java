package speedtesting;

public class Constants {
	public static final String LOGIN_URL = "";
	public static final String LOGIN_NAME = "Log-in page";
	public static final String SYSOP_HOME_URL = "sysops/sysop_home5.html";
	public static final String SYSOP_HOME_NAME = "Sysop Home";
	public static final String ORDER_URL = "sdocs/searchrequest.html";
	public static final String ORDER_NAME = "Basic Order";	
	public static final String REPORTS_URL = "cgi-bin/sdocs/show_reportlist";
	public static final String REPORTS_NAME = "Reports (Checked Individuals List)";	
	public static final String ALL_ORDERS_URL = "cgi-bin/sysops/find_order_by?name=%25";
	public static final String ALL_ORDERS_NAME = "Show All Orders";	
	public static final String DOCS_URL = "sdocs/help.html";
	public static final String DOCS_NAME = "Docs & Forms";	
	public static final String ADMIN_URL = "sdocs/admin.html";
	public static final String ADMIN_NAME = "Admin";	
	public static final String OPS_URL = "sysops/sysop_home.html";
	public static final String OPS_NAME = "Operations Home";	
	public static final String BACKLOG_ALL_URL = "cgi-bin/sysopsl2/vendor_backlog";
	public static final String BACKLOG_ALL_NAME = "Manage Backlog (all)";
	public static final String REVIEW_RELEASE_LIST_URL = "cgi-bin/sysopsl2/check_work_list";
	public static final String REVIEW_RELEASE_LIST_NAME = "Review & release results by list";	
	public static final String BILLING_URL = "sysops/billing_and_invoicing.html";
	public static final String BILLING_NAME = "Billing and Invoicing";	
	public static final String ALL_ACCOUNTS_URL = "sysopsl2/review_edit_copy_account.html";
	public static final String ALL_ACCOUNTS_NAME = "List all accounts";	
	public static final String VENDOR_RULES_URL = "sysops/manage_vendor_dispatch_rules.html";
	public static final String VENDOR_RULES_NAME = "Vendor Dispatch Rules";	
	public static final String VENDOR_TABLE_URL = "sysopsl2/vendor_management.html";
	public static final String VENDOR_TABLE_NAME = "Vendor Dispatch table";
	public static final String PRICING_WEB_URL = "sysopsl2/pricing_management_web.html";
	public static final String PRICING_WEB_NAME = "Manage pricing (web)";	
	public static final String PRICING_EXCEL_URL = "sysopsl2/pricing_management.html";
	public static final String PRICING_EXCEL_NAME = "Manage Pricing (excel)";	
	public static final String PRODUCTS_URL = "sysopsl2/addproducts.html";
	public static final String PRODUCTS_NAME = "Add new products";	
	public static final String DOCS_MANAGEMENT_URL = "sysops/manage_docsandform_files.html";
	public static final String DOCS_MANAGEMENT_NAME = "Docs & Forms content management";	
	public static final String SITE_SETUP_URL = "sysopsl2/site_configuration.html#%20toggle%20all";
	public static final String SITE_SETUP_NAME = "Site setup";	
	public static final String SYSTEM_VARIABLES_URL = "cgi-bin/sysops/list_systemvariables";
	public static final String SYSTEM_VARIABLES_NAME = "List System Variables";	
	public static final String SYTEM_MESSAGES_URL = "cgi-bin/sysops/system_messages";
	public static final String SYTEM_MESSAGES_NAME = "System Messages";
	
	//These are ONLY for www.acciodata.com
	public static final String SALES_DOMAIN = "http://www.acciodata.com/";
	public static final String HOME_URL = "";
	public static final String HOME_NAME = "Homepage";
	public static final String ENTERPRISE_URL = "products/new-enterprise/";
	public static final String ENTERPRISE_NAME = "Accio Enterprise";
	public static final String TRAINING_URL = "training-resources/";
	public static final String TRAINING_NAME = "Training Resources";
	public static final String CHOOSING_PLATFORM_URL = "choosing-the-right-platform-provider/";
	public static final String CHOOSING_PLATFORM_NAME = "Choosing Platform";
	public static final String FAQ_URL = "products/faq/";
	public static final String FAQ_NAME = "FAQ";
	public static final String DATA_VENDORS_URL = "vendors/data-vendors/";
	public static final String DATA_VENDORS_NAME = "Data Vendors";
	public static final String RIGHT_PLAN_URL = "the-right-plan-for-you/";
	public static final String RIGHT_PLAN_NAME = "The Right Plan";
	public static final String NEWS_URL = "about-accio/news/";
	public static final String NEWS_NAME = "News";
	public static final String MANAGEMENT_URL = "about-accio/management-team/";
	public static final String MANAGEMENT_NAME = "Management Team";
	public static final String SERIOUS_SECURITY_URL = "serious-security/";
	public static final String SERIOUS_SECURITY_NAME = "Serious Security";
	public static final String CONTACT_URL = "about-accio/contact/";
	public static final String CONTACT_NAME = "Contact Us";
	public static final String JOBS_URL = "jobs-2/";
	public static final String JOBS_NAME = "Jobs";
	public static final String CO_OP_URL = "accioustore/";
	public static final String CO_OP_NAME = "Accio U Co-op";
	public static final String REQUEST_DEMO_URL = "request-a-demo/";
	public static final String REQUEST_DEMO_NAME = "Request A Demo";

	public static final String REPORT_635611_URL = "cgi-bin/sdocs/show_order?order_number=635611";
	public static final String REPORT_635611_NAME = "demoh Report 635611";
	
	public static final int COL_VERSION = 0;
	public static final int COL_DOMAIN = 1;
	public static final int COL_DATE = 2;
	public static final int COL_PAGE_NAME = 3;
	public static final int COL_PAGE_URL = 4;
	public static final int COL_CACHE = 5;
	public static final int COL_BROWSER = 6;
	public static final int COL_BROWSER_VERSION = 7;
	public static final int COL_ADD_ONS = 8;
	public static final int COL_RAM = 9;
	public static final int COL_TIME = 10;
	public static final int COL_TIME_NO_LOAD = 11;
	
	static final String FIREFOX = "Firefox";
	static final String CHROME = "Chrome";
	static final String IE = "IE";
	static final String NO_ADD_ONS = "No";
	
	/**Variables to set**/
	public static final double RAM = 6.0; // Your computer's RAM in GB
	public static final String FILEOUT = "C:/Users/Victor/Documents/Speed/Performance Testing.xls"; // Excel file to receive data, folder needs to exist, but file should not
	public static final String DATA_SHEET = "Raw Data"; //Sheet name to use in FILEOUT
			
}
