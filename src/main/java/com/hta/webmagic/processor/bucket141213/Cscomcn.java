package com.hta.webmagic.processor.bucket141213;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.management.JMException;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.Result;
import com.hta.webmagic.pipeline.MysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.regex.regexParser;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class Cscomcn implements PageProcessor, Runnable {

  private String seedUrl = "http://iof.hexun.com/";
  private String siteNmae = "中证网";
  private int stopDeep = 3;
  private static String StopDeepStr = null;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 5000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket_finance";
  private static String PageUrl = null;
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links().regex(".*cs.com.cn[^#]*").all();
    // for (String temp : links) {
    // System.out.println("new url :" + temp);
    // }
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(StopDeepStr);// 获取配置deep
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    // System.out.println("get url\t" + page.getUrl().toString());
    PageUrl = page.getUrl().toString();
    if (page.getUrl().toString()
        .matches("http://www.cs.com.cn/[a-z]*/[a-z0-9]*/\\d*/t\\d*_\\d*.html")) {
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
    Elements Title = doc.select("h1");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("div#ozoom1");
    ImageResult imgresult = FindImage.fliterimg(Content, site.getDomain(), localpath, localurl,
        PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    rs.setCategory(findTag(html));
    Elements Time = doc.select("div.column-top").select("span.ctime");
    rs.setTimeDate(findTime(Time));

    return rs;
  }

  /**
   * @since version 1.20
   * @param Time
   * @return
   */
  private static Date findTime(Elements Time) {
    String time = Time.text();
    return Timer.findDate(time, "\\d*-\\d*-\\d* \\d*:\\d*");
  }

  private static String findTag(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements trsid = doc.select("div.linkblack:lt(1)");
    String tags = trsid.text();
    return tags;

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
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Cscomcn())
        .addUrl("http://www.cs.com.cn/gppd/gszb/201501/t20150121_4625101.html")
        // .addPipeline(new mysqlPipeline())
        .thread(5).run();

  }

  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Cscomcn()).addUrl("http://www.cs.com.cn/").addPipeline(new MysqlPipeline())
        .thread(5).run();
  }
}
