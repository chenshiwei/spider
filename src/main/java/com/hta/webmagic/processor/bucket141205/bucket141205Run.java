package com.hta.webmagic.processor.bucket141205;


public class bucket141205Run {
	public static void main(String args[]){
		Thread a = new Thread(new Ibroadcast());
		a.start();
		Thread b = new Thread(new Ccidcom());
		b.start();
		Thread c = new Thread(new Donews());
		c.start();
		Thread d = new Thread(new Sootoo());
    d.start();
		Thread g = new Thread(new Web20share());
		g.start();
	}
}
