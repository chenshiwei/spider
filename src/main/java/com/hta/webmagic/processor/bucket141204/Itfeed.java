package com.hta.webmagic.processor.bucket141204;

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

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.Result;
import com.hta.webmagic.pipeline.DBOPerate;
import com.hta.webmagic.pipeline.MysqlPipeline;
import com.hta.webmagic.processor.bucket141203.Enet;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

public class Itfeed implements PageProcessor, Runnable {
  private String seedUrl = "http://www.itfeed.com/";
  private String siteNmae = "电子商务新闻媒体";
  private int stopDeep = 3;
  private static String StopDeepStr = null;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141204";
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
    if (PageUrl.matches("http://www.itfeed.com/p/\\d*.html")) {
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
    Elements Title = doc.select("h1.tit");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("div.con");
    ImageResult imgresult = FindImage.fliterimg(Content, site.getDomain(), localpath, localurl,
        PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    String time = doc.select("div.meta").text();
    System.out.println("time1:" + time);
    String regex = ".*(\\d{4})-(\\d{2})-(\\d{2}) .*";
    String val = null;
    Pattern pt = Pattern.compile(regex);
    Matcher m = pt.matcher(time);
    while (m.find()) {
      // val = m.group(1);
      val = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
    }
    System.out.println("time2:" + val);
    rs.setTimeDate(Timer.findDate(val));
    return rs;
  }

  @Override
  public Site getSite() {
    // TODO Auto-generated method stub
    return site;
  }

  private static Date findTime(String html) {
    Date date = null;
    String time = regex.findTime(findBlock(html), "\\d{4}-\\d*-\\d* \\d*:\\d*:\\d*");
    // System.out.println("regex time " + time);
    if (time != null && !time.equals("")) {
      try {
        date = DateUtil.String2Date(time);
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return date;
  }

  private static String findBlock(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements Title = doc.select("div.meta");
    String title = Title.text();
    return title;
  }

  public static void main(String[] args) {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");

    Spider.create(new Itfeed()).addUrl("http://www.itfeed.com/").thread(5).run();
  }

  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Itfeed()).addPipeline(new MysqlPipeline()).addUrl("http://www.itfeed.com/")
        .thread(5).run();
  }

}
