package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.netty.handler.codec.http.HttpObject;
import org.example.entity.Account;
import org.example.entity.RespEmptyResult;
import org.example.entity.RespErrorCode;
import org.example.entity.RespResult;
import org.example.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("执行了拦截器的preHandle方法");
        if (RequestUriUtils.tokenIsVail(request)) {
            return  true;
        } else {
            String token = request.getHeader("token");
            if (!token.isEmpty()) { return true; }
            RespEmptyResult result = new RespEmptyResult();
            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
            result.setStatus(RespErrorCode.ERROR.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponseJson = objectMapper.writeValueAsString(result);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(errorResponseJson);
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

}
