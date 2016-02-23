package com.hta.webmagic.processor.bucket141114;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.Result;
import com.hta.webmagic.pipeline.MysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImage;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

/**
 * 
 * @author xuexianwu
 *
 */
public class Chinais implements PageProcessor, Runnable {
  private String siteNmae = "中国信息安全网";
  private static Site site = Site.me().setTimeOut(8000).setRetryTimes(3);
  private static String stopDeepStr = null;
  private int stopDeep = 3;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static String pageUrl = null;

  @Override
  public void process(Page page) {
    List<String> links = page.getHtml().links().regex("http://www.chinais.net[^#]*").all();
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(stopDeepStr);
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    pageUrl = page.getUrl().toString();
    if (pageUrl.matches("http://www.chinais.net/portal.php\\?mod=view[^#]*")) {
      Result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite(siteNmae);
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
    Elements Title = doc.select("h1.ph");
    String title = Title.text();
    rs.setTitle(title);

    Elements Content = doc.select("td#article_content");
    ImageResult imgresult = FindImage.fliterimg(Content, site.getDomain(), localpath, localurl,
        pageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    rs.setTimeDate(findTime(html));

    return rs;

  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("p.xg1");
    System.out.println("regex time " + trsid.text());
    String time = regex.findTime(trsid.html(), "\\d*-\\d*-\\d* \\d*:\\d*");
    System.out.println("regex time " + time);
    Date result = null;
    try {
      result = DateUtil.String2Date(time);
      SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      time = t.format(result);

    } catch (Exception e) {
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
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Chinais()).addUrl("http://www.chinais.net/")
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
    stopDeepStr = pro.getProperty("DEEP");
    logger.info("begin");
    Spider.create(new Chinais()).addUrl("http://www.chinais.net/").addPipeline(new MysqlPipeline())
        .thread(10).run();

  }

}
