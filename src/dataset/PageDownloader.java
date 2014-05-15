package dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

import core.StaticData;

public class PageDownloader {
	
	public static void main(String[] args){
	
		String link="http://www.eclipse.org/forums/index.php/t/185439/";
		try{
		URL u=new URL(link);
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(u.openStream()));
		String  content=new String();
		int value=0;
		while((value=bufferedReader.read())!=-1)
		{
			char c=(char)value;
			content+=c;
		}
		bufferedReader.close();
		String fileName=StaticData.CE_Data_Home+"/source/coll500/gold-html/153.html";
		FileWriter writer=new FileWriter(new File(fileName));
		writer.write(content);
		writer.close();
		
		System.out.println("File saved successfully.");
		
		}catch(Exception exc){
			//handle the exception
		}
		
	}
}
