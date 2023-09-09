package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.netty.handler.codec.http.HttpObject;
import org.example.entity.Account;
import org.example.mapper.AccountMapper;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("执行了拦截器的preHandle方法");
        boolean isVaild = getHeaderToken(request);
        if (isVaild) {
            return  true;
        } else {
            response.sendRedirect("/api/tokenInvail");
            return  false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("执行了拦截器的postHandle方法");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("执行了拦截器的afterCompletion方法");
    }

//    @Resource
//    AccountMapper accountMapper;

//    private Account tokenIsVaild(String token) {
//        QueryWrapper<Account> query = new QueryWrapper<Account>();
//        query.eq("token", token);
//        query.select("id", "nickname"); // 指定要返回的字段
//        query.last("limit 1");
//        return accountMapper.selectOne(query);
//    }

    private boolean getHeaderToken(HttpServletRequest httpServletRequest){
        {
            Account account = (Account) httpServletRequest.getSession().getAttribute("user");
            boolean isVail = account != null;
            String content = isVail ? "已登录" : "未登录";
            System.out.println(content);
            return  isVail;
        }

    }
}
