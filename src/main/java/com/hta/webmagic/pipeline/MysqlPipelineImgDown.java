package com.hta.webmagic.pipeline;

import java.util.List;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.hta.webmagic.model.Image;
import com.hta.webmagic.model.Result;
import com.hta.webmagic.processor.utils.DownloadFile;
/**
 * 
 * @author xuexianwu
 *
 */
public class MysqlPipelineImgDown implements Pipeline {
  public void process(ResultItems resultItems, Task task) {
    Result r = (Result) resultItems.get("result");
    String table = r.getTable();
    int k = DBOPerate.insertResults3(r, table);
    if (k > 0) {
      List<Image> imgList = r.getImage();
      if (imgList != null) {
        System.out.println("Boolean:\t" + imgList.isEmpty());
        for (Image singleimg : imgList) {
           DownloadFile.fileDown(singleimg.getSrcOriginalPath(), singleimg.getLocalFilePath(),
           singleimg.getLocalPath());
           DBOPerate.insertImage(singleimg, k);
        }
      }

    } else {
      System.out.println("Skip........");
    }

  }
}

