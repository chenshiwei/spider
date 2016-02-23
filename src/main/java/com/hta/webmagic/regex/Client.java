package com.hta.webmagic.regex;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient; //httpclient
import org.apache.http.client.methods.HttpGet; //httpclient
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.params.HttpClientParams; //httpclient
import org.apache.http.impl.client.DefaultHttpClient; //httpclient

public class Client {
	private static String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322; InfoPath.1; .NET CLR 2.0.50727)";

	public static String HttpClient(String url, String charsetr) {
		String doc = "";
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, true);
		HttpProtocolParams.setUserAgent(params, userAgent);
		HttpClient httpClient = new DefaultHttpClient(params);

		try {
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				httpGet.abort();
				System.out.println("httpClient **error01");
			}

			HttpEntity entity = response.getEntity();

			System.out.println("chart set i null");
			doc = EntityUtils.toString(entity, charsetr);

		} catch (Exception ee) {
			System.out.println("httpClient" + ee);
			ee.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return doc;
	}

	@SuppressWarnings("deprecation")
	public static String HttpClient(String url) {

		String doc = "";
		String charset;
		HttpEntity entity = null;
		@SuppressWarnings("deprecation")
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, true);
		HttpProtocolParams.setUserAgent(params, userAgent);
		HttpClient httpClient = new DefaultHttpClient(params);
		try {

			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			// + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {

				httpGet.abort();
				// System.out.println("httpClient **error01");
			}

			// Header[] headers = response.getAllHeaders();
			//
			// for (int i = 0; i < headers.length; i++) {
			// System.out.println(headers[i]);
			// }
			entity = response.getEntity();
			charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				System.out.println("chart set i null");
				String sdoc = EntityUtils.toString(entity, "utf-8"); // ����ʶ����
				charset = parserCharset.regexCharset(sdoc);
				doc = HttpClient(url, charset);
				// doc = EntityUtils.toString(entity, charset);
			}
			if (charset != null) {
				doc = EntityUtils.toString(entity, charset); // ����ʶ����
				EntityUtils.consume(entity);
			} else {
				System.out.println("chart set i null");
				doc = EntityUtils.toString(entity, "utf-8");
			}
		} catch (Exception ee) {
			System.out.println("httpClient  error 7" + ee);
			ee.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return doc;
	}

}
