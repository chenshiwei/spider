package com.hta.webmagic.processor.bucket141114;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class Cto51_com implements PageProcessor, Runnable {
  /**
   * @version1.35
   */
  private static Site site = Site.me().setTimeOut(8000).setRetryTimes(3);
  private static String stopDeepStr = null;
  private int StopDeep = 2;
  private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
  private static String localurl = "http://172.16.4.213:8080/spiderData/img/";

  @Override
  public void process(Page page) {
    List<String> links = page
        .getHtml()
        .links()
        .regex(
            "http://developer.51cto.com[^#]*|http://news.51cto.com[^#]*|http://os.51cto.com[^#]*|http://network.51cto.com[^#]*|http://netsecurity.51cto.com[^#]*|http://cloud.51cto.com[^#]*|http://server.51cto.com[^#]*|http://virtual.51cto.com[^#]*|http://mobile.51cto.com[^#]*")
        .all();
    int deep = page.getRequest().getDeep() + 1;
    StopDeep = Integer.parseInt(stopDeepStr);
    if (deep < StopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    // System.out.println("new url: " + page.getRequest().getUrl());
    if (page.getRequest().getUrl().matches("http://[a-z]*.51cto.com/art/[0-9]*/[0-9]*.*htm")) {
      String url = page.getUrl().toString();
      result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite("51CTO");
      rs = getDocument(rs, code, url);
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
   * @throws IOException
   * @throws MalformedURLException
   */
  private static result getDocument(result rs, String html, String url) {

    org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
    Elements title = doc.select("h1");
    String titleStr = title.text();
    rs.setTitle(titleStr);

    Elements content = doc.select("div#content");
    try {
      if (doc.select("div[class=page]") != null) {
        url = url.replaceAll(".htm", "_all.htm");
        System.out.println("new url = " + url);
        doc = Jsoup.parse(new URL(url), 30000);
        content = doc.select("div#content");
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    content.select("div[class=page]").remove();
    content.select("div[ol]").remove();
    ImageResult imgresult = FindImgage.fliterimg(content, site.getDomain(), localpath, localurl);
    String contentStr = ChangeP.htnlHand(imgresult.getContent());
    String s = "【编辑推荐】";
    String regex = "(<p>((?!</p>).)*" + s + "[\\s\\S]*|" + s + "[\\s\\S]*)";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(contentStr);
    contentStr = m.replaceAll("");
    rs.setContent(contentStr);

    rs.setImgSrc(imgresult.getFirstImgUrl());

    Elements tagFliter = doc.select("p.crumb").select("a");
    String fliter = tagFliter.text();
    rs.setCategory_navigation(fliter);
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setTimeD(findTime(html));

    return rs;

  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("div.msg");
    // System.out.println("regex time " + trsid.text());
    String time = regex.findTime(trsid.html(), "\\d{4}-\\d*-\\d* \\d*:\\d*");
    // System.out.println("regex time " + time);
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
    stopDeepStr = pro.getProperty("DEEP");
    Spider.create(new Cto51_com()).addUrl("http://network.51cto.com/art/201501/464002.htm")
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
    Spider.create(new Cto51_com()).addUrl("http://www.51cto.com/").addPipeline(new mysqlPipeline())
        .thread(6).run();

  }

}
