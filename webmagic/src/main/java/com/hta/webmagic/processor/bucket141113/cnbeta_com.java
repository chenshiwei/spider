package com.hta.webmagic.processor.bucket141113;
/**
 * @version1.11
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import us.codecraft.webmagic.utils.DateUtil;

import com.hta.webmagic.model.ImageResult;
import com.hta.webmagic.model.result;
import com.hta.webmagic.pipeline.mysqlPipeline;
import com.hta.webmagic.processor.utils.ChangeP;
import com.hta.webmagic.processor.utils.FindImgage;
import com.hta.webmagic.processor.utils.showinfo;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

public class cnbeta_com implements PageProcessor, Runnable {

	private static Site site = Site.me().setTimeOut(16000).setRetryTimes(5);
	private static String StopDeepStr = null;
	private int StopDeep = 2;	
	private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
	private static String localurl = "http://172.16.4.213:8080/spiderData/img/";

	@Override
	public void process(Page page) {
		List<String> links = page.getHtml().links()
				.regex("http://www.cnbeta.com[^#]*.htm").all();
		// for(String temp:links){
		// System.out.println(" new url"+temp);
		// }
		StopDeep = Integer.parseInt(StopDeepStr);
		int deep = page.getRequest().getDeep() + 1;

		if (deep < StopDeep) {
			page.addTargetRequests(links, deep);
		}
		String code = page.getRawText();
//		System.out.println(" filter url" + page.getUrl().toString());
		if (page.getUrl().toString()
				.matches("http://www.cnbeta.com/articles/\\d*.htm")) {
			result rs = regexParser.parserkeywords(code);
			rs.setUrl(page.getUrl().toString());
			rs.setSite("中文业界资讯站");
			rs = getDocument(rs, code);
			String table = "spiderdata.bucket141113";
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
	private static result getDocument(result rs, String html) {

		org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");
		Elements Title = doc.select("h2#news_title");
		String title = Title.text();
		rs.setTitle(title);

		Elements Content = doc.select("div.content");
		 ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
	    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
	    rs.setImgSrc(imgresult.getFirstImgUrl());
	    rs.setImage(imgresult.getImgList());
		rs.setTimeD(findTime(html));

		return rs;

	}

	private static Date findTime(String html) {
		org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
		Elements trsid = doc.select("span.date");
		String time = trsid.text();
		Date result = null;
		try {
			result = DateUtil.String2Date(time);
			// SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// time = t.format(result);

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
		StopDeepStr = pro.getProperty("DEEP");
		Spider.create(new cnbeta_com()).addUrl("http://www.cnbeta.com/articles/356499.htm")
//				.addPipeline(new mysqlPipeline())
		.thread(15).run();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Properties pro = com.hta.webmagic.processor.utils.IOUtil
				.readPropertiesFile("/home/xuexianwu/spider/spiderConf/spider.conf");
		localpath = pro.getProperty("LOCALPATH");
		localurl = pro.getProperty("LOCALURL");
		StopDeepStr = pro.getProperty("DEEP");
		Spider.create(new cnbeta_com()).addUrl("http://www.cnbeta.com/")
				.addPipeline(new mysqlPipeline()).thread(3).run();

	}

}
