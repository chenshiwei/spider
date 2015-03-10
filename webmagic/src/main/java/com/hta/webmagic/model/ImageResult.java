package com.hta.webmagic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xuexianwu
 *
 */
public class ImageResult {
  private String firstImgUrl;
  private String content;
  private List<Image> imgList;

  public String getFirstImgUrl() {
    return firstImgUrl;
  }

  public void setFirstImgUrl(String firstImgUrl) {
    this.firstImgUrl = firstImgUrl;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<Image> getImgList() {
    return imgList;
  }

  public void setImgList(List<Image> imgList) {
    this.imgList = imgList;
  }

}
