package org.example.config;


import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;


import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter(filterName = "HttpServletRequestFilter", urlPatterns = "/")
@Order(99)
public class HttpServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String contentType = request.getContentType();
        String method = "multipart/form-data";

        if (contentType != null && contentType.contains(method)) {
            // 将转化后的 request 放入过滤链中
            request = new StandardServletMultipartResolver().resolveMultipart(request);
        }
        request = new RequestWrapper((HttpServletRequest) servletRequest);
        //获取请求中的流如何，将取出来的字符串，再次转换成流，然后把它放入到新request对象中
        // 在chain.doFiler方法中传递新的request对象
        if(null == request) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(request, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

}
