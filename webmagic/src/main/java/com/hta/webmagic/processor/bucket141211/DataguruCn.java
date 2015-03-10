package com.hta.webmagic.processor.bucket141211;

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
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

public class DataguruCn implements PageProcessor, Runnable {
  private String seedUrl = "http://www.dataguru.cn/";
  private String siteNmae = "炼数成金";
  private static String StopDeepStr = "3";
  private int stopDeep = 4;
  private static String localpath = "";
  private static String localurl = "";
  private static int timeOut = 9000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141211";
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);
  private static String PageUrl = null;

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links().regex("http://.*.dataguru.cn/.*").all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(StopDeepStr);
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    // System.out.println("get url" + page.getUrl().toString());
    PageUrl = page.getUrl().toString();
    if (PageUrl.matches("http://.*.dataguru.cn/article-\\d*-\\d*.html")) {
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
    html = ChangeP.reDiv2P(html);
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements Title = doc.select("h1.ph");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("td#article_content");

    ImageResult imgresult = FindImage.fliterimg(Content, site.getDomain(), localpath, localurl,
        PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImage(imgresult.getImgList());
    rs.setImgSrc(imgresult.getFirstImgUrl());
    // replace label outside
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
    Elements trsid = doc.select("p.xg1");
    String time = regex.findTime(trsid.html(), "\\d*-\\d*-\\d* \\d*:\\d*");
    System.out.println("regex time " + time);
    Date result = null;
    try {
      result = DateUtil.String2Date(time);
      // SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      // time = t.format(result);

    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return result;
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
    Spider.create(new DataguruCn()).addUrl(seedUrl).addPipeline(new MysqlPipeline()).thread(5)
        .run();

  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new DataguruCn()).addUrl("http://bi.dataguru.cn/article-5511-1.html")
        .addPipeline(new MysqlPipeline()).thread(5).run();

  }

}
