package com.hta.webmagic.processor.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



public class IOUtil {

	@SuppressWarnings("unchecked")
	public static List<String> getTables(String path) {
		List<String> tables = new ArrayList<String>();
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(new File(path));
			List<Element> root = doc.getRootElement().elements();
			for (Element e : root) {
				tables.add(e.attributeValue("name"));
			}
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return tables;
	}
	
	public static Properties readPropertiesFile(String path) {
		Properties pros = new Properties();
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(path));
			pros.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pros;
	}
}
