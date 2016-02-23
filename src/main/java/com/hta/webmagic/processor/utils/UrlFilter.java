package com.hta.webmagic.processor.utils;

/**
 * 
 * @author xuexianwu
 * 
 */
public class UrlFilter {
	public static String siteRangeFliter(String seedUrl) {
		return seedUrl + "[^#]*";
	}

	public static String siteRangeFliter(String seedUrl, String symbol) {
		return seedUrl + "[^" + symbol + "]*";
	}

	public static String pageRangeFliter(String seedUrl, String pageUrlRagex) {
		return seedUrl + pageUrlRagex;
	}

}
