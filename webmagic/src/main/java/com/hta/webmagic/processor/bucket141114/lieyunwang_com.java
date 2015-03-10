package com.hta.webmagic.processor.bucket141114;

/**
 * version 1.02
 */
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
import com.hta.webmagic.processor.utils.showinfo;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

public class lieyunwang_com implements PageProcessor, Runnable {

  private static Site site = Site.me().setTimeOut(8000).setRetryTimes(3);
  private static String StopDeepStr = "2";
  private int StopDeep = 3;
  private static String localpath = "";
  private static String localurl = "";

  @Override
  public void process(Page page) {
    List<String> links = page.getHtml().links().regex("http://www.lieyunwang.com[^#]*").all();

    int deep = page.getRequest().getDeep() + 1;
    StopDeep = Integer.parseInt(StopDeepStr);
    if (deep < StopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    if (page.getUrl().toString().matches("http://www.lieyunwang.com/archives/\\d*")) {
      result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite("猎云网");
      rs = getDocument(rs, code);
      String table = "spiderdata.bucket141114";
      rs.setTable(table);
      page.putField("result", rs);
      showinfo.printresult(rs);

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

    // Elements Author = doc.select("div.content_titleinfoa");
    // String author = Author.get(1).ownText();
    // rs.setAuthor(author);

    // Elements Source = doc.select("div.content_titleinfoa");
    // String source = Source.get(0).ownText();
    // rs.setSource(source);

    Elements Content = doc.select("div.l_txt");
    ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());

    Elements tagFliter = doc.select("div.l_tag").select("a");
    String fliter = tagFliter.text();
    rs.setCategory_navigation(fliter);

    String description = doc.select("meta#description").attr("content");
    rs.setDescription(description);
    rs.setImage(imgresult.getImgList());
    rs.setTimeD(findTime(html));

    return rs;

  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("span.entry-date");
    System.out.println("regex time " + trsid.text());
    String time = trsid.text();
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
        .readPropertiesFile("./spiderConf/etl.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    Spider.create(new lieyunwang_com()).addUrl("http://www.lieyunwang.com/")
    // .addPipeline(new mysqlPipeline())
        .thread(10).run();
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new lieyunwang_com()).addUrl("http://www.lieyunwang.com")
        .addPipeline(new mysqlPipeline()).thread(5).run();

  }

}
