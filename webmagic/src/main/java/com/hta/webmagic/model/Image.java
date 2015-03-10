package com.hta.webmagic.model;
/**
 * 
 * @author xuexianwu
 *
 */
public class Image {
  private String localPath; 
  private String localHttp;
  private String srcOriginalPath; 
  private String srcAfterPath; 
  private String imgOriginal;
  private String imgAfter;
  private String fileType;
  private String domin;
  private String localFilePath;

  public String getLocalPath() {
    return localPath;
  }

  public void setLocalPath(String localPath) {
    this.localPath = localPath;
  }

  public String getImgOriginal() {
    return imgOriginal;
  }

  public void setImgOriginal(String imgOriginal) {
    this.imgOriginal = imgOriginal;
  }

  public String getImgAfter() {
    return imgAfter;
  }

  public void setImgAfter(String imgAfter) {
    this.imgAfter = imgAfter;
  }

  public String getSrcOriginalPath() {
    return srcOriginalPath;
  }

  public void setSrcOriginalPath(String srcOriginalPath) {
    this.srcOriginalPath = srcOriginalPath;
  }

  public String getSrcAfterPath() {
    return srcAfterPath;
  }

  public void setSrcAfterPath(String srcAfterPath) {
    this.srcAfterPath = srcAfterPath;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getDomin() {
    return domin;
  }

  public void setDomin(String domin) {
    this.domin = domin;
  }

  public String getLocalHttp() {
    return localHttp;
  }

  public void setLocalHttp(String localHttp) {
    this.localHttp = localHttp;
  }

  public String getLocalFilePath() {
    return localFilePath;
  }

  public void setLocalFilePath(String localFilePath) {
    this.localFilePath = localFilePath;
  }
  
  

}
