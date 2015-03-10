package com.hta.webmagic.processor.bucket141213;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
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
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.Showinfo;
import com.hta.webmagic.regex.regexParser;


public class TechQq implements PageProcessor, Runnable {
	private String seedUrl = "http://tech.qq.com/";
	private String siteNmae = "腾讯科技";
	private static String StopDeepStr = null;
	private int stopDeep = 3;
	private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
	private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
	private static int timeOut = 5000;
	private static int retryTimes = 5;
	private String table = "spiderdata.bucket141213";
	private static Site site = Site.me().setTimeOut(timeOut)
			.setRetryTimes(retryTimes);
	private static String PageUrl = null;

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		List<String> links = page.getHtml().links()
				.regex(UrlFilter.siteRangeFliter(seedUrl)).all();
		int deep = page.getRequest().getDeep() + 1;
		stopDeep = Integer.parseInt(StopDeepStr);
		if (deep < stopDeep) {
			page.addTargetRequests(links, deep);
		}
		String code = page.getRawText();
		 PageUrl = page.getUrl().toString();
	    if (PageUrl
				.matches("http://tech.qq.com/a/[^#]*")) {
			Result rs = regexParser.parserkeywords(code);
			rs.setUrl(page.getUrl().toString());
			rs.setSite(siteNmae);
			rs.setDeep(deep - 1);
			rs = getDocument(rs, code);
			rs.setTable(table);
			page.putField("result", rs);
//			showinfo.printresult(rs);

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

		org.jsoup.nodes.Document doc = Jsoup.parse(html, "gb2312");
		Elements Title = doc.select("h1");
		String title = Title.text();
		rs.setTitle(title);

		Elements Content = doc.select("div[id = Cnt-Main-Article-QQ]");
		ImageResult imgresult = FindImage.fliterimg(Content, site.getDomain(),  localpath, localurl,
        PageUrl);
    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
    rs.setImgSrc(imgresult.getFirstImgUrl());
    rs.setImage(imgresult.getImgList());
    rs.setTimeDate(findTime(html));
    return rs;
	}

	/**
	 * 
	 * @param htmlcode
	 *            regex timeString
	 * @return Date
	 */
	private static Date findTime(String html) {
		org.jsoup.nodes.Document doc = Jsoup.parse(html, "gb2312");
		Elements trsid = doc.select("span[class=pubTime]");
		String time = trsid.text();
		return Timer.findDate(time);
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
		StopDeepStr = pro.getProperty("DEEP");
		Spider.create(new TechQq()).addUrl(seedUrl)
				.addPipeline(new MysqlPipeline()).thread(10).run();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties pro = com.hta.webmagic.processor.utils.IOUtil
				.readPropertiesFile("./spiderConf/spider.conf");
		localpath = pro.getProperty("LOCALPATH");
		localurl = pro.getProperty("LOCALURL");
		StopDeepStr = pro.getProperty("DEEP");
		Spider.create(new TechQq())
				.addUrl("http://tech.qq.com/")
				//.addPipeline(new mysqlPipeline())
				.thread(5).run();

	}

}
