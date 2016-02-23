package com.hta.webmagic.processor.bucket141111;

/**
 * 
 * @author xuexianwu
 * @version 1.11<br>
 *          image download and humbnail download deep \local config
 * 
 *
 */
public final class Bucket141111Run {
  private Bucket141111Run() {

  }

  public static void main(String[] args) {
    Thread a = new Thread(new Bigdatascn());
    a.start();
    Thread b = new Thread(new Blogitpubnet());
    b.start();
    Thread c = new Thread(new Gzdashujucom());
    c.start();
    Thread d = new Thread(new Raincentcom());
    d.start();
    Thread f1 = new Thread(new Zdnetcomcn());
    f1.start();
  }

}
