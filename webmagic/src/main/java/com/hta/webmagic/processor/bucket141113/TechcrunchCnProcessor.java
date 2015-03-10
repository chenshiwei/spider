package com.hta.webmagic.processor.bucket141113;

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
import com.hta.webmagic.model.Result;
import com.hta.webmagic.pipeline.MysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.regex.regexParser;

/**
 * 
 * @author xuexianwu version 1.12<br>
 *         download picture and thumbnail
 *
 */
public class TechcrunchCnProcessor implements PageProcessor, Runnable {
  private String seedUrl = "http://techcrunch.cn/";
  private String siteNmae = "techcrunch";
  private static String stopDeepStr = "2";
  private int stopDeep = 3;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 9000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141113";
  private static String pageUrl = null;
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links().regex(UrlFilter.siteRangeFliter(seedUrl)).all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(stopDeepStr);
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    pageUrl = page.getUrl().toString();
    if (pageUrl.toString().matches("http://techcrunch.cn/\\d*/\\d*/\\d*/[^>]*")) {
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
    Elements titleElements = doc.select("h1.alpha");
    String title = titleElements.text();
    rs.setTitle(title);

    Elements contentElements = doc.select("div.article-entry");
    ImageResult imgresult = FindImage.fliterimg(contentElements, site.getDomain(), localpath,
        localurl, pageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());

    rs.setTimeDate(findTime(html));
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
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new TechcrunchCnProcessor()).addUrl(seedUrl).addPipeline(new MysqlPipeline())
        .thread(5).run();

  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new TechcrunchCnProcessor()).addUrl("http://techcrunch.cn/")
        .addPipeline(new MysqlPipeline()).thread(2).run();

  }

}
