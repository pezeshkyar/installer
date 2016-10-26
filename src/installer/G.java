package installer;

public class G {
	public static int utf8StrtoInt(String str){
		int res = 0;
		int digit = 0;
		char[] chars = str.toCharArray();
		
		for(int i=0; i < chars.length; i++){
			if(chars[i] >= '0' && chars[i] <= '9'){
				digit = chars[i] - '0';
				res = res * 10 + digit;
			} 
		}
		
		return res;
	}
}
