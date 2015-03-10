package com.hta.webmagic.processor.bucket141114;

/**
 * 
 * @author xuexianwu<br>
 * @version 1.05 picture download
 *
 */

public class bucket141114Run {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Thread a = new Thread(new Cto51com());
    a.start();
    Thread b = new Thread(new Lieyunwangcom());
    b.start();
    Thread c = new Thread(new Chinais());
    c.start();
    Thread d = new Thread(new TechnodComProcessor());
    d.start();
    Thread f1 = new Thread(new Kr36com());
    f1.start();
  }

}
