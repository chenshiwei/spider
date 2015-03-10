package com.hta.webmagic.processor.utils;

import com.hta.webmagic.model.Image;
import com.hta.webmagic.model.Result;

/**
 * 
 * @author xuexianwu
 * @version 1.35 <br>
 *          print the result
 */
public final class Showinfo {
  private Showinfo() {

  }

  public static void printresult(Result rs) {

    System.out.println("----------------------------------------\n" + rs.getUrl() + "\n deep\t"
        + rs.getDeep() + "\n first img \t" + rs.getImgSrc() + "\nkeywords \t" + rs.getKeywords()
        + "\n description \t" + rs.getDescription() + "\n title \t" + rs.getTitle() + "\nTime\t"
        + rs.getTimeDate() + "\nAuthor\t" + rs.getAuthor() + "\nTag\t" + rs.getCategory()
        + "\ncontent\t" + rs.getContent());

  }

  public static void printImgresult(Image rs) {

    System.out.println("Image----------------------------------------\n Image before: "
        + rs.getImgOriginal() + "\n fileType\t" + rs.getFileType() + "\nImageAfter \t"
        + rs.getImgAfter() + "\n OriginalPath \t" + rs.getSrcOriginalPath() + "\n AfterPath \t"
        + rs.getSrcAfterPath() + "\nLaocalPath\t" + rs.getLocalPath());

  }

}
