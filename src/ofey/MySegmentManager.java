package ofey;

import htmlview.HTMLPageViewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import performance.PerformanceCalc;
import core.StaticData;
import utility.ContentLoader;

public class MySegmentManager {

	//class members
	String filePathName;
	int fileID=0;
	int leafNode=0;
	ArrayList<String> deleteTags;
	HashMap<Element,DOMNode> nodeMap;
	//HashMap<Element, Element> parentMap;
	int LCb=0;
	int Cb=0;
	double e=2.71828;
	double threshold_textDensity=0;
	double thresold_compositeDensity=0;
	
	public MySegmentManager(String filePathName, int fileID) {
		//property initialization
		this.filePathName = filePathName;
		this.deleteTags = new ArrayList<>();
		this.populateDeleteTagList();
		this.fileID=fileID;
		//this.nodeMap=new HashMap<>();
		//this.parentMap=new HashMap<>();
	}

	protected void populateDeleteTagList() {
		//this.deleteTags.add("a");
		this.deleteTags.add("script");
		this.deleteTags.add("style");
		this.deleteTags.add("noscript");
	}
	
	protected void computeBodyStats(Element body)
	{
		//code for computing the statistics of page body
		this.Cb=body.text().length();
		Elements links=body.select("a,input,button,select"); //add more tags
		for(Element link:links){
			this.LCb+=link.text().trim().length();
		}
	}
	
	protected double getLogBase(int Ci, int LCi, int NLCi)
	{
		//code for getting the log base
		if(NLCi==0)NLCi=1;
		if(Cb==0)Cb=1;
		double logbase=Math.log((Ci*LCi/NLCi)+(LCb*Ci/Cb)+e);
		return logbase;
	}
	
	protected Element recordElement(Element elem)
	{
		//text char and tags
		Elements elems=elem.getAllElements();
		int tagCount=elems.size();
		if(tagCount==0)tagCount=1;
		int textCharCount=elem.text().length();
		//link char and tags
		Elements linkElems=elem.select("a,input,button,select"); //add more tags
		int linkTagCount=linkElems.size();
		if(linkTagCount==0)linkTagCount=1;
		int linkCharCount=0;
		for(Element linkelem:linkElems){
			linkCharCount+=linkelem.text().length();
		}
		if(linkCharCount==0)linkCharCount=1;
		
		//non link char and tags
		int nonLinkCharCount=textCharCount-linkCharCount;
		
		//density calculation
		double textDensity=(double)textCharCount/tagCount;
		elem.attr("textdensity",String.format("%.2f", textDensity));
		
		//now compute complex text density
		double logbase=getLogBase(textCharCount, linkCharCount, nonLinkCharCount);
		double logPart=Math.log(textCharCount*tagCount/(linkCharCount*linkTagCount));
		logPart=logPart/Math.log(logbase);
		double complex_density=0;
		try{
		complex_density=((double)textCharCount/tagCount)*logPart;
		double compositeTextDensity=complex_density;
		elem.attr("compdensity",String.format("%.2f", compositeTextDensity));
		}catch(Exception exc){
			
		}
		return elem;
	}
	
	protected Element discoverNode(Element elem)
	{
		Elements children=elem.children();
		for(Element child:children){
			discoverNode(child);
		}
		//now record the element
		elem=recordElement(elem);
		return elem;
	}
	
	protected Element discoverDOMTree(Element pageElement) {
		Elements childElements = pageElement.children();
		for (Element child : childElements) {
			if (child.hasText()) {
				if (this.deleteTags.contains(child.tagName()))
					continue;
				child = discoverNode(child);
				// record the blocks
				child = recordElement(child);
			}
		}
		// record for page body
		pageElement = recordElement(pageElement);
		return pageElement;
	}

	protected Element computeSumSubTree(Element subTree) {
		Elements elems = subTree.children();
		double sum = 0;
		for (Element elem : elems) {
			elem = computeSumSubTree(elem);
			try {
				double compDensity = Double.parseDouble(elem
						.attr("compdensity"));
				sum += compDensity;
			} catch (Exception e) {
			}
		}
		subTree.attr("compdensum", String.format("%.2f", sum));
		return subTree;
	}
	
	
	protected Element computeDensitySums(Element body) {
		// code for calculating the density sums
		Elements childElems=body.children();
		for(Element child:childElems){
			if (child.hasText()) {
				if (this.deleteTags.contains(child.tagName()))
					continue;
				child = computeSumSubTree(child);
			}
		}
		body=computeSumSubTree(body);
		return body;
	}
	
	protected void analyzePageSegments()
	{
		//code for analyzing the page segments
		String pageContent=ContentLoader.loadFileContent(this.filePathName);
		Document document=Jsoup.parse(pageContent);
		Element body=document.body();
		this.computeBodyStats(body);
		body=discoverDOMTree(body);
		body=this.computeDensitySums(body);
		this.collecThresholds(document);
		//System.out.println("Total elements recorded:"+this.nodeMap.keySet().size());
		//collect and show the content
		//ContentExtractor extractor=new ContentExtractor(document, this.nodeMap, this.threshold_textDensity);
		//Element pageBody=document.body();
		
		ContentExtractor extractor=new ContentExtractor(body,this.thresold_compositeDensity,this.nodeMap);
		System.out.println("Primary elements:"+body.children().size());
		Element modified=extractor.refineDOMTree();
		//System.out.println(modified);
		System.out.println("After refinement:"+modified.children().size());
		document.body().html(modified.toString());
		//new HTMLPageViewer(document.html()).view();
		//new HTMLPageViewer(modified.html()).view();
		//showPerformance(modified.text());
		saveMainContent(modified.text());
	}
	
	protected void collecThresholds(Document document)
	{
		//code for collecting thresholds
		
		Element body=document.body();
		//thresholds
		this.threshold_textDensity=Double.parseDouble(body.attr("textdensity"));
		//System.out.println("Text Density threshold:"+this.threshold_textDensity);
		this.thresold_compositeDensity=Double.parseDouble(body.attr("compdensity"));
		System.out.println("Composite Density threshold:"+this.thresold_compositeDensity);
	}
	
	protected void showDOMNodeInfo() {
		// code for showing the information
		for (Element elem : this.nodeMap.keySet()) {
			DOMNode node = this.nodeMap.get(elem);
			System.out.println(elem.tagName() + " " + node.textDensity + " "
					+ node.compositeTextDensity + " " + node.textDensitySum
					+ " " + node.compositeDensitySum);
		}
	}
	
	protected void showPerformance(String extracted)
	{
		String goldFile=StaticData.CE_Data_Home+"/source/coll500/main-html/9.html";
		try {
			Document doc1=Jsoup.parse(new File(goldFile), "UTF-8");
			PerformanceCalc calc=new PerformanceCalc(doc1.text(), extracted);
			calc.calculatePerformance();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void saveMainContent(String content)
	{
		String textFileName = new String();
		textFileName = StaticData.CE_Data_Home
				+ "/source/coll500/existing/sun/main-text/" + fileID+".txt";
		// save the content
		try {
			FileWriter fwriter = new FileWriter(new File(textFileName));
			fwriter.write(content);
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		
		for(int i=51;i<=80;i++){
		String segmentFileName=StaticData.CE_Data_Home+"/source/coll500/org/"+i+".html";;
		MySegmentManager segmentManager=new MySegmentManager(segmentFileName,i);
		segmentManager.analyzePageSegments();
		System.out.println("Completed:"+i);
		}
	}
}
