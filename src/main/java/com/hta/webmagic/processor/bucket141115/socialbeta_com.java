package com.hta.webmagic.processor.bucket141115;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

import com.hta.webmagic.model.result;
import com.hta.webmagic.pipeline.mysqlPipeline;
import com.hta.webmagic.regex.regexParser;

public class socialbeta_com implements PageProcessor, Runnable {

  private Site site = Site.me().setTimeOut(8000).setRetryTimes(3);
  private int StopDeep = 3;

  @Override
  public void process(Page page) {
    Logger logger = LoggerFactory.getLogger(getClass());
    List<String> links = page.getHtml().links().regex("http://www.socialbeta.com[^#]*").all();
    logger.info("get new url {}", links.size());
    // for (String tep : links) {
    // System.out.println("new url" + tep);
    // }

    int deep = page.getRequest().getDeep() + 1;

    if (deep < StopDeep) {
      page.addTargetRequests(links, deep);
    }
    String code = page.getRawText();
    if (page.getUrl().toString().matches("http://www.socialbeta.com/articles/[\\s\\S]*.html")) {
      result rs = regexParser.parserkeywords(code);
      rs.setUrl(page.getUrl().toString());
      rs.setSite("社会化营销人的知识分享和职场成长平台");
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
    Elements Title = doc.select("h1.entry-title");
    String title = Title.text();
    rs.setTitle(title);

    Elements Author = doc.select("span.author");
    String author = Author.text();
    rs.setAuthor(author);

    // Elements Source = doc.select("div.content_titleinfoa");
    // String source = Source.get(0).ownText();
    // rs.setSource(source);

    Elements Content = doc.select("div.entry-content").tagName("p");
    String content = Content.text();
    rs.setContent(content);

    Elements tagFliter = doc.select("footer.entry-meta");
    String fliter = tagFliter.text();
    rs.setCategory_navigation(fliter);

    rs.setTimeD(findTime(html));

    return rs;

  }

  private static Date findTime(String html) {
    org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
    Elements trsid = doc.select("time.updated");
    String time = trsid.attr("datetime");
    time = time.replace("T", " ");
    time = time.replace("+08:00", "");
    Date result = null;
    // System.out.println("regex time " + time);
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
    Spider.create(new socialbeta_com()).addUrl("http://www.socialbeta.com")
        .addPipeline(new mysqlPipeline()).thread(15).run();
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    Spider.create(new socialbeta_com()).addUrl("http://www.socialbeta.com")
        .addPipeline(new mysqlPipeline()).thread(5).run();

  }

}
