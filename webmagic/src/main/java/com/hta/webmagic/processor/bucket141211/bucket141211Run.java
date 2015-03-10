package com.hta.webmagic.processor.bucket141211;

public class bucket141211Run {
  public static void main(String args[]) {
    Thread a = new Thread(new Admin5Processor());
    a.start();
    Thread b = new Thread(new DataguruCn());
    b.start();
  }
}
