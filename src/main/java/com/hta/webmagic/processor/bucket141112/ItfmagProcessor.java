package com.hta.webmagic.processor.bucket141112;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

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

/**
 * 
 * @author xuexianwu
 *
 */
public class ItfmagProcessor implements PageProcessor, Runnable {
  private String seedUrl = "http://www.itf-mag.com/";
  private String siteNmae = "互联网金融";
  private static String stopDeepStr;
  private int stopDeep = 2;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141112";
  private static String PageUrl = null;
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links().regex(UrlFilter.siteRangeFliter(seedUrl)).all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(stopDeepStr);// 获取配置deep
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }

    String code = page.getRawText();
    System.out.println("new url " + page.getUrl().toString());
    PageUrl = page.getUrl().toString();
    if (PageUrl.matches("http://www.itf-mag.com/Detail-[0-9]*.html")) {
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
    Elements titleElements = doc.select("div.channel").select("h1");
    String title = titleElements.text();
    rs.setTitle(title);

    Elements contentElements = doc.select("div.information");
    ImageResult imgresult = FindImage.fliterimg(contentElements, site.getDomain(), localpath,
        localurl, PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    rs.setTimeDate(findTime(html));
    return rs;

  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("div.page");
    String time = trsid.text();
    time = regex.findTime(trsid.html(), "\\d{4}-\\d{2}-\\d{2}");
    Date result = null;
    try {
      result = DateUtil.String2Date(time);
    } catch (ParseException e) {

      e.printStackTrace();
    }
    return result;
  }

  @Override
  public Site getSite() {
    return site;

  }

  @Override
  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new ItfmagProcessor()).addUrl(seedUrl).addPipeline(new MysqlPipeline()).thread(5)
        .run();

  }

  public static void main(String[] args) {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new ItfmagProcessor()).addUrl("http://www.itf-mag.com/")
        .thread(2).run();

  }

}
