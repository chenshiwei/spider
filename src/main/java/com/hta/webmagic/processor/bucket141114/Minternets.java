package com.hta.webmagic.processor.bucket141114;

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
import com.hta.webmagic.model.result;
import com.hta.webmagic.pipeline.mysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImgage;
import com.hta.webmagic.processor.utils.IOUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

public class Minternets implements PageProcessor, Runnable {
  private static Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
  private int StopDeep = 3; // set deep
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";

  @Override
  public void process(Page page) {
    String html = page.getHtml().toString();
    Document doc = Jsoup.parse(html);
    result r = new result();
    String site = "移动互联网资讯站";
    String url = null;
    String title = null;
    String keywords = null;
    String description = null;
    String time = null;
    String content = null;
    String author = null;
    String source = null;
    String category_navigation = null;
    int forward = 0;
    int read = 0;
    int comment = 0;
    int applaud = 0;
    String table = "spiderdata.bucket141114";
    r.setTable(table);

    // get the urls of this page
    List<String> links = page.getHtml().links().regex("http://www.minternets.com/[^#]*").all();
    int deep = page.getRequest().getDeep() + 1;
    if (deep < StopDeep) {
      page.addTargetRequests(links, deep);
    }

    r = getDocument(r, html);

    // url
    url = page.getUrl().regex("http://www.minternets.com/[^#]*").toString();
    // title
    title = doc.select("h1").text();
    // keywords
    keywords = doc.select("meta[name=keywords]").attr("content");
    // description
    description = doc.select("meta[name=description]").attr("content");
    // create_time
    time = doc.select("time[class=muted]").text();
    // author
    source = doc.select("div[class=meta] > span").eq(3).text();

    content = doc.select("article[class=article-content]").text();

    author = "Minternets";

    if (content == null || content.equals("")) {
      // skip this page
      page.setSkip(true);
    }

    if (time.length() != 0) {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      String regex = "(.*)小时前";
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(time);
      String x;
      if (m.find()) {
        x = m.group(1);
        int xx = Integer.parseInt(x);
        cal.add(Calendar.HOUR_OF_DAY, -xx);// 小时
        time = df.format(cal.getTime());
      } else {
        regex = "(.*)天前";
        p = Pattern.compile(regex);
        m = p.matcher(time);
        if (m.find()) {
          x = m.group(1);
          int xx = Integer.parseInt(x);
          cal.add(Calendar.DATE, -xx);// 日
          time = df.format(cal.getTime());
        } else {
          regex = "(.*)周前";
          p = Pattern.compile(regex);
          m = p.matcher(time);
          if (m.find()) {
            x = m.group(1);
            int xx = Integer.parseInt(x);
            cal.add(Calendar.DATE, -xx * 7);// 周
            time = df.format(cal.getTime());
          } else {
            regex = "(.*)个月前";
            p = Pattern.compile(regex);
            m = p.matcher(time);
            if (m.find()) {
              x = m.group(1);
              int xx = Integer.parseInt(x);
              cal.add(Calendar.MONTH, -xx);// 周
              time = df.format(cal.getTime());
            } else {
              regex = "(.*)年前";
              p = Pattern.compile(regex);
              m = p.matcher(time);
              if (m.find()) {
                x = m.group(1);
                int xx = Integer.parseInt(x);
                cal.add(Calendar.YEAR, -xx);// 周
                time = df.format(cal.getTime());
              }
            }
          }
        }
      }
    }
    if (source.length() != 0) {
      String regex = "稿源：(.*)";
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(source);
      if (m.find()) {
        source = m.group(1);
      }
    }
    try {
      Date result = DateUtil.String2Date(time);
      SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      time = t.format(result);
    } catch (java.text.ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // page.putField("url",url);
    // page.putField("title",title);
    // page.putField("keywords",keywords);
    // page.putField("description",description);
    // page.putField("time", time);
    // page.putField("content", content);
    // page.putField("author", author);
    // page.putField("source", source);
    // page.putField("comment", comment);

    r.setUrl(url);
    r.setTime(time);
    r.setTitle(title);
    r.setKeywords(keywords);
    r.setDescription(description);
    r.setAuthor(author);
    r.setCategory_navigation(category_navigation);
    r.setSite(site);
    r.setSource(source);
    r.setForward(forward);
    r.setRead(read);
    r.setApplaud(applaud);
    r.setComment(comment);
    page.putField("result", r);
  }

  private static result getDocument(result rs, String html) {
    Document doc = Jsoup.parse(html);
    Elements Content = doc.select("article[class=article-content]").select("p");

    ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    return rs;
  }

  @Override
  public Site getSite() {
    return site;
  }

  public static void main(String[] args) throws JMException {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("./spiderConf/etl.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");

    Spider.create(new Minternets())

    .addUrl("http://www.minternets.com/archives/18225.html")
    // .addPipeline(new mysqlPipeline())
        .thread(5).run();

  }

  public void run() {
    Properties pro = com.hta.webmagic.processor.utils.IOUtil
        .readPropertiesFile("/home/xuexianwu/spider/spiderConf/etl.conf");
    localpath = pro.getProperty("LOCALPATH");
    localurl = pro.getProperty("LOCALURL");
    Spider.create(new Minternets())

    .addUrl("http://www.minternets.com/")

    .addPipeline(new mysqlPipeline()).thread(5).run();
  }
}
