package com.hta.webmagic.model;

import java.util.Calendar;
import java.util.Date;
/**
 * 
 * @author xuexianwu
 *
 */
public class DateResult {
  private String year;
  private String mouth;
  private String date;
  private Date todate;

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getMouth() {
    return mouth;
  }

  public void setMouth(String mouth) {
    this.mouth = mouth;
  }

  public String getDate() {

    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Date getTodate() {
    this.todate = new Date();
    return todate;
  }

  public void setTodate(Date todate) {
    this.todate = todate;
  }

  @SuppressWarnings("static-access")
  public static boolean checkDate(Date date, int previousday) {
    boolean result = false;
    Calendar cal = Calendar.getInstance();
    cal.add(cal.DATE, -previousday);
    Date contrast = cal.getTime();
    if (date.after(contrast)) {
      result = true;
    }
    return result;
  }

}
