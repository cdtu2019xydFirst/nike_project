package com.yadong.nike.order.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/7/18 | 14:59
 **/
public class HttpclientUtil {
    public static String doGet(String url) {
        // 创 建 Httpclient 对 象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创 建 httpGET 请 求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // 执 行 请 求
            response = httpclient.execute(httpGet);
            // 判 断 返 回 状 态 是 否 为 200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                httpclient.close();
                return result;
            }
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String doPost(String url, Map<String, String> paramMap) {
        // 创 建 Httpclient 对 象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创 建 httpPost 请 求
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            if (paramMap != null){
                List<BasicNameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                HttpEntity httpEntity = new UrlEncodedFormEntity(list, "utf-8");
                httpPost.setEntity(httpEntity);
            }
            // 执 行 请 求
            response = httpclient.execute(httpPost);
            // 判 断 返 回 状 态 是 否 为 200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                httpclient.close();
                return result;
            }
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}

