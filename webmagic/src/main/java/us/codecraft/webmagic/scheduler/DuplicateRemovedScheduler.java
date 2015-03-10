package us.codecraft.webmagic.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hta.webmagic.processor.utils.BFDuplicateRemover;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

/**
 * Remove duplicate urls and only push urls which are not duplicate.<br>
 * </br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

  private DuplicateRemover queueDuplicatedRemover = new HashSetDuplicateRemover();

  // private DuplicateRemover duplicatedRemover;

  public DuplicateRemover getDuplicateRemover() {
    return duplicatedRemover;
  }

  public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
    this.duplicatedRemover = duplicatedRemover;
    return this;
  }

  @Override
  public void push(Request request, Task task) {
    logger.trace("get a candidate url {}", request.getUrl());
    // logger.info("duplicate" + request.getUrl() + "\tTask\t" + task.toString());
    
    //determine whether to pasre the request 
    if (!duplicatedRemover.isDuplicate(request, task)) {
      request.putExtra("parse", "true");
    } else {
      request.putExtra("parse", "false");
    }
    //determin whether to filter the request
    if (!queueDuplicatedRemover.isDuplicate(request, task) || shouldReserved(request)) {
      // logger.info("push to queue {}", request.getUrl());
      pushWhenNoDuplicate(request, task);
    }
  }

  protected boolean shouldReserved(Request request) {
    return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
  }

  protected void pushWhenNoDuplicate(Request request, Task task) {

  }
}
