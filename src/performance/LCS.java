package performance;

public class LCS {

	String seq1;
	String seq2;
	int matrix[][];
	int mStart = 0;
	int nStart = 0;
	int mEnd = 0;
	int nEnd = 0;
	String startPart;
	String endPart;
	int len1 = 0;
	int len2 = 0;

	public LCS(String content1, String content2) {
		// assigning array lists
		this.seq1 = content1;
		mEnd = this.seq1.length();
		this.seq2 = content2;
		nEnd = this.seq2.length();
		this.initializeMatrix();
	}
	
	public LCS()
	{
		//default constructor
	}
	
	protected void initializeMatrix() {
		// initializing the matrix
		int start = 0;
		int m_end = this.seq1.length();
		int n_end = this.seq2.length();
		while (start < m_end && start < n_end
				&& seq1.charAt(start) == seq2.charAt(start)) {
			start++;
		}
		this.startPart = this.seq1.substring(0, start);

		while (start < m_end && start < n_end
				&& seq1.charAt(m_end - 1) == seq2.charAt(n_end - 1)) {
			m_end--;
			n_end--;
		}
		this.endPart = this.seq1.substring(m_end, mEnd);

		len1 = m_end - start;
		len2 = n_end - start;

		//System.out.println("Matix dimension:"+len1+" "+len2);
		// efficiently allocating matrix
		matrix = new int[len1 + 1][len2 + 1];
		// initializing indices
		mStart = start;
		mEnd = m_end;
		nStart = start;
		nEnd = n_end;
	}

	public String getLCS() {
		// getting LCS
		String lcs = startPart;
		String middle = getLCS_Dynamic(len1, len2);
		lcs += middle + endPart;
		return lcs;
	}
	
	public int getLCSLength(String X, String Y, int i, int j )
	{
		if(i==0||j==0)return 0;
		if(X.charAt(i)==Y.charAt(j)){
			return getLCSLength(X, Y, --i, --j)+1;
		}
		else{
			return Math.max(getLCSLength(X, Y, i, --j), getLCSLength(X, Y, --i, j));
		}
	}
	
	

	public String getLCS_Dynamic(int len1, int len2) {
		// code for getting LCS with dynamic programming
		String mylcs = new String();
		for (int i = len1 - 1; i >= 0; i--) {
			for (int j = len2 - 1; j >= 0; j--) {
				if (this.seq1.charAt(mStart + i) == this.seq2
						.charAt(nStart + j)) {
					matrix[i][j] = matrix[i + 1][j + 1] + 1;
				} else {
					matrix[i][j] = Math.max(matrix[i + 1][j], matrix[i][j + 1]);
				}
			}
		}
		int i = 0, j = 0;
		while (i < len1 && j < len2) {
			if (this.seq1.charAt(i + mStart) == this.seq2.charAt(j + nStart)) {
				mylcs += this.seq1.charAt(i + mStart);
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

	public static void main(String[] args) {
		String str1="This is a test Masud Rahman";
		String str2="This is not a funny statement I said Rahman";
		LCS lcsmaker=new LCS(str1, str2);
		System.out.println(lcsmaker.getLCS());
		System.out.println(lcsmaker.getLCSLength(str1, str2, str1.length()-1, str2.length()-1));
	}

}
