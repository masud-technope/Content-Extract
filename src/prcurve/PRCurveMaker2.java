package prcurve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import utility.ContentLoader;
import core.StaticData;

public class PRCurveMaker2 {
	public static void main(String[] args)
	{
		String scoreFile=StaticData.CE_DATA_HOME_500+"/pccurve/tccb-main.txt";
		String content=ContentLoader.loadFileContent(scoreFile);
		String[] lines=content.split("\n");
		double pre_sum=0;
		double rec_sum=0;
		ArrayList<PRC> list=new ArrayList<>();
		for(int i=0;i<500;i++){
			String[] parts=lines[i].split("\\s+");
			double precision=Double.parseDouble(parts[1].trim());
			double recall=Double.parseDouble(parts[2].trim());
			if((i+1)%50==0){
				//System.out.println(fpr_sum/(i+1)+" "+rec_sum/(i+1));
				PRC tfr=new PRC();
				tfr.precision=pre_sum/(i+1);
				tfr.recall=rec_sum/(i+1);
				list.add(tfr);
				//rec_sum=recall;
				//fpr_sum=fpr;
			}
				rec_sum+=recall;
				pre_sum+=precision;
		}
		Collections.sort(list, new Comparator<PRC>() {
			@Override
			public int compare(PRC o1, PRC o2) {
				// TODO Auto-generated method stub
				Double v1=o1.recall;
				Double v2=o2.recall;
				return v1.compareTo(v2);
			}
		});
		for(PRC obj:list){
			System.out.println(obj.recall+" "+obj.precision);
		}		
	}
}
class PRC
{
	double precision;
	double recall;
	
}
