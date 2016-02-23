package com.hta.webmagic.processor.bucket141111;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.result;
import com.hta.webmagic.pipeline.mysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImgage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.showinfo;
import com.hta.webmagic.regex.regexParser;

public class gzdashuju_com implements PageProcessor, Runnable {
  private String seedUrl = "http://www.gzdashuju.com/";
  private String siteNmae = "贵州大数据";
  private static String StopDeepStr = null;
  private int stopDeep = 2;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "";
  private static int timeOut = 10000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141111";
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
    String code = page.getRawText();
    if (page.getUrl().toString().matches("http://www.gzdashuju.com/info-\\d*-\\d*.html")) {
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
    Elements Title = doc.select("div.m-pg-tt");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("div.m-pg-bd");
    // System.out.println("\t" + Content.html());
    ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
    rs.setContent(ChangeP.htmlHandGzdashuju(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    rs.setTimeD(findTime(html));
    return rs;
  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("div.m-pg-info");
    String time = trsid.text();
    return Timer.findDate(time, "\\d{4}-\\d{2}-\\d{2} \\d*:\\d*:\\d*");
  }

  private static String findSource(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("div.body_time");
    String source = "";
    return null;

  }

  @Override
  public Site getSite() {
    // TODO Auto-generated method stub
    return site;

  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new gzdashuju_com()).addUrl(seedUrl).addPipeline(new mysqlPipeline()).thread(3)
        .run();

  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new gzdashuju_com()).addUrl("http://www.gzdashuju.com/info-5-1671.html")
        .addPipeline(new mysqlPipeline()).thread(5).run();

  }

}
