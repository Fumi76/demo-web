package com.example.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

/*
 * https://www.baeldung.com/spring-http-logging
 * 
 * POSTのapplication/x-www-form-urlencodedしかサポートしていないようだ
 * 
 * 4----- REQUEST BODY START -----
 * ------WebKitFormBoundaryJ1uFC05BS0hPgMBk
 * Content-Disposition: form-data; name="file"; filename="a.csv"
 * Content-Type: application/octet-stream
 *
 * A1,A2,A3
 * A4,A5,A6
 * ------WebKitFormBoundaryJ1uFC05BS0hPgMBk--
 *
 * 4----- REQUEST BODY END   -----
 * application/octet-streamなのと
 * 最後に空行あり
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter implements Filter {
 
    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) throws IOException, ServletException {
  
    	System.out.println("POINT 2");
    	
        HttpServletRequest req = (HttpServletRequest) request;
        
        System.out.println(req.getContentLength());
        System.out.println(req.getContentType());
        
        ContentCachingRequestWrapper requestCacheWrapperObject
        	= new ContentCachingRequestWrapper(req);
        
//        System.out.println(requestCacheWrapperObject.getContentAsByteArray().length);// 0だった
        
		ServletInputStream sis;
		try {
			System.out.println("POINT 3");
	    	
	    	
			sis = requestCacheWrapperObject.getInputStream();
			byte[] buff = new byte[1024];
			int size = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while((size = sis.read(buff)) > 0) {
				baos.write(buff, 0, size);
			}
			sis.close();
			System.out.println("4----- REQUEST BODY START -----");
			System.out.println(new String(buff, "UTF-8"));
			System.out.println("4----- REQUEST BODY END   -----");
		} catch (IOException e) {
			e.printStackTrace();
		}

        chain.doFilter(requestCacheWrapperObject, response);
    }

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}