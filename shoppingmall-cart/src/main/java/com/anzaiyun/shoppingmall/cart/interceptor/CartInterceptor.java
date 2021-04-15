package com.anzaiyun.shoppingmall.cart.interceptor;

import com.anzaiyun.common.constant.CartConstant;
import com.anzaiyun.common.vo.UserLoginVo;
import com.anzaiyun.shoppingmall.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 在执行目标方法之前，判断用户的登录状态，是否临时用户，用户信息是什么
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 业务执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        UserLoginVo loginUser = (UserLoginVo) session.getAttribute("userInfo");
        UserInfoTo userInfoTo = new UserInfoTo();
        if (loginUser != null){
            userInfoTo.setUserId(1L);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies!=null &&cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());
                }
            }
        }

        //如果没有临时用户则需要创建一个临时用户
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String userKey = UUID.randomUUID().toString();
            userInfoTo.setUserKey(userKey);

        }
        threadLocal.set(userInfoTo);

        return true;
    }

    /**
     * 业务执行之后
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, threadLocal.get().getUserKey());
        cookie.setDomain("shoppingmall.com");
        cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
        response.addCookie(cookie);
    }
}
