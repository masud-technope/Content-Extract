package performance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import utility.ContentLoader;
import core.StaticData;

public class PerformanceManager {
	
	double avgPrecision=0;
	double avgRecall=0;
	double avgFmeasure=0;
	HashMap<Integer, String> indexURLs;
	
	public PerformanceManager()
	{
		//default constructor
		indexURLs=new HashMap<>();
		this.loadIndexURLs();
	}
	
	protected String getHTMLPageContent(String fileName)
	{
		String content=new String();
		try {
			Document doc=Jsoup.parse(new File(fileName),"UTF-8");
			content=doc.text();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
	
	protected ArrayList<String> tokenizeContent(String content) {
		// tokenize the content
		ArrayList<String> tokenList = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(content," .,;:?\"\'!/-");
		while (tokenizer.hasMoreTokens()) {
			tokenList.add(tokenizer.nextToken());
		}
		return tokenList;
	}
	
	
	protected static void checkLCS(int ID)
	{
		double precision=0, recall=0, fmeasure=0;
		String extFileName=StaticData.CE_Data_Home+"/source/coll500/existing/sun/main-text/"+ID+".txt";
		String extractedContent=ContentLoader.loadFileContent(extFileName);
		String goldFileName=StaticData.CE_Data_Home+"/source/coll500/gold-text/"+ID+".txt";
		String goldcontent=ContentLoader.loadFileContent(goldFileName);
		
		LCS lcsmaker=new LCS(goldcontent.trim(), extractedContent.trim());
		String lcs=lcsmaker.getLCS();
		//LCS lcs=new LCS();
		//int lcslength=lcs.getLCSLength(goldcontent, extractedContent, goldcontent.length()-1, extractedContent.length()-1);
		
		if(extractedContent.length()==0)precision=0;
		//else precision=((double)lcslength/extractedContent.length());
		//recall=((double)lcslength/goldcontent.length());
		else precision=((double)lcs.length()/extractedContent.length());
		recall=((double)lcs.length()/goldcontent.length());
		if(precision>0 || recall>0)
		fmeasure=2*precision*recall/(precision+recall);
		
		System.out.println(ID +" Precision:"+precision+" Recall:"+recall+" Fmeasure:"+fmeasure);
	}
	
	
	protected void calculatePerformance()
	{
		//calculating the performance
		try{
			
			double sum_precision=0;
			double sum_recall=0;
			double sum_fmeasure=0;
			double sum_fpr=0;
			
			int actualcount=0;
			
			for(int i=1;i<=500;i++){
				double precision=0;
				double recall=0;
				double fmeasure=0;
				double fpr=0;
				
				//if(isStackOverflow(i))continue;
				
				//if(i==236||i==256 || i==257 ||i==213|| i==234 || i==212 ||i==235 )continue;
				//if(i==184)continue;
				//if(i==330)continue;
				//System.out.println("Now running:"+i);
				String extFileName=StaticData.CE_Data_Home+"/source/coll500/existing/tccb/main-text/"+i+".txt";
				String extractedContent=ContentLoader.loadFileContent(extFileName);
				String goldFileName=StaticData.CE_Data_Home+"/source/coll500/gold-text/"+i+".txt";
				String goldcontent=ContentLoader.loadFileContent(goldFileName);
				String orgFileName=StaticData.CE_Data_Home+"/source/coll500/org/"+i+".html";
				String orgContent=ContentLoader.loadFileContent(orgFileName);
				
				try{
				
				//added
				ArrayList<String> gold=tokenizeContent(goldcontent.trim());
				ArrayList<String> extracted=tokenizeContent(extractedContent.trim());
				ArrayList<String> org=tokenizeContent(orgContent);
				WordLCS lcsmaker=new WordLCS(gold, extracted);
				int lcslength=lcsmaker.getLCS_Dynamic(gold.size(), extracted.size()).size();
				if(extracted.size()==0){
					precision=0;
					recall=0;
				}else{
					precision=(double)lcslength/extracted.size();
					recall=(double)lcslength/gold.size();
					fpr=(double)(extracted.size()-lcslength)/(org.size()-gold.size());
				}
				//added
					
				/*LCS lcsmaker=new LCS(goldcontent.trim(), extractedContent.trim());
				String lcs=lcsmaker.getLCS();
				//LCS lcs=new LCS();
				//int lcslength=lcs.getLCSLength(goldcontent, extractedContent, goldcontent.length()-1, extractedContent.length()-1);
				
				if(extractedContent.length()==0){
					precision=0;
					recall=0;
				}
				//else precision=((double)lcslength/extractedContent.length());
				//recall=((double)lcslength/goldcontent.length());
				else {
					precision=((double)lcs.length()/extractedContent.length());
					recall=((double)lcs.length()/goldcontent.length());
				}*/
				
				
				if(precision>0 || recall>0)
				fmeasure=2*precision*recall/(precision+recall);
				
				//System.out.println(i +" Precision:"+precision+" Recall:"+recall+" Fmeasure:"+fmeasure);
				//System.out.println(i +" "+precision+" "+recall);
				System.out.println(i +" "+recall+" "+fpr);
				//System.out.println(i +" "+fmeasure);
				
				sum_precision+=precision;
				sum_recall+=recall;
				sum_fmeasure+=fmeasure;
				sum_fpr+=fpr;
				actualcount++;
				//Thread.sleep(1000);
				}catch(Exception e){
					
				}
			}
			System.out.println("Performance on main content extraction");
			System.out.println("Avg precision:"+sum_precision/actualcount);
			System.out.println("Avg recall:"+sum_recall/actualcount);
			System.out.println("Avg f-measure:"+sum_fmeasure/actualcount);
			
			System.out.println("Actual count:"+actualcount);
			
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	protected void calculateRelevancePerformance()
	{
		//calculating the performance
		try{
			
			double sum_precision=0;
			double sum_recall=0;
			double sum_fmeasure=0;
			int actualcount=0;
			
			for(int i=1;i<=250;i++){
				double precision=0;
				double recall=0;
				double fmeasure=0;
				
				if(isStackOverflow(i))continue;
				
				//if(i==21||i==15 || i==16 || i==17)continue;
				//if(i==184)continue;
				String extFileName=StaticData.CE_Data_Home+"/source/coll500/relevant-text/"+i+".txt";
				String extractedContent=ContentLoader.loadFileContent(extFileName);
				String goldFileName=StaticData.CE_Data_Home+"/source/coll500/gold-relevant-text/"+i+".txt";
				String goldcontent=ContentLoader.loadFileContent(goldFileName);
				try{
				
					//added
					ArrayList<String> gold=tokenizeContent(goldcontent.trim());
					ArrayList<String> extracted=tokenizeContent(extractedContent.trim());
					WordLCS lcsmaker=new WordLCS(gold, extracted);
					int lcslength=lcsmaker.getLCS_Dynamic(gold.size(), extracted.size()).size();
					if(extracted.size()==0){
						precision=0;
						recall=0;
					}else{
						precision=(double)lcslength/extracted.size();
						recall=(double)lcslength/gold.size();
					}
					//added
					
					
				/*LCS lcsmaker=new LCS(goldcontent.trim(), extractedContent.trim());
				String lcs=lcsmaker.getLCS();
				//LCS lcs=new LCS();
				//int lcslength=lcs.getLCSLength(goldcontent, extractedContent, goldcontent.length()-1, extractedContent.length()-1);
				
				if(extractedContent.length()==0)precision=0;
				//else precision=((double)lcslength/extractedContent.length());
				//recall=((double)lcslength/goldcontent.length());
				else precision=((double)lcs.length()/extractedContent.length());
				recall=((double)lcs.length()/goldcontent.length());*/
				
				
				if(precision>0 || recall>0)
				fmeasure=2*precision*recall/(precision+recall);
				else fmeasure=0;
				
				//System.out.println(i +" Precision:"+precision+" Recall:"+recall+" Fmeasure:"+fmeasure);
				System.out.println(i +" "+precision+" "+recall);
				
				sum_precision+=precision;
				sum_recall+=recall;
				sum_fmeasure+=fmeasure;
				actualcount++;
				//Thread.sleep(1000);
				}catch(Exception e){
				}
			}
			
			System.out.println("Performance on relevant content extraction");
			System.out.println("Avg precision:"+sum_precision/actualcount);
			System.out.println("Avg recall:"+sum_recall/actualcount);
			System.out.println("Avg f-measure:"+sum_fmeasure/actualcount);
			System.out.println("Actual count:"+actualcount);
			
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	
	protected void loadIndexURLs()
	{
		//loading the exceptions
		String indexFile=StaticData.CE_Data_Home+"/source/sourceIndex.txt";
		String content=ContentLoader.loadFileContent(indexFile);
		String[] lines=content.split("\n");
		for(String line:lines){
			String[] parts=line.trim().split("\\s+");
			int ID=Integer.parseInt(parts[0]);
			String url=parts[2].trim();
			indexURLs.put(ID, url);
		}
	}
	protected boolean isStackOverflow(int ID)
	{
		//check if it is a SO link
		boolean istack=false;
		String url=indexURLs.get(ID);
		if(url.contains("stackoverflow.com"))
			istack=true;
		return istack;
	}
	
	protected void makeGoldText()
	{
		//preparing gold text
		String goldHtml=StaticData.CE_DATA_HOME_500+"/gold-relevant-html";
		String goldText=StaticData.CE_DATA_HOME_500+"/gold-relevant-text";
		//String goldHtml=StaticData.CE_DATA_HOME_500+"/gold-relevant-html";
		//String goldText=StaticData.CE_DATA_HOME_500+"/gold-relevant-text";
		
		int done=0;
		for(int i=1;i<=250;i++){
				try {
					String _goldHtml=goldHtml+"/"+i+".html";
					//String _goldText=goldText+"/"+i+".html";
					File f2=new File(_goldHtml);
					Document doc=Jsoup.parse(f2,"UTF-8");
					String fileName=goldText+"/"+f2.getName().split("\\.")[0]+".txt";
					FileWriter fwriter=new FileWriter(new File(fileName));
					fwriter.write(doc.text());
					fwriter.close();
					done++;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(OutOfMemoryError e){
					System.err.println("cannot process:"+i);
				}
		}
		System.out.println("Done for "+done);
	}
	
	public static void main(String[] args){
		//new PerformanceManager().makeGoldText();
		new PerformanceManager().calculatePerformance();
		//new PerformanceManager().calculateRelevancePerformance();
		//checkLCS(73);
	}
	

}
