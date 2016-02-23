package com.hta.webmagic.pipeline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hta.webmagic.model.Image;
import com.hta.webmagic.model.Result;

/**
 * 
 * @author xuexianwu
 *
 */
public final class DBOPerate {
  private DBOPerate() {

  }

  static Connection conn;
  static Statement st;

  public static void insertResults(Result rs, String table) {
    Connection connection = DBConnection.getConnection();
    PreparedStatement ps = null;
    java.sql.Timestamp ti = new java.sql.Timestamp(rs.getTimeDate().getTime());
    try {
      String sql = "insert into "
          + table
          + " (Url,title,writer,keywords,description,summary,content,create_time,crawler_time,source,category_navigation,site,first_img_src) select '"
          + rs.getUrl() + "','" + rs.getTitle() + "','" + rs.getAuthor() + "','" + rs.getKeywords()
          + "','" + rs.getDescription() + "','" + rs.getSumm() + "','" + rs.getContent() + "','"
          + ti + "','" + new Timestamp(System.currentTimeMillis()) + "','" + rs.getSource() + "','"
          + rs.getCategory() + "','" + rs.getSite() + "','" + rs.getImgSrc()
          + "' from dual where not exists(select url from " + table
          + " where TO_DAYS(NOW()) - TO_DAYS(crawler_time) <= 30 and url='" + rs.getUrl() + "')";
      ps = connection.prepareStatement(sql);
      ps.addBatch();
      ps.executeBatch();
      ps.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public static void insertResults2(Result rs, String table) {
    // TODO Auto-generated method stub

    Connection connection = DBConnection.getConnection();
    PreparedStatement ps = null;
    try {
      String sql2 = "insert into "
          + table
          + " (Url,title,writer,keywords,description,summary,content,create_time,crawler_time,source,category_navigation,site) select (?, ?, ?,?,?,?,?,?,?,?,?,?)from dual where not exists(select url from "
          + table + " where TO_DAYS(NOW()) - TO_DAYS(crawler_time) <= 30 and url='" + rs.getUrl()
          + "')";
      ps = connection.prepareStatement(sql2);
      ps.setString(1, rs.getUrl());
      ps.setString(2, rs.getTitle());
      ps.setString(3, rs.getAuthor());
      ps.setString(4, rs.getKeywords());
      ps.setString(5, rs.getDescription());
      ps.setString(6, rs.getSumm());
      ps.setString(7, rs.getContent());
      ps.setString(8, rs.getTime());
      ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
      ps.setString(10, rs.getSource());
      ps.setString(11, rs.getCategory());
      ps.setString(12, rs.getSite());
      ps.addBatch();
      ps.executeBatch();
      ps.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {

        connection.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  public static int insertResults3(Result rs, String table) {
    Connection connection = DBConnection.getConnection();
    PreparedStatement ps = null;
    int id = 0;
    java.sql.Timestamp ti = new java.sql.Timestamp(rs.getTimeDate().getTime());
    try {
      String sql = "insert into "
          + table
          + " (Url,title,writer,keywords,description,summary,content,create_time,crawler_time,source,category_navigation,site,first_img_src) select '"
          + rs.getUrl() + "','" + rs.getTitle() + "','" + rs.getAuthor() + "','" + rs.getKeywords()
          + "','" + rs.getDescription() + "','" + rs.getSumm() + "','" + rs.getContent() + "','"
          + ti + "','" + new Timestamp(System.currentTimeMillis()) + "','" + rs.getSource() + "','"
          + rs.getCategory() + "','" + rs.getSite() + "','" + rs.getImgSrc()
          + "' from dual where not exists(select url from " + table
          + " where TO_DAYS(NOW()) - TO_DAYS(crawler_time) <= 30 and url='" + rs.getUrl() + "')";
      ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.addBatch();
      ps.executeBatch();
      ResultSet result = ps.getGeneratedKeys();
      if (result.next()) {
        id = result.getInt(1);
      }
      ps.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {

        connection.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    // System.out.println("\n----------------------------------------------\narticleID:" + id);
    return id;
  }

  public static void insertResults4(Result rs, String table) {
    // TODO Auto-generated method stub

    Connection connection = DBConnection.getConnection();
    PreparedStatement ps = null;
    try {
      String sql = "insert into "
          + table
          + " (Url,title,writer,keywords,description,content,create_time,crawler_time,source,category_navigation,site,first_img_src) values (?, ?, ?,?,?,?,?,?,?,?,?,?)";
      ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, rs.getUrl());
      ps.setString(2, rs.getTitle());
      ps.setString(3, rs.getAuthor());
      ps.setString(4, rs.getKeywords());
      ps.setString(5, rs.getDescription());
      ps.setString(6, rs.getContent());
      // ps.setDate(7, new java.sql.Date(rs.getTimeD().getTime()));
      ps.setTimestamp(7, new java.sql.Timestamp(rs.getTimeDate().getTime()));
      ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
      ps.setString(9, rs.getSource());
      ps.setString(10, rs.getCategory());
      ps.setString(11, rs.getSite());
      ps.setString(12, rs.getImgSrc());
      ps.addBatch();
      ps.executeBatch();
      // ResultSet result = ps.getGeneratedKeys();
      // if (result.next()) {
      // // 仅一列
      // id = result.getLong(1);
      // // System.out.println("----插入后得到的主键 = " + id);
      // return id;
      // }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        ps.close();
        connection.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  public static void insertImageId(String newsID, ArrayList<Image> image, String table) {
    // TODO Auto-generated method stub
    Connection connection = DBConnection.getConnection();
    PreparedStatement ps = null;
    try {
      String sql = "insert into " + table + " (localPath,urlPath,newsID) values (?,?,?)";
      ps = connection.prepareStatement(sql);
      for (Image tep : image) {
        ps.setString(1, tep.getLocalPath());
        ps.setString(3, newsID);
        ps.addBatch();
      }
      ps.executeBatch();
      ps.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {

        connection.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  public static void update(String sql) {
    conn = DBConnection.getConnection();
    try {

      st = (Statement) conn.createStatement();

      int count = st.executeUpdate(sql);

      System.out.println("result表中更新 " + count + " 条数");

      conn.close();
    } catch (SQLException e) {
      System.out.println("更新数据失败");
    }
  }

  public static java.sql.Date getdate() {
    java.sql.Date d2 = null;
    Calendar cal = Calendar.getInstance();
    java.util.Date d1;
    try {
      d1 = cal.getTime();
      System.out.println("d1" + d1);
      d2 = new java.sql.Date(d1.getTime()); // 再转换为sql.Date对象
      System.out.println("d2" + d2);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } // 先把字符串转为util.Date对象

    return d2;

  }

  public static void insertImage(Image singleimg, int newsID) {
    Connection connection = DBConnection.getConnection();
    PreparedStatement ps = null;
    try {
      String sql2 = "insert into image (localPath,urlPath,newsID)values(?,?,?)";
      ps = connection.prepareStatement(sql2);
      ps.setString(1, singleimg.getLocalFilePath());
      ps.setString(2, singleimg.getSrcAfterPath());
      ps.setInt(3, newsID);
      ps.addBatch();
      ps.executeBatch();
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {

        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }

  public static List<Image> resultImageQuery(String sql) {

    conn = DBConnection.getLocalConnection();
    List<Image> list = new ArrayList<Image>();

    try {
      st = (Statement) conn.createStatement();
      ResultSet rs = st.executeQuery(sql);
      while (rs.next()) {
        Image temp = new Image();
        temp.setLocalPath(rs.getString("localPath").toString());
        list.add(temp);
      }
      st.close();
      conn.close();

    } catch (SQLException e) {
      System.out.println("lotquery error...");
      e.printStackTrace();

    } finally {
      try {
        if (st != null)
          st.close();
        if (conn != null)
          conn.close();
      } catch (SQLException e) {
        System.out.println("close error...");
        e.printStackTrace();
      }
    }
    return list;
  }

}
