package com.hta.webmagic.processor.bucket141212;

public class bucket141212Run {
  public static void main(String args[]) {
    Thread a = new Thread(new Hexun());
    a.start();
    Thread a1 = new Thread(new Techhexun());
    a1.start();
    Thread b = new Thread(new Socialbeta());
    b.start();
    Thread c = new Thread(new StockSohu());
    c.start();
    Thread d = new Thread(new Jrj_com_cn());
    d.start();
    Thread f = new Thread(new Gw_com_cn());
    f.start();
  }
}
