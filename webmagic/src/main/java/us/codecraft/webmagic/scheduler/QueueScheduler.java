package us.codecraft.webmagic.scheduler;

import org.apache.http.annotation.ThreadSafe;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Basic Scheduler implementation.<br>
 * Store urls to fetch in LinkedBlockingQueue and remove duplicate urls by HashMap.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class QueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

  private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

  @Override
  public void pushWhenNoDuplicate(Request request, Task task) {
    queue.add(request);
  }

  @Override
  public synchronized Request poll(Task task) {
    return queue.poll();
  }

  @Override
  public int getLeftRequestsCount(Task task) {
    return queue.size();
  }

  @Override
  public int getTotalRequestsCount(Task task) {
    return getDuplicateRemover().getTotalRequestsCount(task);
  }

  @Override
  public int getLeftRequestsCount() {
    // TODO Auto-generated method stub
    return queue.size();
  }

  @Override
  public void addUrlList(ArrayList<Request> url,Task task) {
    // TODO Auto-generated method stub

  }
}
