package ofey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import core.StaticData;
import utility.ContentLoader;

public class SunRelevantManager {

	String htmlFileName;
	int fileID;
	
	public SunRelevantManager(String htmlFileName, int fileID)
	{
		this.htmlFileName=htmlFileName;
		this.fileID=fileID;
	}
	
	protected void extractRelevantContent()
	{
		//extract relevant content
		MySegmentManager parser=new MySegmentManager(htmlFileName, fileID);
		String pageContent=ContentLoader.loadFileContent(parser.filePathName);
		Document document=Jsoup.parse(pageContent);
		Element body=document.body();
		parser.computeBodyStats(body);
		body=parser.discoverDOMTree(body);
		body=parser.computeDensitySums(body);
		parser.collecThresholds(document);
		
		//now filter irrelevant content
		RelevantExtractor rextractor=new RelevantExtractor(body, parser.thresold_compositeDensity);
		Element modifiedBody=rextractor.provideRelevantSection();
		//saving modified body
		saveRelevantContent(modifiedBody.text());
	}
	protected void saveRelevantContent(String content)
	{
		String textFileName = new String();
		textFileName = StaticData.CE_Data_Home
				+ "/source/coll500/existing/sun/relevant-text/" + fileID+".txt";
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
		long start=System.currentTimeMillis();
		for(int i=1;i<=250;i++){
		int fileID=i;
		String htmlFileName=StaticData.CE_Data_Home+"/source/coll500/org/"+fileID+".html";
		SunRelevantManager manager=new SunRelevantManager(htmlFileName, fileID);
		manager.extractRelevantContent();
		long end=System.currentTimeMillis();
		System.out.println("Completed:"+i);
		//System.out.println("Time required:"+(end-start)/1000);
		}
		
	}
}
