package com.hta.webmagic.processor.bucket141111;

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

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.result;
import com.hta.webmagic.pipeline.mysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImgage;
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.showinfo;
import com.hta.webmagic.regex.regexParser;

/**
 * 
 * @author xuexianwu
 *
 */
public class zdnet_com_cn implements PageProcessor, Runnable {
  private String seedUrl = "http://www.zdnet.com.cn/";
  private String siteNmae = "至顶网";
  private static String StopDeepStr = "3";
  private int stopDeep = 2;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
  private static int timeOut = 10000;
  private static int retryTimes = 5;
  private String table = "spiderdata.bucket141111";
  protected Logger logger = LoggerFactory.getLogger(getClass());
  private static Site site = Site.me().setTimeOut(timeOut).setRetryTimes(retryTimes);

  @Override
  public void process(Page page) {
    // TODO Auto-generated method stub
    List<String> links = page.getHtml().links().regex("http://[\\w\\W]*.zdnet.com.cn[^#]*").all();
    // for(String temp:links){
    // System.out.println(temp);
    // }
    int deep = page.getRequest().getDeep() + 1;
    stopDeep = Integer.parseInt(StopDeepStr);
    if (deep < stopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    // System.out.println(page.getUrl().toString());

    if (page.getUrl().toString()
        .matches("http://[\\w\\W]*.zdnet.com.cn/[a-z_]*/\\d*/\\d*/\\d*.shtml")) {
      result rs = regexParser.parserkeywords(code);
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

  private static result getDocument(result rs, String html) {

    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements Title = doc.select("h1.root_h1");
    String title = Title.text();
    rs.setTitle(title);
    Elements Content = doc.select("div.qu_ocn");
    ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
    // content = ChangeP.htnlHand(content); // 处理标签
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    // Elements firstImg = doc.select("div.qu_ocn").select("p").select("img");
    // // add if
    // if (!firstImg.isEmpty()) {
    // // System.out.println("first img src:" + firstImg.select("img").attr("src"));
    // System.out.println(" yuming" + site.getDomain());
    // String imgpath = FindImgage.imgLoad(firstImg, site.getDomain(), localpath, localurl);
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    // }
    rs.setTimeD(findTime(html));

    return rs;

  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("div.qu_zuo");
    String time = trsid.text();
    return Timer.findDate(time, "\\d{4}年\\d*月\\d*日");
  }

  private static String findSource(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("div.body_time");
    String source = "";
    return null;

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
    // StopDeepStr = pro.getProperty("DEEP");
    Spider.create(new zdnet_com_cn()).addUrl(seedUrl).addPipeline(new mysqlPipeline()).thread(8)
        .run();

  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Properties pro = com.hta.webmagic.processor.utils.IOUtil.readPropertiesFile("./conf/etl.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    Spider.create(new zdnet_com_cn()).addUrl("http://news.zdnet.com.cn")
        .addPipeline(new mysqlPipeline()).thread(10).run();

  }

}
