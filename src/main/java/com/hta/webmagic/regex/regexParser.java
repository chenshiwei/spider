package com.hta.webmagic.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hta.webmagic.model.Result;

public class regexParser {

  public static void main(String args[]) throws Exception {

  }

  public static Result parserkeywords(String code) {
    Result page = new Result();
    String regex = "<meta name=\"(d|D)escription\" content=";
    String regex1 = "(<meta name=\"(d|D)escription\" content=([^>]*\\s*>))";
    String regex2 = "(<meta name=\"(k|K)eywords\" content=\"([^>]*\\s*>))";
    String regexAuthor = "作者[^<]*";
    Pattern p = Pattern.compile(regex1, Pattern.UNICODE_CASE);
    Pattern p2 = Pattern.compile(regex2, Pattern.UNICODE_CASE);
    Pattern p3 = Pattern.compile(regexAuthor, Pattern.UNICODE_CASE);
    Matcher m = p.matcher(code);
    Matcher m2 = p2.matcher(code);
    Matcher m3 = p2.matcher(code);
    String val = null;
    // System.out.println("INPUT: " + candidate);
    // System.out.println("REGEX: " + regex + "\r\n");
    while (m.find()) {
      val = m.group();
      int f = val.indexOf("t=");
      int n = val.indexOf(">");
      val = val.substring(f + 3, n - 2);
      // System.out.println("Description: " + val);
      page.setDescription(val.trim());
    }

    while (m2.find()) {
      val = m2.group();
      int f = val.indexOf("t=\"");
      int n = val.indexOf(">");
      if ((f + 3) < (n - 2)) {
        val = val.substring(f + 3, n - 3);
        // System.out.println("keywords: " + val);
        page.setKeywords(val);
      } else {
        page.setKeywords("keywords");
      }

    }

    if (val == null) {
      System.out.println("keywords error");

    }
    while (m3.find()) {
      val = m3.group();
      int f = val.indexOf("t=\"");
      int n = val.indexOf(">");
      if ((f + 3) < (n - 2)) {
        val = val.substring(f + 3, n - 3);
        // System.out.println("Author: " + val);
        // page.setKeywords(val);
      }

    }
    return page;
  }

}
