package com.hta.webmagic.processor.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hta.webmagic.model.Image;
import com.hta.webmagic.model.ImageResult;

public class FindImgage {
  public static ImageResult fliterimgT(Elements Content, String domin, String localpath,
      String localurl, String url) {
    localpath = localpath + domin.replace(".", "_") + "/";
    localurl = localurl + domin.replace(".", "_") + "/";
    ImageResult imgresult = new ImageResult();
    ArrayList<Image> ImgList = new ArrayList<Image>();
    String content = ChangeP.replacep(Content.toString()); // replace <P>
    Elements Imgs = Content.select("img"); // flit <img>
    for (Element singleimg : Imgs) {
      // System.out.println("----img src" + singleimg.attr("src"));
      Image imgtemp = new Image();
      imgtemp.setImgOriginal(singleimg.toString());
      imgtemp.setLocalHttp(localurl);
      imgtemp.setSrcOriginalPath(handSrcUrl2(singleimg.attr("src"), url)); // chulihou //
                                                                           // zhixizaisrcurl
      imgtemp.setLocalPath(localpath);
      imgtemp.setDomin(domin);
      imgtemp.setFileType(findFileType(singleimg.attr("src")));// fileType/png/jpg/jpeg
      ImgList.add(imgtemp);
      // showinfo.printImgresult(imgtemp);
    }

    ArrayList<Image> ImgList2 = DownloadFile.unloadNewIimg(ImgList);
    for (Image singleimg : ImgList2) {
      // content.replace(singleimg.getImgOriginal(), singleimg.getImgAfter());
      content = content.replace(singleimg.getImgOriginal(), singleimg.getImgAfter());
      // showinfo.printImgresult(singleimg);

    }
    imgresult.setContent(content);
    if (!ImgList2.isEmpty()) {
      imgresult.setFirstImgUrl(ImgList2.get(0).getSrcAfterPath());
    }
    return imgresult; // 返回一个替换过标签的html和第一张图片的firsterurl
  }

  public static ImageResult fliterimg(Elements Content, String domin, String localpath,
      String localurl) {
    localpath = localpath + domin.replace(".", "_") + "/";
    localurl = localurl + domin.replace(".", "_") + "/";
    ImageResult imgresult = new ImageResult();
    ArrayList<Image> ImgList = new ArrayList<Image>();
    String content = ChangeP.replacep(Content.html()); // replace <P>

    Elements Imgs = Content.select("img"); // flit <img>
    for (Element singleimg : Imgs) {
      Image imgtemp = new Image();
      imgtemp.setImgOriginal(singleimg.toString());
      imgtemp.setLocalHttp(localurl);
      imgtemp.setSrcOriginalPath(handSrcUrl(singleimg.attr("src"), domin)); // chulihou //
                                                                            // zhixizaisrcurl
      imgtemp.setLocalPath(localpath);
      imgtemp.setDomin(domin);
      imgtemp.setFileType(findFileType(singleimg.attr("src")));// fileType/png/jpg/jpeg
      ImgList.add(imgtemp);
      // showinfo.printImgresult(imgtemp);
    }

    ArrayList<Image> ImgList2 = DownloadFile.unloadNewIimg(ImgList);
    for (Image singleimg : ImgList2) {
      content = content.replace(singleimg.getImgOriginal(), singleimg.getImgAfter());
    }
    imgresult.setContent(content);
    if (!ImgList2.isEmpty()) {
      imgresult.setFirstImgUrl(ImgList2.get(0).getSrcOriginalPath());
      imgresult.setImgList(ImgList2);
    }
    return imgresult; // 返回一个替换过标签的html和第一张图片的firsterurl
  }

  public static ImageResult fliterimg2(Elements Content, String domin, String localpath,
      String localurl) {
    localpath = localpath + domin.replace(".", "_") + "/";
    localurl = localurl + domin.replace(".", "_") + "/";
    ImageResult imgresult = new ImageResult();
    ArrayList<Image> ImgList = new ArrayList<Image>();
    String content = ChangeP.replacep(Content.html()); // replace <P>

    Elements Imgs = Content.select("img"); // flit <img>
    for (Element singleimg : Imgs) {
      Image imgtemp = new Image();
      imgtemp.setImgOriginal(singleimg.toString());
      imgtemp.setLocalHttp(localurl);
      imgtemp.setSrcOriginalPath(handSrcUrl(singleimg.attr("src"), domin)); // chulihou //
                                                                            // zhixizaisrcurl
      imgtemp.setLocalPath(localpath);
      imgtemp.setDomin(domin);
      imgtemp.setFileType(findFileType(singleimg.attr("src")));// fileType/png/jpg/jpeg
      ImgList.add(imgtemp);
      // showinfo.printImgresult(imgtemp);
    }

    ArrayList<Image> ImgList2 = DownloadFile.loadNewIimg(ImgList);
    for (Image singleimg : ImgList2) {
      content = content.replace(singleimg.getImgOriginal(), singleimg.getImgAfter());
    }
    imgresult.setContent(content);
    if (!ImgList2.isEmpty()) {
      imgresult.setFirstImgUrl(ImgList2.get(0).getSrcAfterPath());
    }
    return imgresult; // 返回一个替换过标签的html和第一张图片的firsterurl
  }

  public static String firstImg(Elements image, String domin, String localpath, String localurl) {
    localurl = localurl + domin.replace(".", "_") + "/";
    localpath = localpath + domin.replace(".", "_") + "/";
    String newpath = null;
    Element img = image.first();
    String src = img.attr("src");
    if (src.matches("http://[^#]*")) {
      newpath = DownloadFile.loadfirstimg(src, localpath);
    } else {
      String srcurl = "http://" + domin + "/" + src;
      System.out.println("http src" + srcurl);
      newpath = DownloadFile.loadfirstimg(srcurl, localpath);
    }
    System.out.println("first img src:" + src + "\nlocal" + localpath + newpath);
    return localurl + newpath;
  }

  public static String imgLoad(Elements image, String domin, String localpath, String localurl) {
    localurl = localurl + domin.replace(".", "_") + "/";
    localpath = localpath + domin.replace(".", "_") + "/";
    String newpath = null;
    ArrayList<String> srcList = new ArrayList<String>();
    for (Element img : image) {
      //
      String imgHtml = img.toString();
      String src = img.attr("src");
      System.out.println("loop src :" + src + "\n" + imgHtml);
      srcList.add(src);
    }
    Element img = image.first();
    // 开始获取正确的url链接
    String src = img.attr("src");
    if (src.matches("http://[^#]*")) {
      newpath = DownloadFile.loadfirstimg(src, localpath);
    } else {
      String srcurl = "http://" + domin + "/" + src;
      System.out.println("http src" + srcurl);
      newpath = DownloadFile.loadfirstimg(srcurl, localpath);
    }
    System.out.println("first img src:" + src + "\nlocal" + localpath + newpath);
    return localurl + newpath;
  }

  public static ImageResult fliterSooTooimg(Elements Content, String domin, String localpath,
      String localurl) {
    localpath = localpath + domin.replace(".", "_") + "/";
    localurl = localurl + domin.replace(".", "_") + "/";
    ImageResult imgresult = new ImageResult();
    ArrayList<Image> ImgList = new ArrayList<Image>();
    String content = ChangeP.replacep(Content.toString()); // replace <P>
    Elements Imgs = Content.select("img"); // flit <img>
    for (Element singleimg : Imgs) {
      // System.out.println("----img src" + singleimg.attr("data-original"));
      Image imgtemp = new Image();
      imgtemp.setImgOriginal(singleimg.toString());
      imgtemp.setLocalHttp(localurl);
      imgtemp.setSrcOriginalPath(handSrcUrl(singleimg.attr("data-original"), domin)); // chulihou //
      // zhixizaisrcurl
      imgtemp.setLocalPath(localpath);
      imgtemp.setDomin(domin);
      imgtemp.setFileType(findFileType(singleimg.attr("data-original")));// fileType/png/jpg/jpeg
      ImgList.add(imgtemp);
      // showinfo.printImgresult(imgtemp);
    }

    ArrayList<Image> ImgList2 = DownloadFile.loadNewIimg(ImgList);
    for (Image singleimg : ImgList2) {
      // content.replace(singleimg.getImgOriginal(), singleimg.getImgAfter());
      content = content.replace(singleimg.getImgOriginal(), singleimg.getImgAfter());
      // showinfo.printImgresult(singleimg);

    }
    imgresult.setContent(content);
    if (!ImgList2.isEmpty()) {
      imgresult.setFirstImgUrl(ImgList2.get(0).getSrcAfterPath());
    }
    return imgresult; // 返回一个替换过标签的html和第一张图片的firsterurl
  }

  private static String findFileType(String src) {
    String fileType = "";
    if (src == null | src == "") {
      fileType = "null";
    } else if (Pattern.matches(".*jpg[^#]*", src)) {
      fileType = ".jpg";
    } else if (Pattern.matches(".*jpeg[^#]*", src)) {
      fileType = ".jpeg";
    } else if (Pattern.matches(".*png[^#]*", src)) {
      fileType = ".png";
    } else if (Pattern.matches(".*gif[^#]*", src)) {
      fileType = "null";
    } else if (Pattern.matches(".*aspx[^#]*", src)) {
      fileType = "null";
    } else if (Pattern.matches(".*dll[^#]*", src)) {
      fileType = "null";
    } else if (Pattern.matches(".*php[^#]*", src)) {
      fileType = "null";
    } else {
      fileType = "";
    }
    return fileType;
  }

  private static String handSrcUrl(String srcurl, String domin) {
    String temp = null;
    if (srcurl.matches("(http://[^#]*|https://[^#]*)")) {
      temp = srcurl;
    } else if (srcurl.matches("../[^#]*")) {
      srcurl = srcurl.replace("../", ""); // thebigdata.cn
      temp = "http://" + domin + "/" + srcurl;
    } else {
      temp = "http://" + domin + "/" + srcurl;
    }
    return temp;
  }

  private static String handSrcUrl2(String srcurl, String url) {
    String temp = null;
    if (srcurl.matches("http://[^#]*")) {
      temp = srcurl;
    } else if (srcurl.matches("../[^#]*")) {
      int cnt = 1;
      int index = 0;
      for (int i = 0; i < srcurl.length(); i += 3) {
        if (srcurl.substring(i, i + 3).equals("../"))
          cnt++;
        else
          break;
      }
      int srcurlCnt = cnt - 1;
      int i = url.length() - 1;
      while (cnt > 0) {
        for (; i >= 0 && url.charAt(i) != '/'; i--) {
          index++;
        }
        index++;
        i--;
        cnt--;
      }
      temp = url.substring(0, url.length() - index + 1) + srcurl.substring(3 * srcurlCnt);
    } else {
      URL urll;
      try {
        urll = new URL(url);
        urll.getHost();
        temp = "http://" + urll.getHost() + "/" + srcurl;
      } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    return temp;
  }
}
