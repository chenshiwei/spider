package com.hta.webmagic.processor.utils;


import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;






import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;



public class BFDuplicateRemover implements DuplicateRemover {

  private int expectedInsertions;

  private double fpp;

  private AtomicInteger counter;
  
  private StoreReadUrl sru;
  
  private BloomFilter<CharSequence> bloomFilter;
  
  public BFDuplicateRemover(int expectedInsertions,String filename) {
      this(expectedInsertions, 0.01,filename);
  }

  /**
   *
   * @param expectedInsertions the number of expected insertions to the constructed
   * @param fpp the desired false positive probability (must be positive and less than 1.0)
   */
  public BFDuplicateRemover(int expectedInsertions, double fpp,String filename) {
      this.expectedInsertions = expectedInsertions;
      this.fpp = fpp;
      this.bloomFilter = rebuildBloomFilter();
      this.sru = new StoreReadUrl(filename);
      this.bloomFilter = sru.readUrl(this.bloomFilter);
  }
  
  public BFDuplicateRemover(int expectedInsertions, double fpp) {
    this.expectedInsertions = expectedInsertions;
    this.fpp = fpp;
    this.bloomFilter = rebuildBloomFilter();
 }
 
  protected BloomFilter<CharSequence> rebuildBloomFilter() {
      counter = new AtomicInteger(0);
      return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
  }

  

  @Override
  public synchronized boolean isDuplicate(Request request, Task task) {
      boolean isDuplicate = bloomFilter.mightContain(getUrl(request));
      if (!isDuplicate) {
          bloomFilter.put(getUrl(request));
          sru.writeUrl(getUrl(request));
          counter.incrementAndGet();
      }
      return isDuplicate;
  }
  
  public boolean isQueueDuplicate(Request request, Task task) {
    boolean isDuplicate = bloomFilter.mightContain(getUrl(request));
    if (!isDuplicate) {
        bloomFilter.put(getUrl(request));
    }
    return isDuplicate;
 }
  

  protected String getUrl(Request request) {
      return request.getUrl();
  }

  @Override
  public void resetDuplicateCheck(Task task) {
      rebuildBloomFilter();
  }

  @Override
  public int getTotalRequestsCount(Task task) {
      return counter.get();
  }
}
