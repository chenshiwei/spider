package com.hta.webmagic.processor.bucket141114;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

/**
 * 
 * @author xuexianwu
 *
 */
public class TechnodComProcessor implements PageProcessor, Runnable {

  private static Site site = Site.me().setTimeOut(8000).setRetryTimes(3);
  private static String StopDeepStr = null;
  private int StopDeep = 3;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static String PageUrl = null;

  @Override
  public void process(Page page) {
    List<String> links = page.getHtml().links().regex("http://cn.technode.com[^#]*").all();
    int deep = page.getRequest().getDeep() + 1;
    StopDeep = Integer.parseInt(StopDeepStr);
    if (deep < StopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    PageUrl = page.getUrl().toString();
    if (PageUrl.matches("http://cn.technode.com/post/\\d{4}-\\d*-\\d*[^#]*")) {
      Result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite("51CTO");
      rs = getDocument(rs, code);
      String table = "spiderdata.bucket141114";
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
    Elements titleElements = doc.select("h1.entry-title");
    String title = titleElements.text();
    rs.setTitle(title);

    Elements contentElements = doc.select("div.span8");
    ImageResult imgresult = FindImage.fliterimg(contentElements, site.getDomain(), localpath,
        localurl, PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    // Elements tagFliter = doc.select("p.crumb").select("a");
    // String fliter = tagFliter.text();
    // rs.setCategory_navigation(fliter);

    rs.setTimeDate(findTime(html));

    return rs;

  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("time[itemprop=dateCreated]");
    System.out.println("regex time " + trsid.text());
    String time = regex.findTime(trsid.html(), "\\d{4}/\\d*/\\d*");
    System.out.println("regex time " + time);
    Date result = null;
    try {
      result = DateUtil.String2Date(time);
      SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      time = t.format(result);

    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return result;

  }

  @Override
  public Site getSite() {
    return site;

  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new TechnodComProcessor())
        .addUrl("http://cn.technode.com/post/2014-12-22/tataufo-series-a/")
        // .addPipeline(new mysqlPipeline())
        .thread(11).run();
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    Logger logger = LoggerFactory.getLogger(getClass());
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    logger.info("begin");
    Spider.create(new TechnodComProcessor()).addUrl("http://cn.technode.com/")
        .addPipeline(new MysqlPipeline()).thread(5).run();

  }

}
