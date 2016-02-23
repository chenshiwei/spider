package com.hta.webmagic.processor.bucket141113;

/**
 * 
 * @author xuexianwu
 * @version 1.11 <br>
 *          piceures download thumbnail download<br>
 *          config
 *
 */
public class bucket141113Run {

  public static void main(String[] args) {
    // TODO Auto-generated method stub

    // Thread a = new Thread(new Chinais());
    // a.start();
    Thread b = new Thread(new Cnbetacom());
    b.start();
    Thread e = new Thread(new Iresearchcn());
    e.start();
    Thread i = new Thread(new TechcrunchCnProcessor());
    i.start();
    System.out.println("bucket141113 begin..");
  }

}
