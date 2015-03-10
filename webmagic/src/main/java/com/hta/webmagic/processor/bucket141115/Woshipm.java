package com.hta.webmagic.processor.bucket141115;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.DateUtil;

public class Woshipm implements PageProcessor, Runnable {
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	private int StopDeep = 3; // set deep

	@Override
	public void process(Page page) {
		String html = page.getHtml().toString();
		Document doc = Jsoup.parse(html);
		result r = new result();
		String site = "人人都是产品经理";
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
		String table = "spiderdata.bucket141113";
		r.setTable(table);
		// get the urls of this page
		List<String> links = page.getHtml().links()
				.regex("http://www.woshipm.com/[^#]*").all();
		int deep = page.getRequest().getDeep() + 1;
		if (deep < StopDeep) {
			page.addTargetRequests(links, deep);
		}

		// url
		url = page.getUrl().regex("http://www.woshipm.com/[^#]*").toString();
		// title
		title = doc.select("h1").text();
		// keywords
		keywords = doc.head().select("meta[name=keywords]").attr("content");
		// description
		description = doc.head().select("meta[name=description]")
				.attr("content");
		// create_time
		time = doc.select("span[class=con_t_time]").text();
		// author
		source = site;
		content = doc.select("div[class=con_txt clx]").text();

		if (content == null || content.equals("")) {
			// skip this page
			page.setSkip(true);
		}

		// if (time.length() != 0) {
		// String regex = ".*发布于(.*)";
		// Pattern p = Pattern.compile(regex);
		// Matcher m = p.matcher(time);
		// if (m.find()) {
		// time = m.group(1);
		// }
		// time = time.substring(0,11);
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		// time = df.format(new Date());
		// }

		if (time.contains("前")) {

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			time = df.format(new Date());
		}
		try {
			result = DateUtil.String2Date(time);
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
		r.setTimeD(result);
		r.setTitle(title);
		r.setKeywords(keywords);
		r.setDescription(description);
		r.setContent(content);
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

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) throws JMException {

		Spider.create(new Woshipm())

		.addUrl("http://www.woshipm.com/")
		// .addPipeline(new mysqlPipeline())
				.thread(5).run();

	}

	public void run() {
		Spider.create(new Woshipm())

		.addUrl("http://www.woshipm.com/")

		.addPipeline(new mysqlPipeline()).thread(5).run();
	}
}
