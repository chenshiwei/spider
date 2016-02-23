package com.hta.webmagic.processor.bucket141202;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.JMException;

import org.apache.http.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.Result;
import com.hta.webmagic.pipeline.MysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regexParser;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

public class Cctime implements PageProcessor, Runnable {
  private String seedUrl = "http://mobile.cctime.com/";
  private String siteNmae = "飞象手机";
  private static String StopDeepStr = null;
  private int stopDeep = 3;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141202";
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links().regex(UrlFilter.siteRangeFliter(seedUrl)).all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(StopDeepStr);
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    System.out.println(" filter url" + page.getUrl().toString());
    String code = page.getRawText();
    if (page.getUrl().toString()
        .matches("http://mobile.cctime.com/html/\\d{4}-\\d{2}-\\d{2}/[^>]*")) {
      Result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite(siteNmae);
      rs.setDeep(deep - 1);
      rs = getDocument(rs, code, page.getUrl().toString());
      rs.setTable(table);
      page.putField("result", rs);
      // showinfo.printresult(rs);

    } else {
      page.setSkip(true);
    }

  }

  /**
   * 
   * @param rs
   * @param html
   * @return
   */
  private static Result getDocument(Result rs, String html, String pagrurl) {

    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements Title = doc.select("td[style=color:#2846A0;font-size:16px;font-weight:bolder;]");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("div[class=art_content]");
    ImageResult imgresult = FindImage.fliterimgT(Content, site.getDomain(), localpath, localurl,
        pagrurl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    String time = doc.select("td[class=dateAndSource]").text();
    rs.setTimeDate(Timer.findDate(time, "\\d*年\\d*月\\d*日 \\d*:\\d*"));
    return rs;
  }

  public static String findTime(String htmlcode, String regex) {
    String val = null;
    Pattern pt = Pattern.compile(regex);
    Matcher m = pt.matcher(htmlcode);
    while (m.find()) {
      val = m.group(1);
    }
    return val;
  }

  /**
   * 
   * @param htmlcode
   *          regex timeString
   * @return Date
   */
  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("time[class=timestamp]");
    String time = trsid.attr("time");
    return Timer.findDate(time);
  }

  @Override
  public Site getSite() {
    return site;
  }

  public static void main(String[] args) {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Cctime())
        .addUrl("http://mobile.cctime.com/html/2014-12-24/20141224119574344.htm")
        .addPipeline(new MysqlPipeline()).thread(5).run();

  }

  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Cctime()).addUrl("http://mobile.cctime.com/")
        .addPipeline(new MysqlPipeline()).thread(5).run();
  }
}
