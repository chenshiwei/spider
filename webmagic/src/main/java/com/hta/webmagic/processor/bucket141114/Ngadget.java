package com.hta.webmagic.processor.bucket141114;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.JMException;

import org.apache.http.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hta.webmagic.model.result;
import com.hta.webmagic.pipeline.mysqlPipeline;
import com.hta.webmagic.processor.utils.FindImgage;
import com.hta.webmagic.processor.utils.IOUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

public class Ngadget implements PageProcessor,Runnable {
	private static Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	private int StopDeep = 3; // set deep
	private static String localpath = "/var/lib/tomcat/webapps/spiderData/img/";
	private static String localurl = "http://172.16.4.213:8080/spiderData/img/";
	@Override
	public void process(Page page) {
		String html = page.getHtml().toString();
		Document doc = Jsoup.parse(html);
		result r = new result();
		String site = "engadget中文版";
		String url = null;
		String title = null;
		String keywords = null;
		String description = null;
		String time = null;
		String content;
		String author = null;
		String source = null;
		String category_navigation = null;
		Date result = null;
		int forward = 0;
		int read = 0;
		int comment = 0;
		int applaud = 0;
		String table = "spiderdata.bucket141114";
		r.setTable(table);
		// get the urls of this page
		List<String> links = page.getHtml().links()
				.regex("http://cn.engadget.com/[^#]*").all();
		int deep = page.getRequest().getDeep() + 1;
		if (deep < StopDeep) {
			page.addTargetRequests(links, deep);
		}

		// url
		url = page.getUrl().regex("http://cn.engadget.com/[^#]*").toString();
		// title
		title = doc.select("h1").text();
		// keywords
		keywords = doc.head().select("meta[name=keywords]").attr("content");
		// description
		description = doc.select("meta[name=description]")
				.attr("content");
		// create_time
		if(url.length() != 0){
			String regex = ".*/(\\d{4}/\\d{2}/\\d{2}).*";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(url);
			if (m.find()) {
				time = m.group(1);//+m.group(2)+m.group(3);
			}
		}
		// author
		source = site;
		content = doc.select("div[id=body]").text();
	 
		if (content == null || content.equals("")) {
			 //skip this page
			 page.setSkip(true);
		}
		if(time != null){
		try {
			result = DateUtil.String2Date(time);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		

//		 page.putField("url",url);
//		 page.putField("title",title);
//		 page.putField("keywords",keywords);
//		 page.putField("description",description);
//		 page.putField("time", time);
//		 page.putField("content", content);
//		 page.putField("author", author);
//		 page.putField("source", source);
//		 page.putField("comment", comment);

		 

		r.setUrl(url);
		r.setTimeD(result);
		r.setTitle(title);
		r.setKeywords(keywords);
		r.setContent(content);
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
//	private static result getDocument(result rs, String html) {
//		Document doc = Jsoup.parse(html);
//		String content = doc.select("div[id=body]").text();
//		
////		String content=FindImgage.fliterimg(Content, site.getDomain(), localpath, localurl);
//        System.out.println(content);
//		rs.setContent(content);
//		return rs;
//	}
	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) throws JMException {
		Properties pro = com.hta.webmagic.processor.utils.IOUtil
				.readPropertiesFile("./spiderConf/etl.conf");
		localpath = pro.getProperty("LOCALPATH");
		localurl = pro.getProperty("LOCALURL");

		Spider.create(new Ngadget())

		.addUrl("http://cn.engadget.com/2014/11/24/cern-open-data-portal/")
		//.addPipeline(new mysqlPipeline())
				.thread(5).run();

	}
	public void run() {
		Properties pro = com.hta.webmagic.processor.utils.IOUtil
				.readPropertiesFile("/home/xuexianwu/spider/spiderConf/etl.conf");
		localpath = pro.getProperty("LOCALPATH");
		localurl = pro.getProperty("LOCALURL");
		Spider.create(new Ngadget())

		.addUrl("http://cn.engadget.com/")

		.addPipeline(new mysqlPipeline()).thread(5).run();
	}
}
