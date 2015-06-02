package speedtesting;

public class WebResult {
	public String cache;
	public long millis, millis_no_load;
	public double seconds, seconds_no_load;
	
	public WebResult(long millis, long millis_no_load, String cache){
		this.millis = millis;
		this.millis_no_load = millis_no_load;
		this.cache = cache;
		seconds = this.millis / 1000.0;
		seconds_no_load = this.millis_no_load / 1000.0;
	}

}
