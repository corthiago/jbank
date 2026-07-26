package com.thiago.jbank.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final IpFilter ipFilter;

    public FilterConfig(IpFilter ipfilter){
        this.ipFilter = ipfilter;
    }

    @Bean
    public FilterRegistrationBean<IpFilter> filterFilterRegistrationBean(){
        var registrationBean = new FilterRegistrationBean<IpFilter>();

        registrationBean.setFilter(ipFilter);
//        registrationBean.setOrder(0);
//        registrationBean.setUrlPatterns();

        return registrationBean;
    }
}
