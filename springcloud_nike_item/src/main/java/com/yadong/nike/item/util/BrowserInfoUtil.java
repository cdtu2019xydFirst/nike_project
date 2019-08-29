package com.yadong.nike.item.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : 熊亚东
 * @description :获取操作系统，浏览器即版本信息
 * @date : 2019/8/15 | 11:30
 **/
public class BrowserInfoUtil {

    public static String getOsAndBrowserInfo(HttpServletRequest request){
        String browserDetails = request.getHeader("User-Agent");
        String userAgent = browserDetails;
        String user = userAgent.toLowerCase();

        String os = "";
        String browser = "";

        /*====================OSInfo====================*/
        if (userAgent.toLowerCase().indexOf("windows") >= 0){
            os = "Windows";
        }else if (userAgent.toLowerCase().indexOf("mac") >= 0){
            os = "Mac";
        }else if (userAgent.toLowerCase().indexOf("xll") >= 0){
            os = "Unix";
        }else if (userAgent.toLowerCase().indexOf("android") >= 0){
            os = "Android";
        }else if (userAgent.toLowerCase().indexOf("iphone") >= 0){
            os = "Iphone";
        }else {
            os = "Unknown, More-Info";
        }

        /*====================Browser====================*/
        if (user.contains("edge")){
            browser = (userAgent.substring(userAgent.indexOf("Edge")).split(" ")[0]).replace("/", "-");
        }else if (user.contains("msie")){
           String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(":")[0];
           browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        }else if (user.contains("safari") && user.contains("version")){
            browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-" +
                    (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        }else if (user.contains("opr") || user.contains("opera")){
            if (user.contains("opera")){
                browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-" +
                        (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            }else if (user.contains("opr")){
                browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]
                        .replace("/", "-").replace("OPR" , "Opera");
            }
        }else if (user.contains("chrome")){
            browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        }else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) ||
                  (user.indexOf("mozilla/4.7") != -1)|| (user.indexOf("mozilla/4.78") != -1)||
                  (user.indexOf("mozilla/4.08") !=-1)|| (user.indexOf("mozilla/3") != -1)){
            browser = "Netscape-?";
        }else if (user.contains("firefox")){
            browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        }else if (user.contains("rv")){
            String IEVersion = (userAgent.substring(userAgent.indexOf("rv")).split(" ")[0]).replace("rv", "-");
            browser = "IE" + IEVersion.substring(0, IEVersion.length() - 1);
        }else {
            browser = "Unknown, More-Info: " + userAgent;
        }
        return os + " --- " + browser;
    }

}
