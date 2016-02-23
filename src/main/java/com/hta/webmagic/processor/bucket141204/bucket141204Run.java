package com.hta.webmagic.processor.bucket141204;

public class bucket141204Run {
  public static void main(String args[]) {
    Thread b = new Thread(new Ecpai());
    b.start();
    Thread c = new Thread(new Itfeed());
    c.start();

  }
}
