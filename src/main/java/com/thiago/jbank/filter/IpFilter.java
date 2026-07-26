package com.thiago.jbank.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IpFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        var ipAddress = request.getRemoteAddr();

        // setting attribute to both requests and responses in this case
        request.setAttribute("x-user-ip", ipAddress);
        response.setHeader("x-user-ip", ipAddress);

        chain.doFilter(request, response);
    }
}
