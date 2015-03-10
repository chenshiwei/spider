package com.hta.webmagic.processor.bucket141202;

public class bucket141202run {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Thread b = new Thread(new Cctime());
    b.start();
    Thread c = new Thread(new Ciotimes());
    c.start();
    Thread d = new Thread(new Cnii());
    d.start();
    Thread e = new Thread(new Cniteyes());
    e.start();
  }

}
