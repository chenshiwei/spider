package com.hta.webmagic.regex;

public class htmlfliter {
	public static String Encode(String str)        
	{            
		str = str.replace("'", "''");            
		str = str.replace("\"", "&quot;");            
		str = str.replace("<", "&lt;");            
		str = str.replace(">", "&gt;");            
		str = str.replace("\n", "<br>");            
		str = str.replace("“", "&ldquo;");            
		str = str.replace("”", "&rdquo;");            
		return str;        
	} 
	/// 取SQL值时还原字符
	public static String Decode(String str)        
	{            
		str = str.replace("&rdquo;", "”");            
		str = str.replace("&ldquo;", "“");            
		str = str.replace("<br>", "\n");            
		str = str.replace("&gt;", ">");            
		str = str.replace("&lt;", "<");            
		str = str.replace("&quot;", "\"");            
		str = str.replace("''", "'");           
		return str;        
	}
}
