package com.hta.webmagic.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regexAuthor {
	public static String findAuthor(String time) {
		String regex = "作者：[^&nbsp;]*";
		String val = "Author";
		Pattern pt = Pattern.compile(regex, Pattern.UNICODE_CASE);
		Matcher m = pt.matcher(time);
		while (m.find()) {
			val = m.group();
			int f = val.indexOf("：");
			// int n = val.indexOf(">");
			val = val.substring(f + 1);
//			System.out.println(f + "Author: " + val);
		}
		return val;
	}

	public static String findSource(String htmlcode) {
		String regex = "来源：[^<]*";
		String val = "Source";
		Pattern pt = Pattern.compile(regex, Pattern.UNICODE_CASE);
		Matcher m = pt.matcher(htmlcode);
		while (m.find()) {
			val = m.group();
			int f = val.indexOf("：");
			// int n = val.indexOf(">");
			val = val.substring(f + 1);
//			System.out.println(f + "Source: " + val);
		}
		return val;
	}

}
