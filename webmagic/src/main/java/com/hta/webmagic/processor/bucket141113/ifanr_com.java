package com.hta.webmagic.processor.bucket141113;

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
import com.hta.webmagic.model.result;
import com.hta.webmagic.pipeline.mysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImgage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

/**
 * bug processor
 * 
 * @author xuexianwu
 *
 */
public class ifanr_com implements PageProcessor, Runnable {

  private static Site site = Site.me().setTimeOut(8000).setRetryTimes(3);
  private static String StopDeepStr = "2";
  private int StopDeep = 3;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";

  @Override
  public void process(Page page) {
    Logger logger = LoggerFactory.getLogger(getClass());
    List<String> links = page.getHtml().links().regex("http://www.ifanr.com[^#]*").all();
    // logger.info("get new url {}", links.size());
    // for (String tep : links) {
    // System.out.println("new url" + tep);
    // }

    int deep = page.getRequest().getDeep() + 1;
    StopDeep = Integer.parseInt(StopDeepStr);
    if (deep < StopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    if (page.getUrl().toString().matches("http://www.ifanr.com/\\d{6}")) {
      result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite("爱范儿");
      rs = getDocument(rs, code);
      String table = "spiderdata.bucket141113";
      rs.setTable(table);
      page.putField("result", rs);
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
    Elements Title = doc.select("h1.entry-name yahei");
    String title = Title.text();
    rs.setTitle(title);

    Elements Author = doc.select("a[rel=author]");
    String author = Author.text();
    rs.setAuthor(author);

    // Elements Source = doc.select("div.content_titleinfoa");
    // String source = Source.get(0).ownText();
    // rs.setSource(source);

    Elements Content = doc.select("div#entry-content");
    ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());

    Elements tagFliter = doc.select("class.entry-meta-tags");
    String fliter = tagFliter.text();
    rs.setTag(fliter);
    rs.setImage(imgresult.getImgList());
    rs.setTimeD(findTime(html));

    return rs;

  }

  /**
   * singel trun
   * 
   * @param html
   * @return
   */
  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("div.content_box");
    String time = regex.findTime(trsid.html(), "\\d{4}-\\d{2}-\\d{1,2}, \\d{1,2}:\\d{1,2}");
    // System.out.println("regex time " + time);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
    Date d = null;
    try {

      d = sdf.parse(time);
      // Date result = DateUtil.String2Date(time);
      SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      time = t.format(time);

    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return d;

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
    Spider.create(new ifanr_com()).addUrl("http://www.ifanr.com/category/special/opinion")
        .addPipeline(new mysqlPipeline()).thread(15).run();
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new ifanr_com()).addUrl("http://www.ifanr.com/").addPipeline(new mysqlPipeline())
        .thread(5).run();

  }

}
