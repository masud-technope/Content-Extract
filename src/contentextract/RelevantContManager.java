package contentextract;

import htmlview.CustomizePageContent;
import htmlview.HTMLPageViewer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import metrics.MetricNormScoreCalculator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import utility.ContentLoader;
import core.StaticData;

public class RelevantContManager {
	
	String htmlFileName;
	int exceptionID;
	int fileID;
	static HashMap<Integer, Integer> exceptionPointer=new HashMap<>();
	
	public RelevantContManager(String fileName, int exceptionID, int fileID)
	{
		this.htmlFileName=fileName;
		this.exceptionID=exceptionID;
		this.fileID=fileID;
	}
	
	
	
	protected void extractRelevantContent()
	{
		String content=ContentLoader.loadFileContent(htmlFileName);
		Document document=Jsoup.parse(content);
		DOMParser parser=new DOMParser(document,exceptionID);
		Element body=parser.parseDocument();
		
		//normalization
		MetricNormScoreCalculator normcalc=new MetricNormScoreCalculator(body,true);
		Element finalBody=normcalc.normalizeMetricAndCalculateScore();
		//Element finalBodySum=normcalc.getElementScoreSum(finalBody);
		
		//now filter the HTML body
		//DOMFilter filterer=new DOMFilter(finalBodySum);
	
		RelevanceFilter filterer=new RelevanceFilter(finalBody);
		Element refinedBody=filterer.provideRelevantSection();
	
		//System.out.println(refinedBody);
		
		//showPerformance(refinedBody.text());
		//System.out.println(refinedBody);
		//System.out.println("Node recorded:"+parser.nodeMap.keySet().size());
		//Document modifiedDoc=CustomizePageContent.customizerContent(document, refinedBody);
		//new HTMLPageViewer(modifiedDoc.toString()).view();
		saveMainContent(refinedBody.text());
		//saveMainContentHTML(modifiedDoc.html());
	}
	
	
	
	protected void saveMainContent(String content)
	{
		String textFileName =new String();// this.htmlFileName.split("\\.")[0] + ".txt";
		textFileName = StaticData.CE_Data_Home
				+ "/source/coll500/relevant-text/" + fileID+".txt";
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
	
	protected static void getIndexPointer() {
		// developing exception index
		String indexFile = StaticData.CE_Data_Home + "/source/sourceIndex.txt";
		try {
			Scanner scanner = new Scanner(new File(indexFile));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				int id = Integer.parseInt(parts[0]);
				int excepID = Integer.parseInt(parts[1]);
				exceptionPointer.put(id, excepID);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		long start=System.currentTimeMillis();
		getIndexPointer();
		for(int i=1;i<=250;i++){
		int fileID=i;
		int exceptionID=exceptionPointer.get(fileID).intValue();
		String fileName=StaticData.CE_Data_Home+"/source/coll500/org/"+fileID+".html";
		RelevantContManager manager=new RelevantContManager(fileName, exceptionID,fileID);
		manager.extractRelevantContent();
		long end=System.currentTimeMillis();
		System.out.println("Completed:"+i);
		//System.out.println("Time required:"+(end-start)/1000);
		}
	}
}
