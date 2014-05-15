package performance;

import org.apache.commons.lang.StringUtils;

public class PerformanceCalc {
	
	String goldenContent;
	String extractedContent;
	
	public PerformanceCalc()
	{
		//default constructor
	}
	public PerformanceCalc(String gold, String extracted)
	{
		this.goldenContent=gold;
		this.extractedContent=extracted;
	}
	
	public void calculatePerformance()
	{
		//performance calculation
		LCS lcsmaker=new LCS(this.goldenContent, this.extractedContent);
		String lcs=lcsmaker.getLCS();
		System.out.println("Precision:"+((double)lcs.length()/this.extractedContent.length()));
		System.out.println("Recall:"+((double)lcs.length()/this.goldenContent.length()));
	}
	
	

}
