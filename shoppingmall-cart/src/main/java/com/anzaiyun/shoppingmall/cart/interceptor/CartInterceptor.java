package com.anzaiyun.shoppingmall.cart.interceptor;

import com.anzaiyun.common.constant.CartConstant;
import com.anzaiyun.common.vo.UserInfoVo;
import com.anzaiyun.shoppingmall.cart.vo.UserStatus;
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

    public static ThreadLocal<UserStatus> threadLocal = new ThreadLocal<>();

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
        UserInfoVo userInfoVo = (UserInfoVo) session.getAttribute("userInfo");
        UserStatus userStatus = new UserStatus();
        if (userInfoVo != null){
            //当前用户已登录
            userStatus.setUserId(userInfoVo.getId());
        }

        Cookie[] cookies = request.getCookies();
        if (cookies!=null &&cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    //获取到临时用户的用户id
                    userStatus.setUserKey(cookie.getValue());
                    //当前用户id不是新创建出来的
                    userStatus.setCreateTmpUser(false);
                }
            }
        }

        //如果没有临时用户则需要创建一个临时用户
        if(StringUtils.isEmpty(userStatus.getUserKey())){
            String userKey = UUID.randomUUID().toString();
            userStatus.setUserKey(userKey);
            userStatus.setCreateTmpUser(true);

        }
        threadLocal.set(userStatus);

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
        //如果是新创建的临时用户，需要配置临时用户的作用域和存活时间
        UserStatus userStatus = threadLocal.get();
        if (userStatus.getCreateTmpUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userStatus.getUserKey());
            cookie.setDomain("shoppingmall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }

    }
}
