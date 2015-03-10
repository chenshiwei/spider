package com.hta.webmagic.processor.bucket141212;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.JMException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.Result;
import com.hta.webmagic.pipeline.MysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImage;
import com.hta.webmagic.processor.utils.IOUtil;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regexParser;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

public class Hexun implements PageProcessor, Runnable {

  private String seedUrl = "http://iof.hexun.com/";
  private String siteNmae = "和讯金融";
  private int stopDeep = 3;
  private static String stopDeepStr = "3";
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket_finance";
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);
  private static String PageUrl = null;

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links()
        .regex("(http://(stock|funds|gold|forex|bank|insurence|futures|bond|news).hexun.com[^#]*)")
        .all();
    // for (String temp : links) {
    // System.out.println("new url :" + temp);
    // }
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(stopDeepStr); 
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
//    System.out.println("get url\t" + page.getUrl().toString());
    PageUrl = page.getUrl().toString();
    if (PageUrl.matches("http://.*hexun.com/\\d*-\\d*-\\d*/\\d*.html")) {
      Result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite(siteNmae);
      rs.setDeep(deep - 1);
      rs = getDocument(rs, code);
      rs.setTable(table);
      page.putField("result", rs);
      Showinfo.printresult(rs);

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
    Elements Title = doc.select("div[class=art_title] > h1");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("div.art_context");
    ImageResult imgresult = FindImage.fliterimg(Content, site.getDomain(),  localpath, localurl,
        PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setCategory(findTag(html));
    rs.setImage(imgresult.getImgList());
    String time = doc.select("span[id=pubtime_baidu]").eq(0).text();

    System.out.println("time1:" + time);
    // String regex = ".*(\\d{4})年(\\d{1,2})月(\\d{1,2})日.*";
    // String val = null;
    // Pattern pt = Pattern.compile(regex);
    // Matcher m = pt.matcher(time);
    // while (m.find()) {
    // // val = m.group(1);
    // val = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
    // }
    // System.out.println("time2:" + val);
    if (time.length() > 10) {
      // rs.setTimeD(Timer.findDate(val));
      time = time.substring(0, 9);
      rs.setTimeDate(Timer.findDate(time));
    }

    return rs;
  }

  private static String findTag(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("div#page_navigation");
    String tags = trsid.text();
    return tags;

  }

  @Override
  public Site getSite() {
    return site;
  }

  public static void main(String[] args) throws JMException {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Hexun()).addUrl("http://www.hexun.com/")
    // .addPipeline(new mysqlPipeline())
        .thread(5).run();

  }

  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Hexun()).addUrl("http://www.hexun.com/").addPipeline(new MysqlPipeline())
        .thread(5).run();
  }
}
