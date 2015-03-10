package com.hta.webmagic.pipeline;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 
 * @author xuexianwu
 *
 */
public class DBConnection {
  public static Connection getConnection() {
    // TODO Auto-generated method stub
    Connection con = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");

      con = DriverManager.getConnection(
          "jdbc:mysql://172.16.4.107:3306/spiderdata?useUnicode=true&characterEncoding=utf-8",
          "root", "123456");

    } catch (Exception e) {
      System.out.println(" connect mysql error " + e.getMessage());
      e.printStackTrace();
    }
    return con;
  }

  public static Connection getLocalConnection() {
    // TODO Auto-generated method stub
    Connection con = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");

      con = DriverManager.getConnection(
          "jdbc:mysql://127.0.0.1:3306/spiderdata?useUnicode=true&characterEncoding=utf-8", "root",
          "");

    } catch (Exception e) {
      System.out.println(" connect mysql error " + e.getMessage());
      e.printStackTrace();
    }
    return con;
  }

}
