package com.hta.webmagic.processor.bucket141204;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.result;
import com.hta.webmagic.pipeline.mysqlPipeline;

import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImgage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.showinfo;
import com.hta.webmagic.regex.regexParser;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class Itsec implements PageProcessor, Runnable {
  private String seedUrl = "http://www.itsec.gov.cn/";
  private String siteNmae = "中国信息安全测评中心";
  private int stopDeep = 3;
  private static String StopDeepStr = null;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141204";
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    // List<String> links = page.getHtml().links()
    // .regex(UrlFilter.siteRangeFliter(seedUrl)).all();
    List<String> links = page.getHtml().links()
        .regex("http://www.itsec.gov.cn/export/sites/itsec.*").all();
    // for(String str:links){
    // System.out.println("link:"+str);
    // }
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(StopDeepStr);// 获取配置deep
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    if (page.getUrl().toString().matches("http://www.itsec.gov.cn/export/sites/itsec/news/.*")) {
      result rs = regexParser.parserkeywords(code);
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
  private static result getDocument(result rs, String html) {

    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements Title = doc.select("h2");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("div[class=paragraph]");
    ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    String time = doc.select("div.artInfo").text();
    System.out.println("time1:" + time);
    String regex = ".*(\\d{4})年 (\\d{2})月(\\d{2})日.*";
    String val = null;
    Pattern pt = Pattern.compile(regex);
    Matcher m = pt.matcher(time);
    while (m.find()) {
      // val = m.group(1);
      val = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
    }
    System.out.println("time2:" + val);
    rs.setTimeD(Timer.findDate(val));
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

    Spider.create(new Itsec())
    // .addPipeline(new FilePipeline("D:\\test1"))
    // .addPipeline(new JDBCPipeline())
    // .addPipeline(new mysqlPipeline())
        .addUrl("http://www.itsec.gov.cn/").thread(5).run();
  }

  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Itsec()).addPipeline(new mysqlPipeline()).addUrl("http://www.itsec.gov.cn/")
        .thread(5).run();
  }

}
