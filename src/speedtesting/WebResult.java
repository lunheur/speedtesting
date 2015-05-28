package speedtesting;

public class WebResult {
	public String cache;
	public long millis;
	public double seconds;
	
	public WebResult(long m, String c){
		millis = m;
		cache = c;
		seconds = millis / 1000.0;
	}

}
