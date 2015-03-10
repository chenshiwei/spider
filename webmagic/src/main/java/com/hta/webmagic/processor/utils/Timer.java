package com.hta.webmagic.processor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.utils.DateUtil;

import com.hta.webmagic.regex.regex;
/**
 * 
 * @author xuexianwu
 *
 */
public final class Timer {
  private Timer(){
    
  }
  public static String findTime(String htmlString, String TimeRegex) {
    String time = regex.findTime(htmlString, TimeRegex);
    // System.out.println("regex time " + time);
    try {
      Date result = DateUtil.String2Date(time);
      SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      time = t.format(result);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return time;
  }

  public static Date findDate(String htmlString, String DateRegex) {
    String time = regex.findTime(htmlString, DateRegex);
    Date result = null;
    try {
      result = DateUtil.String2Date(time);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }

  public static Date findDate(String time) {
    Date result = null;
    try {
      result = DateUtil.String2Date(time);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }

  public static String findMixTime(String htmlString, String TimeRegex, String dayRegex,
      String hourRegex) {
    String time = regex.findTime(htmlString, TimeRegex);
    if (!(time == null) && !time.equals("")) {
       return "";
    } else {
      time = regex.findTime(htmlString, dayRegex);
      System.out.println("regex daytime " + time);
      if (!(time == null) && !time.equals("")) {
        Date nowTime = new Date();
        SimpleDateFormat ti = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = ti.format(nowTime);
      } else {
        System.out.println("regex hourtime " + time);
        time = regex.findTime(htmlString, hourRegex);
        if (!(time == null) && !time.equals("")) {
          Date nowTime = new Date();
          SimpleDateFormat ti = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          time = ti.format(nowTime);
        }

      }
    }
    System.out.println("regex last  time " + time);
    if (!(time == null) && !time.equals("")) {
      try {
        Date result = DateUtil.String2Date(time);
        SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = t.format(result);
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return time;
  }

}
