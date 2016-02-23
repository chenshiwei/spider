package com.hta.webmagic.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class parserCharset {
	public static String regexCharset(String code) {
		String charset = null;
		String regex = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=[0-9a-z-]*";
		String regx = "<meta charset=[0-9a-z-]*";

		Pattern pt = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
		Matcher m = pt.matcher(code);
		while (m.find()) {
			charset = m.group();
		}
		int d = charset.indexOf("set=");
		charset = charset.substring(d + 4);
		System.out.println("charset is \t" + charset);
		return charset;
	}
}
