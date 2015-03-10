package com.hta.webmagic.processor.bucket141203;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regexParser;

/**
 * @author code4crafter@gmail.com <br>
 */
public class Ccidnet implements PageProcessor, Runnable {
  private String seedUrl = "http://www.ccidnet.com/";
  private String siteNmae = "赛迪网";
  private int stopDeep = 3;
  private static String stopDeepStr = null;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 10000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141203";
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);
  private static String pageUrl = null;

  @Override
  public void process(Page page) {
    List<String> links = page.getHtml().links().regex("http://news.ccidnet.com/.*").all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(stopDeepStr);
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    pageUrl = page.getUrl().toString();
    if (pageUrl.matches("http://.*ccidnet.com/[^_]*?_1.html")) {
      Result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite(siteNmae);
      rs.setDeep(deep - 1);

      rs = getDocument(rs, code, page.getUrl().toString());
      if (rs == null) {
        page.setSkip(true);
      } else {
        rs.setTable(table);
        page.putField("result", rs);
         Showinfo.printresult(rs);
      }

    } else {
      page.setSkip(true);
    }
  }

  /**
   * 
   * @param page
   * @param url
   * @return
   */
  public static String urlConcate(int page, String url) {

    url = url.split("_")[0];
    url = url + "_" + page + ".html";
    return url;
  }

  /**
   * 
   * @param rs
   * @param html
   * @return
   */
  private static Result getDocument(Result rs, String html, String url) {

    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements Title = doc.select("div[class=cont-div2] > h2");
    String title = Title.text().trim();
    if (title.endsWith("(1)"))
      title = title.substring(0, title.length() - 3);
    rs.setTitle(title);

    int size = doc.select("div.cont-div2").select("h5 > a").size();
    Elements Content = doc.select("div.temp");

    // deal with split page
    for (int i = 1; i < size; i++) {
      String tempUrl = urlConcate(i + 1, url);
      org.jsoup.nodes.Document pageDom = null;
      try {
        pageDom = Jsoup.parse(new URL(tempUrl), timeOut);
      } catch (MalformedURLException e) {
        System.out.println("URL format error." + e.getMessage());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        System.out.println("Connect time out." + e.getMessage());
      }
      if (pageDom != null) {
        Elements pageContent = pageDom.select("div.temp");
        
        Content.add(pageContent.get(0));
      } else {
        return null;
      }
    }

    ImageResult imgresult = FindImage.fliterimg(Content, site.getDomain(), localpath, localurl,
        pageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    String time = doc.select("div[class=cont-div2] > h3").text();
    if (time.length() > 0) {
      String regex = ".*(\\d{4}).(\\d{2}).(\\d{2}) .*";
      String val = null;
      Pattern pt = Pattern.compile(regex);
      Matcher m = pt.matcher(time);
      while (m.find()) {
        // val = m.group(1);
        val = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
      }

      rs.setTimeDate(Timer.findDate(val));
    }

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
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Ccidnet())
        .addUrl("http://news.ccidnet.com/art/1032/20150122/5744505_1.html").thread(5).run();
  }

  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Ccidnet()).addPipeline(new MysqlPipeline()).addUrl("http://www.ccidnet.com/")
        .thread(5).run();
  }

}
