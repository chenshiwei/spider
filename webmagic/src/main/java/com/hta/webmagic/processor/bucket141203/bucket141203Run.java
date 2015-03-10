package com.hta.webmagic.processor.bucket141203;


public class bucket141203Run {
	public static void main(String args[]){
		Thread a = new Thread(new Ccidnet());
		a.start();
		Thread b = new Thread(new ChinaSourcing());
		b.start();		
		Thread c = new Thread(new Enet());
		c.start();

	}
}
