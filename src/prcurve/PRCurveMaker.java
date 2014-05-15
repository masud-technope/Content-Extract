package prcurve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import utility.ContentLoader;
import core.StaticData;

public class PRCurveMaker {
	
	public static void main(String[] args)
	{
		String scoreFile=StaticData.CE_DATA_HOME_500+"/roc/tccb-main.txt";
		String content=ContentLoader.loadFileContent(scoreFile);
		String[] lines=content.split("\n");
		double fpr_sum=0;
		double rec_sum=0;
		ArrayList<TFR> list=new ArrayList<>();
		for(int i=0;i<500;i++){
			String[] parts=lines[i].split("\\s+");
			double recall=Double.parseDouble(parts[1].trim());
			double fpr=Double.parseDouble(parts[2].trim());
			if((i+1)%50==0){
				//System.out.println(fpr_sum/(i+1)+" "+rec_sum/(i+1));
				TFR tfr=new TFR();
				tfr.fpr=fpr_sum/(i+1);
				tfr.recall=rec_sum/(i+1);
				list.add(tfr);
				//rec_sum=recall;
				//fpr_sum=fpr;
			}
				rec_sum+=recall;
				fpr_sum+=fpr;
		}
		Collections.sort(list, new Comparator<TFR>() {
			@Override
			public int compare(TFR o1, TFR o2) {
				// TODO Auto-generated method stub
				Double v1=o1.fpr;
				Double v2=o2.fpr;
				return v1.compareTo(v2);
			}
		});
		for(TFR obj:list){
			System.out.println(obj.fpr+" "+obj.recall);
		}
		
	}
}
class TFR
{
	double fpr;
	double recall;
	
}
