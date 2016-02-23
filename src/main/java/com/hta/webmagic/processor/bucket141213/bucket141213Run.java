package com.hta.webmagic.processor.bucket141213;

public class bucket141213Run {
  public static void main(String args[]) {
    Thread a = new Thread(new Gbs());
    a.start();
    Thread b = new Thread(new TechQq());
    b.start();
    Thread c = new Thread(new Zixun21());
    c.start();
    Thread d = new Thread(new Cscomcn());
    d.start();
    Thread e = new Thread(new Goldcnfolcom());
    e.start();
  }
}
