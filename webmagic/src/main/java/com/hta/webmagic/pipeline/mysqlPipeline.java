package com.hta.webmagic.pipeline;

import com.hta.webmagic.model.Result;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 
 * @author xuexianwu
 *
 */
public class MysqlPipeline implements Pipeline {
  public void process(ResultItems resultItems, Task task) {
    Result r = (Result) resultItems.get("result");
    String table = r.getTable();
    DBOPerate.insertResults3(r, table);

  }
}
