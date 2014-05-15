package ofey;

import java.io.Serializable;
import java.util.ArrayList;

public class DOMNode implements Serializable {
	public int nodeID;
	public String tagName=new String();
	public int textCharCount=0;
	public int tagCount=0;
	public int linkCharCount=0;
	public int linkTagCount=0;
	
	public double textDensity=0;
	public double compositeTextDensity=0;
	public double textDensitySum=0;
	public double compositeDensitySum=0;
	public DOMNode parentNode;
	
	public boolean isContentNode;
	public boolean deleteIt;
	
	
	public DOMNode()
	{
		//node property initialization
		this.isContentNode=false;
		this.deleteIt=false;
	}
}
