package com.hta.webmagic.processor.bucket141112;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.InitMyQueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.Result;
import com.hta.webmagic.pipeline.MysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regexParser;

/**
 * 
 * 
 */
public class FinanceSinaProcessor implements PageProcessor, Runnable {
  private String seedUrl = "http://finance.sina.com.cn/";
  private String siteNmae = "新浪财经";
  private static String stopDeepStr;
  private int stopDeep = 2;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141112";
  private static String pageUrl;
  private static Site site = Site.me().setDomain("finance.sina.com.cn").setTimeOut(timeOut)
      .setRetryTimes(retryTimes);
  protected String uuid;

  @Override
  public void process(Page page) {
    List<String> links = page.getHtml().links().regex(UrlFilter.siteRangeFliter(seedUrl)).all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(stopDeepStr);
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    pageUrl = page.getUrl().toString();
    if (pageUrl.matches("http://finance.sina.com.cn/[a-z/]*/[0-9]{8}/[0-9]*.shtml")) {
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
    Elements titleElements = doc.select("h1#artibodyTitle");
    String title = titleElements.text();
    rs.setTitle(title);

    Elements contentElements = doc.select("div#artibody");
    ImageResult imgresult = FindImage.fliterimg(contentElements, site.getDomain(), localpath,
        localurl, pageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    rs.setTimeDate(findTime(html));
    return rs;

  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("span#pub_date");
    String time = trsid.text();
    // System.out.print("time" + time);
    return Timer.findDate(time);
  }

  @Override
  public Site getSite() {
    // TODO Auto-generated method stub
    return site;

  }

  @Override
  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new FinanceSinaProcessor()).addUrl(seedUrl).addPipeline(new MysqlPipeline())
        .thread(5).run();

  }

  public static void main(String[] args) {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    stopDeepStr = pro.getProperty("DEEP");
    ArrayList<Request> history = new ArrayList<Request>();

    history.add(new Request("http://finance.sina.com.cn/", 1));
    Scheduler test = new InitMyQueueScheduler();
    Task task = null;
    test.addUrlList(history, task);
    Spider.create(new FinanceSinaProcessor()).setScheduler(test)
        .addUrl("http://finance.sina.com.cn/").addPipeline(new MysqlPipeline()).thread(2).run();

  }

}
