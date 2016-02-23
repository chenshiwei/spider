package com.hta.webmagic.processor.bucket141205;

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

public class Sootoo implements PageProcessor, Runnable {

  private String seedUrl = "http://www.sootoo.com/";
  private String siteNmae = "速途网";
  private int stopDeep = 3;
  private static String StopDeepStr = "3";
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141205";
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);
  private static String PageUrl = null;

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links().regex(UrlFilter.siteRangeFliter(seedUrl)).all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(StopDeepStr);// 获取配置deep
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }

    PageUrl = page.getUrl().toString();
    if (PageUrl.matches("http://www.sootoo.com/content/\\d*.shtml")) {
      String code = page.getRawText();
      String tag = findCategory(code);
      if (tag.contains("微信")) {
        page.setSkip(true);
      } else {
        Result rs = new Result();
        rs.setUrl(page.getUrl().toString());
        rs.setSite(siteNmae);
        rs.setDeep(deep - 1);
        rs = getDocument(rs, code);
        rs.setTable(table);
        page.putField("result", rs);
        // Showinfo.printresult(rs);
      }

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
    Elements Title = doc.select("h1");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("div#content");
    ImageResult imgresult = FindImage.fliterSooTooimg(Content, site.getDomain(), localpath,
        localurl, PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    String time = doc.select("div[class=t11_info] > div > span").eq(0).text();
    String regex = ".*(\\d{4})年(\\d{1,2})月(\\d{1,2})日.*";
    String val = null;
    Pattern pt = Pattern.compile(regex);
    Matcher m = pt.matcher(time);
    while (m.find()) {
      val = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
    }
    if (val != null) {
      rs.setTimeDate(Timer.findDate(val));
    }

    return rs;
  }

  private static String findCategory(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements trsid = doc.select("div.center-research-t").select("a[target=_self]");
    String category = trsid.html();
    System.out.println("\n----------------tag result:\t" + category + "\n------------------");
    return category;

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
    Spider.create(new Sootoo()).addUrl("http://news.ccidnet.com/art/1032/20150122/5744505_1.html")
    // .addPipeline(new mysqlPipeline())
        .thread(5).run();

  }

  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Sootoo()).addUrl("http://www.sootoo.com/").addPipeline(new MysqlPipeline())
        .thread(5).run();
  }
}
