package com.hta.webmagic.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class timeunit {
	public static String timefrom(String time) {
		String regex = "[0-9]{4}/[0-9]*/[0-9]* [0-9]*:[0-9]*";
		String val = "time";
		Pattern pt = Pattern.compile(regex, Pattern.UNICODE_CASE);
		Matcher m = pt.matcher(time);
		while (m.find()) {
			val = m.group();
		}
		return val;
	}

}
