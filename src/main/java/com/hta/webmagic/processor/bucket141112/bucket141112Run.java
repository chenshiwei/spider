package com.hta.webmagic.processor.bucket141112;

/**
 * 
 * @author xuexianwu
 * @version 1.11<br>
 *          image download and humbnail download<br>
 *          config
 * 
 *
 */
public class bucket141112Run {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Thread a = new Thread(new Dop2pcomProcessor());
    a.start();
    Thread c = new Thread(new FinanceSinaProcessor());
    c.start();
    Thread d = new Thread(new ItfmagProcessor());
    d.start();
    Thread f1 = new Thread(new JrzjcomProcessor());
    f1.start();

  }

}
