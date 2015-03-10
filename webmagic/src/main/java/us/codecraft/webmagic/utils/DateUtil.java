package us.codecraft.webmagic.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class DateUtil {
	private static String[] DEFAULT_FROMAT = { "yyyy-MM-dd HH:mm:ss.SSS",
			"yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日HH:mm","yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyy-MM",
			"yyyy年MM月dd HH时mm分ss秒", "yyyy年MM月dd HH时mm分", "yyyy年MM月dd日",
			"yyyy年MM月dd", "yyyy年MM月dd日 HH:mm","yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss a", "yy-MM-dd HH:mm:ss",
			"yy-MM-dd HH:mm","yyyy-MM-dd HH:mm", "yy-MM-dd", "yy-MM", "MM-dd",
			"yy/MM/dd HH:mm:ss", "yy/MM/dd HH:mm", "yy/MM/dd",
			"MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss","MM/dd HH:mm", "MM/dd" };

	public static Date String2Date(String s) throws ParseException {
		Date date = String2Date(s, DEFAULT_FROMAT);
		return date;
	}

	public static String formatDate(String s, Date date) {
		SimpleDateFormat sdf;
		try {
			sdf = new SimpleDateFormat(s);
		} catch (IllegalArgumentException e) {
			// SimpleDateFormat sdf;
			e.printStackTrace();
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			System.out.println("ERROR:日期格式错误，采用默认格式yyyy-MM-dd HH:mm:ss SSS");
		}
		if (date != null) {
			return sdf.format(date);
		}
		System.out.println("ERROR:日期错误，采用当前实时日期");
		return sdf.format(new Date());
	}

	public static Date String2Date(String date, String[] dateFormat)
			throws ParseException {
		Date parsedDate = new Date();
		parsedDate = DateUtils.parseDate(date, dateFormat);
		return parsedDate;
	}

	public static Date getMondayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayofweek = c.get(7) - 1;
		if (dayofweek == 0) {
			dayofweek = 7;
		}
		c.add(5, -dayofweek + 1);
		return c.getTime();
	}

	public static Date getSundayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayofweek = c.get(7) - 1;
		if (dayofweek == 0) {
			dayofweek = 7;
		}
		c.add(5, -dayofweek + 7);
		return c.getTime();
	}

	public static Date getLastDateOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int days = cal.getActualMaximum(5);
		cal.set(5, days);
		return cal.getTime();
	}

	public static Date getFristDateOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int days = cal.getActualMinimum(5);
		cal.set(5, days);
		return cal.getTime();
	}

	public static Date getNextDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(5, 1);
		return cal.getTime();
	}

	public static Date getBeforeDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(5, -1);
		return cal.getTime();
	}

	public static Date getLastTimeOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(11, 23);
		cal.set(12, 59);
		cal.set(13, 59);
		cal.set(14, 999);
		return cal.getTime();
	}

	public static Date getBeginTimeOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);
		return cal.getTime();
	}

	public static Date getBeginTimeOfHour(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);
		return cal.getTime();
	}

	public static Date getLastTimeOfHour(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(12, 59);
		cal.set(13, 59);
		cal.set(14, 999);
		return cal.getTime();
	}

	private static SimpleDateFormat ft5 = new SimpleDateFormat(
			"yyyyMMddHHmmssSSS");

	public static String getStringFt5(Date date) {
		String newDate = ft5.format(date);
		return newDate;
	}

	private static SimpleDateFormat ft3 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	public static String getStringFt3(Date date) {
		String newDate = ft3.format(date);
		return newDate;
	}

	public static String getStringFt3toFt5(String d) {
		String newDate = "";
		try {
			Date date1 = ft3.parse(d);
			newDate = ft5.format(date1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
	}

	public static Date getDateFromStringWithFormat(String date, String format)
			throws ParseException {
		DateFormat format1 = new SimpleDateFormat(format);
		return format1.parse(date);
	}

	public static int getIntervalDate(Date compDate, Date targetDate) {
		long DAY = 86400000L;
		return (int) ((targetDate.getTime() - compDate.getTime()) / DAY);
	}

	public static boolean isBefore(Date compDate, Date targetDate) {
		return targetDate.getTime() - compDate.getTime() > 0L;
	}

	public static int getWeekOfYear(Date date) {
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.setFirstDayOfWeek(2);
		int week = cl.get(3);
		cl.add(5, -7);
		int year = cl.get(1);
		if (week < cl.get(3)) {
			year++;
		}
		return week;
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(getWeekOfYear(new Date()));
	}
}
