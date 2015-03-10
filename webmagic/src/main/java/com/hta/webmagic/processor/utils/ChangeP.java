package com.hta.webmagic.processor.utils;

/**
 * 
 * @author xuexianwu
 *
 */
public final class ChangeP {
  private ChangeP() {

  }

  public static String replacep(String html) {

    String tr = html.replaceAll("<p[^>]*", "<p");

    return tr;
  }

  public static String reDiv2P(String html) {

    String tr = html.replaceAll("<div[^>]*", "<p");
    tr = tr.replaceAll("</div>", "</p>");
    return tr;
  }

  public static String reDivClass(String html, String divclass) {

    String tr = html.replaceAll("<div class=\"" + divclass + "\"[^div>]*div>", "");
    return tr;
  }

  public static String reDivID(String html, String divid) {

    String tr = html.replaceAll("<div id=\"" + divid + "\"[^</div>]*</div>", "");
    return tr;
  }

  public static String replaceSrc(String html, String path) {

    String tr = html.replaceAll("<img[^>]*>", "<img src=\"" + path + "\"/>");

    return tr;
  }

  public static String replaceA(String html) {

    String tr = html.replaceAll("<a[^>]*>", "");
    tr = tr.replaceAll("</a>", "");

    return tr;
  }

  public static String replaceDiv(String html) {

    String tr = html.replaceAll("<div[^@]*</div>", " ");

    return tr;
  }

  public static String replaceJs(String html) {

    String tr = html.replaceAll("<script>[^@]*</script>", " ");

    return tr;
  }

  public static String replaceSpan(String html) {

    String tr = html.replaceAll("<span[^>]*>", "");
    tr = tr.replaceAll("</span>", "");

    return tr;
  }

  public static String replaceDIV(String html) {

    String tr = html.replaceAll("<UL[^UL>]*UL>", "");
    return tr;
  }

  public static String replaceXML(String html) {

    String tr = html.replaceAll("<xml>[^@]*</xml>", " ");
    return tr;
  }

  public static String replaceAll(String html) {
    String htmlStr;
    htmlStr = html.replaceAll("(?is)<!--.*?-->", "");
    htmlStr = htmlStr.replaceAll("(?is)<script.*?>.*?</script>", "");
    htmlStr = htmlStr.replaceAll("(?is)<style.*?>.*?</style>", "");
    htmlStr = htmlStr.replaceAll("&.{2,5};|&#.{2,5};", " ");
    htmlStr = htmlStr.replaceAll("(?is)<.*?>", "");
    return htmlStr;
  }

  public static String htnlHand(String html) {
    String htmlStr;
    htmlStr = replacep(html);
    htmlStr = htmlStr.replaceAll("<img", "Timg");
    htmlStr = htmlStr.replaceAll("<p>", "TP1");
    htmlStr = htmlStr.replaceAll("</p>", "TP2");
    htmlStr = htmlStr.replaceAll("<strong>", "ST1");
    htmlStr = htmlStr.replaceAll("</strong>", "ST2");
    htmlStr = htmlStr.replaceAll("(<br>|<br/>|<br />|<br />)", "BR1");
    htmlStr = replaceAll(htmlStr);
    htmlStr = htmlStr.replaceAll("Timg", "<img");
    htmlStr = htmlStr.replaceAll("TP1", "<p>");
    htmlStr = htmlStr.replaceAll("TP2", "</p>");
    htmlStr = htmlStr.replaceAll("ST1", "<strong>");
    htmlStr = htmlStr.replaceAll("ST2", "</strong>");
    htmlStr = htmlStr.replaceAll("BR1", "<br/>");
    return htmlStr;
  }

  /**
   * 
   * @param html
   * @return String
   * @author xuexianwu <br>
   *         guizhoudashujv ONLY fliter div
   */
  public static String htmlHandGzdashuju(String html) {
    String htmlStr;
    htmlStr = replacep(html);
    htmlStr = htmlStr.replaceAll("<img", "Timg");
    htmlStr = htmlStr.replaceAll("<p>", "TP1");
    htmlStr = htmlStr.replaceAll("</p>", "TP2");
    htmlStr = htmlStr.replaceAll("<div class=\"bdsharebuttonbox\">[^?]*</div>", "");
    htmlStr = replaceAll(htmlStr);
    htmlStr = htmlStr.replaceAll("Timg", "<img");
    htmlStr = htmlStr.replaceAll("TP1", "<p>");
    htmlStr = htmlStr.replaceAll("TP2", "</p>");
    return htmlStr;
  }

  public static String htmlHandAmdin5(String html) {
    String htmlStr;
    htmlStr = replacep(html);
    htmlStr = htmlStr.replaceAll("<img", "Timg");
    htmlStr = htmlStr.replaceAll("<p>", "TP1");
    htmlStr = htmlStr.replaceAll("</p>", "TP2");
    htmlStr = replaceAll(htmlStr);
    htmlStr = htmlStr.replaceAll("Timg", "<img");
    htmlStr = htmlStr.replaceAll("TP1", "<p>");
    htmlStr = htmlStr.replaceAll("TP2", "</p>");
    htmlStr = htmlStr.replaceFirst("<p[^p]*p>", "");
    return htmlStr;
  }

  public static String htnlHandSooToo(String html) {
    String htmlStr;
    htmlStr = replacep(html);
    int i = htmlStr.indexOf("<noscript>");
    if (i > 1) {
      htmlStr = htmlStr.substring(0, i);
    }
    htmlStr = htmlStr.replaceAll("<img", "Timg");
    htmlStr = htmlStr.replaceAll("<p>", "TP1");
    htmlStr = htmlStr.replaceAll("</p>", "TP2");
    htmlStr = replaceAll(htmlStr);
    htmlStr = htmlStr.replaceAll("Timg", "<img");
    htmlStr = htmlStr.replaceAll("TP1", "<p>");
    htmlStr = htmlStr.replaceAll("TP2", "</p>");
    return htmlStr;
  }

}
