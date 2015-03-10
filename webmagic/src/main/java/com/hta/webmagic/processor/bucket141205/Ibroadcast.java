package com.hta.webmagic.processor.bucket141205;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.Result;
import com.hta.webmagic.pipeline.MysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

public class Ibroadcast implements PageProcessor, Runnable {

  private String seedUrl = "http://www.ibroadcast.com.cn/";
  private String siteNmae = "i播";
  private int stopDeep = 3;
  private static String StopDeepStr = null;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141205";
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);
  private static String PageUrl = null;

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links().regex(UrlFilter.siteRangeFliter(seedUrl)).all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(StopDeepStr);// 获取配置deep
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    PageUrl = page.getUrl().toString();
    if (PageUrl.matches("http://www.ibroadcast.com.cn/article/\\d*/")) {
      Result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite(siteNmae);
      rs.setDeep(deep - 1);
      rs = getDocument(rs, code);
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
  private static Result getDocument(Result rs, String html) {

    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements Title = doc.select("div[id=info] > h1");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("div#text");
    ImageResult imgresult = FindImage.fliterimg(Content, site.getDomain(), localpath, localurl,
        PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    String time = doc.select("div[id=info] > p > em").get(1).text();
    if (time.contains("天")) {
      String regex = "发布于：(\\d{1,3}).*";
      String val = null;
      Pattern pt = Pattern.compile(regex);
      Matcher m = pt.matcher(time);
      while (m.find()) {
        val = m.group(1);
      }
      time = val;
    }

    long day = Integer.parseInt(time);
    Date d = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    System.out.println();
    String crawl_date = df.format(new Date(d.getTime() - day * 24 * 60 * 60 * 1000));
    System.out.println("发布的日期：" + crawl_date);
    rs.setTimeDate(Timer.findDate(crawl_date));
    return rs;
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
    String time = trsid.attr("datetime");
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
    // TODO Auto-generated method stub
    Spider.create(new Ibroadcast()).addUrl("http://www.ibroadcast.com.cn")
    // .addPipeline(new mysqlPipeline())
        .thread(15).run();
  }

  @Override
  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    // TODO Auto-generated method stub
    Spider.create(new Ibroadcast()).addUrl("http://www.ibroadcast.com.cn")
        .addPipeline(new MysqlPipeline()).thread(5).run();

  }

}
