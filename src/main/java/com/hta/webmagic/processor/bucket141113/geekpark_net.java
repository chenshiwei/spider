package com.hta.webmagic.processor.bucket141113;

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
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

/**
 * @version 1.11
 * 
 * @author xuexianwu
 *
 */
public class geekpark_net implements PageProcessor, Runnable {

	private static Site site = Site.me().setTimeOut(8000).setRetryTimes(3);
	private static String StopDeepStr = null;
	private int StopDeep = 2;
	private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
	private static String localurl = "http://172.16.4.213:8080/spiderData/img/";

	@Override
	public void process(Page page) {
		Logger logger = LoggerFactory.getLogger(getClass());
		List<String> links = page.getHtml().links()
				.regex("http://www.geekpark.net/topics/\\d*").all();
		StopDeep = Integer.parseInt(StopDeepStr);
		int deep = page.getRequest().getDeep() + 1;

		if (deep < StopDeep) {
			page.addTargetRequests(links, deep);
		}
		String code = page.getRawText();

		result rs = regexParser.parserkeywords(code);
		rs.setUrl(page.getUrl().toString());
		rs.setSite("极客公园");
		rs = getDocument(rs, code);
		String table = "spiderdata.bucket141113";
		rs.setTable(table);
		page.putField("result", rs);
	}

	/**
	 * 
	 * @param rs
	 * @param html
	 * @return
	 */
	private static result getDocument(result rs, String html) {

		org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
		Elements Title = doc.select("h1.topic-title");
		String title = Title.text();
		rs.setTitle(title);


		Elements Content = doc.select("span[itemprop=articleBody]");
		 ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
	    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
	    rs.setImgSrc(imgresult.getFirstImgUrl());

		Elements Time = doc.select("span[itemprop=datePublished]");
		 rs.setImage(imgresult.getImgList());
		String timeString = Time.text();

		rs.setTimeD(new Date(Time(timeString)));

		return rs;

	}

	private static String Time(String html) {
		String TimeRegex = "\\d{4}-\\d{2}-\\d{2}";
		String dayRegex = "大约 \\d* 小时前";
		String hourRegex = "大约 [\\d*] 小时前";
		String time = Timer.findMixTime(html, TimeRegex, dayRegex, hourRegex);
		return time;

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
		StopDeepStr = pro.getProperty("DEEP");
		Spider.create(new geekpark_net()).addUrl("http://www.geekpark.net/")
				.addPipeline(new mysqlPipeline()).thread(5).run();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Properties pro = com.hta.webmagic.processor.utils.IOUtil
				.readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
		localpath = pro.getProperty("LOCALPATH");
		localurl = pro.getProperty("LOCALURL");
		StopDeepStr = pro.getProperty("DEEP");
		Spider.create(new geekpark_net()).addUrl("http://www.geekpark.net/")
				.addPipeline(new mysqlPipeline()).thread(6).run();

	}

}
