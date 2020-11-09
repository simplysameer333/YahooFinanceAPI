
package yahoofinance.histquotes;

/**
 *
 * @author 
 */
public enum Interval {
	MINUTE("m"),
    DAILY("d"),
    WEEKLY("w"),
    MONTHLY("mo");
    
    private final String tag;
    
    Interval(String tag) {
        this.tag = tag;
    }
    
    public String getTag() {
        return this.tag;
    }
    
}
