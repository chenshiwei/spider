package com.hta.webmagic.processor.bucket141112;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

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
import com.hta.webmagic.processor.utils.Timer;
import com.hta.webmagic.processor.utils.UrlFilter;
import com.hta.webmagic.processor.utils.showinfo;
import com.hta.webmagic.regex.regex;
import com.hta.webmagic.regex.regexParser;

public class Jrzj_comProcessor implements PageProcessor, Runnable {
	private String seedUrl = "http://news.jrzj.com/";
	private String siteNmae = "金融之家";
	private static String StopDeepStr = null;
	private int stopDeep = 2;
	private static String localpath = "";
	private static String localurl = "";
	private static int timeOut = 9000;
	private static int retryTimes = 5;
	private String table = "spiderdata.bucket141112";
	private static Site site = Site.me().setTimeOut(timeOut)
			.setRetryTimes(retryTimes);

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		List<String> links = page.getHtml().links()
				.regex(UrlFilter.siteRangeFliter(seedUrl)).all();
		// for (String temp : links) {
		// System.out.println("new url " + temp);
		// }
		int deep = page.getRequest().getDeep() + 1;
		stopDeep = Integer.parseInt(StopDeepStr);// 获取配置deep
		if (deep < stopDeep) {
			page.addTargetRequests(links, deep);
		}
		String code = page.getRawText();
		if (page.getUrl().toString()
				.matches("http://news.jrzj.com/[1-9]*.html")) {
			result rs = regexParser.parserkeywords(code);
			rs.setUrl(page.getUrl().toString());
			rs.setSite(siteNmae);
			rs.setDeep(deep - 1);
			rs = getDocument(rs, code);
			rs.setTable(table);
			page.putField("result", rs);
//			 showinfo.printresult(rs);

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
		Elements Title = doc.select("h1#syxbt");
		String title = Title.text();
		rs.setTitle(title);
		Elements firstImg = doc.select("div.news_content").select("img");
		// add if
		if (!firstImg.isEmpty()) {
			// System.out.println("image-------------");
			String imgpath = FindImgage.firstImg(firstImg, site.getDomain(),
					localpath, localurl);
			rs.setImgSrc(imgpath);
		}
		Elements Content = doc.select("div.news_content");
		 ImageResult imgresult = FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
	    rs.setContent(ChangeP.htnlHand(imgresult.getContent()));
	    rs.setImgSrc(imgresult.getFirstImgUrl());
	    rs.setImage(imgresult.getImgList());
		rs.setTimeD(findTime(html));
		return rs;

	}

	private static Date findTime(String html) {
		org.jsoup.nodes.Document doc = Jsoup.parse(html, "GBK");
		Elements trsid = doc.select("div.news_bt").select("span");
		// System.out.println("regex time " + trsid.text());
		String time = regex.findTime(trsid.html(), "\\d*-\\d*-\\d* \\d*:\\d*");
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
		StopDeepStr = pro.getProperty("DEEP");	
		Spider.create(new Jrzj_comProcessor()).addUrl(seedUrl)
				.addPipeline(new mysqlPipeline()).thread(10).run();


	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties pro = com.hta.webmagic.processor.utils.IOUtil
				.readPropertiesFile("./spiderConf/spider.conf");
		localpath = pro.getProperty("LOCALPATH");
		localurl = pro.getProperty("LOCALURL");
		StopDeepStr = pro.getProperty("DEEP");
		Spider.create(new Jrzj_comProcessor()).addUrl("http://news.jrzj.com/")
		// .addPipeline(new mysqlPipeline())
				.thread(10).run();

	}

}
