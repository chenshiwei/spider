package com.hta.webmagic.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regex {
	/**
	 * 
	 * @param htmlcode
	 * @param regex
	 * @param lab
	 * @return
	 */
	public static String findLab(String htmlcode, String regex, String lab) {
		// regex = "阅读\\([0-9]*";
		String val = "Author";
		Pattern pt = Pattern.compile(regex, Pattern.UNICODE_CASE);
		Matcher m = pt.matcher(htmlcode);
		while (m.find()) {
			val = m.group();
			int f = val.indexOf(lab);
			// int n = val.indexOf(">");
			val = val.substring(f + lab.length());
			// System.out.println(f + "阅读: " + val);
		}
		return val;
	}

	public static String findKeywords(String htmlcode, String regex) {
		String val = "Keywords";
		Pattern pt = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = pt.matcher(htmlcode);
		while (m.find()) {
			val = m.group();
			int f = val.indexOf("T=\"");
			int n = val.indexOf("\">");
			val = val.substring(f + 3, n);
			// val=val.substring(0, n);
			// System.out.println(f + "结果 " + val);
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
			// System.out.println(f + "Source: " + val);
		}
		return val;
	}

	public static String findTime(String htmlcode,String regex) {
		String val = null;
		Pattern pt = Pattern.compile(regex);
		Matcher m = pt.matcher(htmlcode);
		while (m.find()) {
			     val = m.group();
		}
		return val;
	}
	public static boolean findUpdate(String htmlcode) {
		String regex = "201410";
		boolean result = false;
		Pattern pt = Pattern.compile(regex, Pattern.UNICODE_CASE);
		Matcher m = pt.matcher(htmlcode);
		while (m.find()) {
			result = true;
		}
		return result;
	}

}
