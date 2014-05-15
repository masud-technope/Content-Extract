package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import core.StaticData;

public class DSSolutionManager {

	public static void main(String[] args){
		String folderName=StaticData.CE_Data_Home+"/solution";
		int solcount=0;
		for(int i=1;i<=150;i++){
			String fileName=folderName+"/"+i+".txt";
			try {
				Scanner scanner=new Scanner(new File(fileName));
				while(scanner.hasNext()){
					String line=scanner.nextLine();
					if(line.startsWith("http")){
						solcount++;
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Total solution:"+solcount);
	}
}
