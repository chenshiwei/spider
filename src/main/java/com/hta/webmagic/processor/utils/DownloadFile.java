package com.hta.webmagic.processor.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hta.webmagic.model.Image;

/**
 * 
 * @author xuexianwu
 *
 */
public final class DownloadFile {
  private DownloadFile() {

  }

  static int byteNumber = 1024;
  @SuppressWarnings("deprecation")
  static HttpClient client = new DefaultHttpClient();

  @SuppressWarnings("deprecation")
  public static String loadfile(String url, String path) {
    HttpGet httpGet = new HttpGet(url);
    String name = Long.toString(System.currentTimeMillis());
    String fileType = url.substring(url.lastIndexOf("."), url.length());
    HttpResponse response;
    try {
      response = client.execute(httpGet);
      IOUtils.copy(response.getEntity().getContent(), new FileOutputStream(path + name + fileType));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      // httpGet.releaseConnection();
      client.getConnectionManager().closeExpiredConnections();

    }

    return name + fileType;

  }

  public static boolean isexitsPath(String path) throws InterruptedException {
    String[] paths = path.split("/");
    StringBuffer fullPath = new StringBuffer();
    for (int i = 0; i < paths.length; i++) {
      // System.out.println(paths[i]);
      fullPath.append(paths[i]).append("/");
      File file = new File(fullPath.toString());
      if (paths.length - 1 != i) {
        if (!file.exists()) {
          file.mkdir();
          System.out.println("创建目录为：" + fullPath.toString());
          Thread.sleep(2);
        }
      }
    }
    File file = new File(fullPath.toString());
    return !file.exists();
  }

  public static List<Image> loadNewIimg(List<Image> imgList) {
    int i = 1;
    String name = getStrTime();
    for (Image imgtemp : imgList) {
      if (imgtemp.getFileType().matches("null")) {
        imgtemp.setImgAfter(" ");
      } else {
        String fileType = imgtemp.getFileType();
        String file = imgtemp.getLocalPath() + name + "_" + i + fileType;
        String httpurl = imgtemp.getLocalHttp() + name + "_" + i + fileType;
        String imgAfter = assembleImg(httpurl);

        // fileDown(imgtemp.getSrcOriginalPath(), file, imgtemp.getLocalPath());
        imgtemp.setLocalFilePath(file);
        imgtemp.setSrcAfterPath(httpurl);
        imgtemp.setImgAfter(imgAfter);
        // Showinfo.printImgresult(imgtemp);
        i++;
      }
    }
    return imgList;
  }

  public static List<Image> unloadNewIimg(List<Image> imgList) {
    for (Image imgtemp : imgList) {
      String imgAfter = assembleImg(imgtemp.getSrcOriginalPath());
      imgtemp.setImgAfter(imgAfter);
    }
    return imgList;
  }

  /**
   * 
   * @param url
   * @param path
   * @return
   */

  @SuppressWarnings({ "resource", "deprecation" })
  public static String loadimg(String url, String path) {

    HttpClient httpclient = new DefaultHttpClient();
    String name = Long.toString(System.currentTimeMillis());
    if (url.contains("?")) {
      url = url.substring(0, url.indexOf("?"));
    }
    String fileType = url.substring(url.lastIndexOf("."), url.length());
    try {
      HttpGet httpget = new HttpGet(url);

      // 伪装成google的爬虫JAVA问题查询
      httpget.setHeader("User-Agent",
          "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
      // Execute HTTP request
      // System.out.println("executing request " + httpget.getURI());
      HttpResponse response = httpclient.execute(httpget);
      isexitsPath(path + name + fileType);
      File storeFile = new File(path + name + fileType);
      FileOutputStream output = new FileOutputStream(storeFile);

      // 得到网络资源的字节数组,并写入文件
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        InputStream instream = entity.getContent();
        try {
          byte[] b = new byte[byteNumber];
          int j = 0;
          while ((j = instream.read(b)) != -1) {
            output.write(b, 0, j);
          }
          output.flush();
          output.close();

        } catch (IOException ex) {
          // In case of an IOException the connection will be
          // released
          // back to the connection manager automatically
          throw ex;
        } catch (RuntimeException ex) {
          // In case of an unexpected exception you may want to
          // abort
          // the HTTP request in order to shut down the underlying
          // connection immediately.
          httpget.abort();
          throw ex;
        } finally {
          // Closing the input stream will trigger connection
          // release
          try {
            instream.close();
          } catch (Exception ignore) {
            ignore.printStackTrace();
          }
        }
      }

    } catch (Exception e) {
      e.getMessage();
    } finally {
      httpclient.getConnectionManager().shutdown();
    }
    return name + fileType;
  }

  @SuppressWarnings({ "resource", "deprecation" })
  public static String loadfirstimg(String url, String path) {
    HttpClient httpclient = new DefaultHttpClient();
    String name = HashAlgorithms.oneByOneHash(url) + "thumbnail";
    if (url.contains("?")) {
      String urlStr;
      urlStr = url.substring(0, url.indexOf("?"));
    }
    String fileType = url.substring(url.lastIndexOf("."), url.length());
    try {
      HttpGet httpget = new HttpGet(url);

      // 伪装成google的爬虫JAVA问题查询
      httpget.setHeader("User-Agent",
          "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
      // Execute HTTP request
      // System.out.println("executing request " + httpget.getURI());
      HttpResponse response = httpclient.execute(httpget);
      isexitsPath(path + name + fileType);
      File storeFile = new File(path + name + fileType);
      FileOutputStream output = new FileOutputStream(storeFile);

      // 得到网络资源的字节数组,并写入文件
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        InputStream instream = entity.getContent();
        try {
          byte[] b = new byte[byteNumber];
          int j = 0;
          while ((j = instream.read(b)) != -1) {
            output.write(b, 0, j);
          }
          output.flush();
          output.close();

        } catch (IOException ex) {
          // In case of an IOException the connection will be
          // released
          // back to the connection manager automatically
          throw ex;
        } catch (RuntimeException ex) {
          // In case of an unexpected exception you may want to
          // abort
          // the HTTP request in order to shut down the underlying
          // connection immediately.
          httpget.abort();
          throw ex;
        } finally {
          // Closing the input stream will trigger connection
          // release
          try {
            instream.close();
          } catch (Exception ignore) {
            ignore.printStackTrace();
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      httpclient.getConnectionManager().shutdown();
    }
    return name + fileType;
  }

  /**
   * 
   * @return
   * @author xuexianwu<br>
   *         return picture name depend current time
   * 
   */
  public static String getStrTime() {
    String reStrTime = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    reStrTime = sdf.format(new Date());
    return reStrTime;

  }

  /**
   * 
   * @param url
   * @param file
   */
  public static String assembleImg(String path) {
    String tr = "<img src=\"" + path + "\"/>";
    return tr;
  }

  @SuppressWarnings({ "deprecation", "resource" })
  public static void fileDown(String url, String file, String filePath) {
    HttpClient httpclient = new DefaultHttpClient();
    try {
      HttpGet httpget = new HttpGet(url);
      httpget.setHeader("User-Agent",
          "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
      HttpResponse response = httpclient.execute(httpget);
      isexitsPath(file);
      File storeFile = new File(file);
      FileOutputStream output = new FileOutputStream(storeFile);
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        InputStream instream = entity.getContent();
        try {
          byte[] b = new byte[byteNumber];
          int j = 0;
          while ((j = instream.read(b)) != -1) {
            output.write(b, 0, j);
          }
          output.flush();
          output.close();

        } catch (IOException ex) {
          // In case of an IOException the connection will be
          // released
          // back to the connection manager automatically
          throw ex;
        } catch (RuntimeException ex) {
          // In case of an unexpected exception you may want to
          // abort
          // the HTTP request in order to shut down the underlying
          // connection immediately.
          httpget.abort();
          throw ex;
        } finally {
          // Closing the input stream will trigger connection
          // release
          try {
            instream.close();
          } catch (Exception ignore) {
            System.out.println("file download error");
          }
        }
      }

    } catch (Exception e) {
      e.getMessage();
    } finally {
      httpclient.getConnectionManager().shutdown();
    }

  }
}
