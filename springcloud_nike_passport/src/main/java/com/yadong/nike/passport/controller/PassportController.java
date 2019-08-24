package com.yadong.nike.passport.controller;


import com.alibaba.fastjson.JSON;
import com.yadong.nike.bean.UmsMember;
import com.yadong.nike.passport.feign.UserFeignService;
import com.yadong.nike.passportConfig.util.HttpclientUtil;
import com.yadong.nike.passportConfig.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/7/17 | 18:55
 **/
@Controller
public class PassportController {

    @Autowired
    private UserFeignService userFeignService;

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(String ReturnUrl, ModelMap map) {
        map.put("ReturnUrl", ReturnUrl);
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)/*本站登录*/
    public String login(UmsMember umsMemberParam, HttpServletRequest request) {

        String token = "";
        /*调用用户服务验证用户名，密码*/
        UmsMember umsMemberLogin = userFeignService.login(umsMemberParam);
        if (umsMemberLogin != null) {
            /*登录成功 , 用jwt制作token ， 将token存入redis*/
            /*在web-util中有一个jwt加密算法，定义了一个map<String , Object>*/
            String memberId = umsMemberLogin.getId();
            String nickname = umsMemberLogin.getNickname();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("memberId", memberId);
            userMap.put("nickname", nickname);

            String ip = "127.0.0.1";
            /*在以后实际项目中需要按照预先设计的算法对其进行加密(JwtUtil.encode)，再生成token*/
            token = JwtUtil.encode("2019", userMap, ip);
            /*将token存入redis*/
            /*userFeignService.addUserToken(token, memberId);*/
        } else {
            /*登录失败 , 前端判断token*/
            token = "false";
        }
        return "redirect:http://localhost:9004/cartList?token="+token;
    }

    /*验证token*/
    @RequestMapping(value = "verify", method = RequestMethod.GET)
    @ResponseBody
    public String verify(@RequestParam("token") String token, @RequestParam("currentIp") String currentIp, HttpServletRequest request) {
        /*通过jwt校验token真假 ,解析*/
        Map<String, String> map = new HashMap<>();
        /*对token进行解析(JwtUtil.decode)*/
        /*=========================================================================================================================================*/
                     /*这里有一个大的bug不能从这里的request域中获取原始ip，currentIp（原始ip） ，request.getRemoteAddr(当前获取的ip是
                                     HttpclientUtil.doGet("http://localhost:8085/verify?token="发送的)*/
        /*=========================================================================================================================================*/
        System.out.println(request.getRemoteAddr());

        Map<String, Object> decode = JwtUtil.decode(token, "2019", currentIp);
        if (decode != null) {
            map.put("status", "success");
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));
            return JSON.toJSONString(map);
        } else {
            map.put("status", "fail");
            return JSON.toJSONString(map);
        }
    }

    /*@RequestMapping("vlogin")*//*社交登录*//*
    public String vlogin(String code, HttpServletRequest request) {
        *//*获得授权码，换取access_token*//*
        String URL1 = "https://api.weibo.com/oauth2/access_token";
        String URL2 = "https://api.weibo.com/2/users/show.json";
        String CLIENT_ID = "3702255538";
        String CLIENT_SECRET = "203c233ca28be441bdaf3fabfb82eedb";
        String GRANT_TYPE = "authorization_code";
        String REDIRECT_URI = "http://passport.gmall.com:8012/vlogin";
        String CODE = code;
        String s3 = URL1 + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&grant_type=" + GRANT_TYPE + "&redirect_uri=" + REDIRECT_URI + "&code=" + CODE;
        String access_token_json = HttpclientUtil.doPost(s3, null);
        Map<String, Object> access_map = JSON.parseObject(access_token_json, Map.class);
        String ACCESS_TOKEN = (String) access_map.get("access_token");
        String UID = (String) access_map.get("uid");
        *//*access_token换取用户信息*//*
        String s4 = URL2 + "?access_token=" + ACCESS_TOKEN + "&uid=" + UID;
        String user_json = HttpclientUtil.doGet(s4);
        Map<String, Object> user_map = JSON.parseObject(user_json, Map.class);
        System.out.println(user_map);
        *//*将用户信息保存到数据库，用户类型设置为微博用户*//*
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType("2");
        umsMember.setSourceUid(UID);
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(ACCESS_TOKEN);
        if (user_map != null) {
            umsMember.setNickname((String) user_map.get("screen_name"));
            umsMember.setCity((String) user_map.get("location"));
            String gender = (String) user_map.get("gender");
            if (gender != null) {
                if (gender.equals("m")) {
                    umsMember.setGender("1");
                } else {
                    umsMember.setGender("0");
                }
            }
        }
        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        UmsMember checkOauthUser = userFeignService.checkOauthUser(umsCheck);
        if (checkOauthUser == null) {*//*之前没有登陆过*//*
            *//*===========================================================================================================================================*//*
                                    *//* 主键返回不支持远程RPC协议 ，保存用户信息 , mybatis的主键返回策略不能跨RPC协议使用，当我们远程调用
                                                    userService.addOauthUser(umsMember) 用该把新增的用户返回到这里*//*
            *//*===========================================================================================================================================*//*
            umsMember = userFeignService.addOauthUser(umsMember);
        } else {
            umsMember = checkOauthUser;
        }
        *//*生成jwt的token，并且从定向到首页，携带token*//*
        String token = "";
        *//*调用用户服务验证用户名，密码*//*
        UmsMember umsMemberLogin = userFeignService.login(umsMember);
        if (umsMemberLogin != null) {
            *//*登录成功 , 用jwt制作token ， 将token存入redis*//*
            *//*在web-util中有一个jwt加密算法，定义了一个map<String , Object>*//*
            String memberId = umsMemberLogin.getId();
            String nickname = umsMemberLogin.getNickname();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("memberId", memberId);
            userMap.put("nickname", nickname);

            String ip = "127.0.0.1";
            *//*在以后实际项目中需要按照预先设计的算法对其进行加密(JwtUtil.encode)，再生成token*//*
            token = JwtUtil.encode("2019", userMap, ip);
            *//*将token存入redis*//*
            userFeignService.addUserToken(token, memberId);
            return "redirect:http://myzuul.com:9527/item/120.html?token="+token;
        } else {
            *//*登录失败 , 前端判断token*//*
            return "fail";
        }
    }*/
}
