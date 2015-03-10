package com.hta.webmagic.processor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.common.hash.BloomFilter;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;



public class StoreReadUrl {
	private String filename;
	
	public StoreReadUrl(String filename){
		this.filename = filename;
	}
	
	public BloomFilter<CharSequence> readUrl(BloomFilter<CharSequence> bloomFilter){
        try {
        	 File file = new File(filename);//Text�ļ�
        	 if (!file.exists()) {
        		 CreatFile.createFile(filename);
        		 file = new File(filename);
             }
             BufferedReader br = new BufferedReader(new FileReader(file));//����һ��BufferedReader������ȡ�ļ�
             String s = null;
             while((s = br.readLine())!=null){//ʹ��readLine������һ�ζ�һ��
               boolean isDuplicate = bloomFilter.mightContain(s);
               if (!isDuplicate) {
                 bloomFilter.put(s);
             }
             }
             br.close();
        } catch (Exception e) {
            System.out.println("read file error");
            e.printStackTrace();
        } 
        	return bloomFilter;
        
        
    }
	
	public void writeUrl(String url){
		try{
			FileWriter fw= new FileWriter(filename,true);   
		    BufferedWriter bw=new   BufferedWriter(fw);   
		    bw.write(url);   
		    bw.newLine();
		    bw.flush();    
		    bw.close();    
		    fw.close();    
		}catch(Exception e){
			System.out.println("write file error");
			e.printStackTrace();
		}
	}
	public static void main(String argv[]){
//		writeUrl("aaa");
//		writeUrl("bbb");
//		writeUrl("ccc");
//		writeUrl("ddd");
//		readUrl();
    }
}
