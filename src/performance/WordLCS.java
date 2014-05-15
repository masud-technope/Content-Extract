package performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import net.barenca.jastyle.ASFormatter;
import net.barenca.jastyle.FormatterHelper;

public class WordLCS {
	ArrayList<String> seq1;
	ArrayList<String> seq2;
	int matrix[][];
	
	public WordLCS(ArrayList<String> code1, ArrayList<String> code2)
	{
		//assigning array lists
		this.seq1=new ArrayList<String>();
		this.seq1=code1;
		this.seq2=new ArrayList<String>();
		this.seq2=code2;
		matrix=new int[this.seq1.size()+1][this.seq2.size()+1];
	}
	
	public ArrayList<String> getLCS(int len1, int len2)
	{
		// code for getting LCS
		String last1 = new String();
		String last2 = new String();

		ArrayList<String> lcs1 = new ArrayList<String>();
		ArrayList<String> lcs2 = new ArrayList<String>();

		int lenlcs1, lenlcs2;

		if (len1 <= 0 || len2 <= 0)
			return new ArrayList<String>();

		last1 = this.seq1.get(len1 - 1);
		last2 = this.seq2.get(len2 - 1);

		if (last1.equals(last2)) {
			ArrayList<String> temp = getLCS(len1 - 1, len2 - 1);
			temp.add(last1);
			return temp;
		} else {
			lcs1 = getLCS(len1, len2 - 1);
			lcs2 = getLCS(len1 - 1, len2);
			lenlcs1 = lcs1.size();
			lenlcs2 = lcs2.size();
			return lenlcs1 > lenlcs2 ? lcs1 : lcs2;
		}
	}
	
	
	public ArrayList<String> getLCS_Dynamic(int len1, int len2) {
		// code for getting LCS with dynamic programming
		ArrayList<String> mylcs = new ArrayList<String>();
		for (int i = len1 - 1; i >= 0; i--) {
			for (int j = len2 - 1; j >= 0; j--) {
				if (this.seq1.get(i).toString().equals(this.seq2.get(j).toString())) {
					matrix[i][j] = matrix[i + 1][j + 1] + 1;
				} else {
					matrix[i][j] = Math.max(matrix[i + 1][j], matrix[i][j + 1]);
				}
			}
		}
		int i = 0, j = 0;
		while (i < len1 && j < len2) {
			if (this.seq1.get(i).toString().equals(this.seq2.get(j).toString())) {
				mylcs.add(this.seq1.get(i).toString());
				i++;
				j++;
			} else if (matrix[i + 1][j] >= matrix[i][j + 1])
				i++;
			else
				j++;
		}

		// returning LCS
		return mylcs;
	}
	
	
	protected static ArrayList<String> getTokenized(String code)
	{
		// code for getting tokens of a code fragment
		String tcode=format_the_code(code);
		String fcode=remove_code_comment(tcode);
		StringTokenizer tokenizer = new StringTokenizer(fcode);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token.trim();
			if (!token.isEmpty()) {
				tokens.add(token);
			}
		}
		return tokens;
	}
	
	public static void show_the_lcs(ArrayList<String> lcs)
	{
		for(String str:lcs)
			System.out.println(str);
		System.out.println("-----------------");
	}
	
	static String load_code_snippet(String fileName)
	{
		String codes = new String();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				codes += line + "\n";
			}
		} catch (Exception exc) {
		}
		return codes;
	}
	
	protected static String remove_code_comment(String codeFragment)
	{
		//code for removing code fragment
		String pattern="//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
		return codeFragment.replaceAll(pattern, "");
	}
	
	protected static String format_the_code(String codeFragment)
	{
		//code for formatting code fragment
		ASFormatter formatter=new ASFormatter();
		Reader in=new BufferedReader(new StringReader(codeFragment));
		formatter.setJavaStyle();
		String formattedCode=FormatterHelper.format(in, formatter);
		return formattedCode;
	}
	
	static protected ArrayList<String> tokenizeContent(String content) {
		// tokenize the content
		ArrayList<String> tokenList = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(content," .,;:?\"\'!/-");
		while (tokenizer.hasMoreTokens()) {
			tokenList.add(tokenizer.nextToken());
		}
		return tokenList;
	}
	

	public static void main(String[] args){
		String str="This is a string? How do you know? Yes, believe me !";
		ArrayList<String> tokens=tokenizeContent(str);
		System.out.println(tokens);
	}
	
	

}
