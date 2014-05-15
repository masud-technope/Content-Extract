package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import core.StaticData;

public class ExcepPointer {
	public static void main(String[] args){
		String indexFile=StaticData.CE_Data_Home+"/source/sourceIndex.txt";
		try {
			Scanner scanner=new Scanner(new File(indexFile));
			while(scanner.hasNext())
			{
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
