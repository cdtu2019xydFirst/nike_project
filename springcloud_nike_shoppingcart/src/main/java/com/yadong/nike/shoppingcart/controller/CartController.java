package com.yadong.nike.shoppingcart.controller;

import com.alibaba.fastjson.JSON;
import com.yadong.nike.bean.OmsCartItem;
import com.yadong.nike.bean.PmsSkuInfo;
import com.yadong.nike.passportConfig.annotations.LoginRequired;
import com.yadong.nike.passportConfig.util.CookieUtil;
import com.yadong.nike.shoppingcart.feign.CartFeignService;
import com.yadong.nike.shoppingcart.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 19:52
 **/
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartFeignService cartFeignService;

    @RequestMapping("/index")
    @LoginRequired(loginSuccess = false)
    public String index(){
        return "index";
    }

    @RequestMapping("/cartList")
    @LoginRequired(loginSuccess = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        /*判断用户是否登录*/
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        if (StringUtils.isNotBlank(memberId)) {
            /*已经登录，查询db ，调用cartList方法*/
            /*将用户没有登录时，加入到购物车的数据其实是临时存放在Cookie中，所以我们需要同步购物车*/
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            System.out.println(cartListCookie);
            /*=======================================================================================================================================*/
            /*同步购物车以后一定要把在Cookie中同步的数据删除，不然每次到这里都会从Cookie同步数据*/
            /*=======================================================================================================================================*/
            CookieUtil.deleteCookie(request, response, "cartListCookie");
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                for (OmsCartItem omsCartItem : omsCartItems) {
                    OmsCartItem omsCartItemFromDb = cartService.ifCartsExistByUser(memberId, omsCartItem.getProductSkuId());
                    if (omsCartItemFromDb == null) {
                        /*该用户没有添加过当前商品*/
                        omsCartItem.setMemberId(memberId);
                        omsCartItem.setMemberNickname(nickname);
                        cartService.addCart(omsCartItem);
                    } else {
                        /*该用户有相同商品，改变数量即可*/
                        if (omsCartItem.getQuantity() != null){
                            cartService.updataCartItemQuantity(omsCartItemFromDb, omsCartItem.getQuantity());
                        }
                    }
                }
                /*同步缓存*/
                /*cartService.flushCartCatch(memberId);*/
            }
            /*同步数据库，同步缓存后再次查询*/
            omsCartItems = cartService.cartList(memberId);
        } else {
            /*没有登录，查询cookie*/
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }

        modelMap.put("cartList", omsCartItems);
        modelMap.put("nickname", nickname);
        /*购物车被勾选的商品总价totalAmount*/
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount", totalAmount);
        return "cartList";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getQuantity() != null){
                BigDecimal quantity = omsCartItem.getQuantity();
                String price = omsCartItem.getPrice();
                Long aLong = Long.valueOf(price);
                BigDecimal bigDecimal = BigDecimal.valueOf(aLong);
                totalAmount = totalAmount.add(quantity.multiply(bigDecimal));
            }
        }
        return totalAmount;
    }

    @RequestMapping(value = "/addToCart", method = RequestMethod.POST)
    @LoginRequired(loginSuccess = false)
    public String addToCart(String skuId, BigDecimal quantity, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        /*调用商品服务查询商品信息*/
        PmsSkuInfo skuInfo = cartFeignService.getSkuById(skuId);
        /*将商品信息封装成购物车信息*/
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setPrice(skuInfo.getPrice().toString());
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(quantity);
        omsCartItem.setProductSn(skuInfo.getSkuDesc());
        /*判断用户是否登录*/
        /*在拦截器Authinterceptor中将token中携带的用户信息已经写入request域中了*/
        String memberId = (String) request.getAttribute("memberId");
        String nickName = (String) request.getAttribute("nickname");
        if (StringUtils.isBlank(memberId)) {
            /*用户没有登录 ,更新Cookie数据*/
            /*cookie里原有的购物车数据*/
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            /*判断cookie是否为空*/
            if (StringUtils.isBlank(cartListCookie)) {
                omsCartItems.add(omsCartItem);
            } else {
                //cookie不为空
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                /*判断我们即将添加购物车数据在cookie中是否存在*/
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if (exist) {
                    //之前添加过，更新购物车的添加数量
                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            /*更新数量*/
                            if (omsCartItem.getQuantity() != null){
                                if (cartItem.getQuantity() == null){
                                    cartItem.setQuantity(BigDecimal.valueOf(0));
                                }
                                cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                            }
                        }
                    }
                } else {
                    //之前没有添加过 ， 新增这条购物车
                    omsCartItems.add(omsCartItem);
                }
            }
            /*存入Cookie*/
            CookieUtil.setCookie(request , response , "cartListCookie" , JSON.toJSONString(omsCartItems) , 60*60*72 , true);
        } else {
            /*用户已经登录，更新数据库*/
            /*根据用户id,从数据库db中查询用户购物车信息，方便后面判断是该添加，还是更新购物车*/
            OmsCartItem omsCartItemFromDb = cartService.ifCartsExistByUser(memberId, skuId);
            if (omsCartItemFromDb == null) {
                /*该用户没有添加过当前商品*/
                omsCartItem.setMemberId(memberId);
                omsCartItem.setMemberNickname(nickName);
                cartService.addCart(omsCartItem);
            } else {
                /*该用户添加过当前商品*/
                if (omsCartItem.getQuantity() != null){
                    cartService.updataCartItemQuantity(omsCartItemFromDb, omsCartItem.getQuantity());
                }
            }
            omsCartItems.add(omsCartItem);
            /*同步缓存*/
            /*cartService.flushCartCatch(memberId);*/
        }
        /*购物车集合里面该skuId的数量*/
        modelMap.put("skuInfo", skuInfo);
        for (OmsCartItem cartItem : omsCartItems) {
            if (skuInfo.getId().equals(cartItem.getProductSkuId())){
                modelMap.put("quantity", cartItem.getQuantity());
            }
        }
        return "success";
    }

    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean b = false;
        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();
            /*如果相等，说明即将添加到购物车的数据已经存在，不能覆盖，可以增加该商品的数量*/
            if (productSkuId.equals(omsCartItem.getProductSkuId())) {
                b = true;
            }
        }
        return b;
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String success() {
        return "success";
    }

    /*========================================================================================================*/
        /*其他微服务调用模块*/
    /*========================================================================================================*/

    @RequestMapping(value = "/springcloud/cartList/{memberId}", method = RequestMethod.GET)
    @ResponseBody
    public List<OmsCartItem> getCartList(@PathVariable("memberId") String memberId){
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
        return omsCartItems;
    }



}
