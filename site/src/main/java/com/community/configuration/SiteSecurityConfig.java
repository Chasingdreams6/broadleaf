/*-
 * #%L
 * Community Demo Site
 * %%
 * Copyright (C) 2009 - 2022 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package com.community.configuration;

import org.broadleafcommerce.common.security.BroadleafAuthenticationFailureHandler;
import org.broadleafcommerce.common.security.handler.SecurityFilter;
import org.broadleafcommerce.core.web.order.security.BroadleafAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.Filter;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@EnableWebSecurity
@ComponentScan({"org.broadleafcommerce.common.web.security","org.broadleafcommerce.profile.web.core.security","org.broadleafcommerce.core.web.order.security"})
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SiteSecurityConfig extends WebSecurityConfigurerAdapter {

    @Configuration
    public static class DependencyConfiguration {

        @Bean
        protected AuthenticationFailureHandler blAuthenticationFailureHandler(@Qualifier("blAuthenticationFailureRedirectStrategy") RedirectStrategy redirectStrategy) {
            BroadleafAuthenticationFailureHandler response = new BroadleafAuthenticationFailureHandler("/login?error=true");
            response.setRedirectStrategy(redirectStrategy);
            return response;
        }

        @Bean
        protected AuthenticationSuccessHandler blAuthenticationSuccessHandler(@Qualifier("blAuthenticationSuccessRedirectStrategy") RedirectStrategy redirectStrategy) {
            BroadleafAuthenticationSuccessHandler handler = new BroadleafAuthenticationSuccessHandler();
            handler.setRedirectStrategy(redirectStrategy);
            handler.setDefaultTargetUrl("/");
            handler.setTargetUrlParameter("successUrl");
            handler.setAlwaysUseDefaultTargetUrl(false);
            return handler;
        }

        @Bean
        protected Filter blCsrfFilter() {
            SecurityFilter securityFilter = new SecurityFilter();
            List<String> excludedRequestPatterns = new ArrayList<>();
            excludedRequestPatterns.add("/sample-checkout/**");
            excludedRequestPatterns.add("/hosted/sample-checkout/**");
            securityFilter.setExcludedRequestPatterns(excludedRequestPatterns);
            return securityFilter;
        }

    }

    @Value("${asset.server.url.prefix.internal}")
    protected String assetServerUrlPrefixInternal;

    @Resource(name="blAuthenticationSuccessHandler")
    protected AuthenticationSuccessHandler successHandler;

    @Resource(name="blAuthenticationFailureHandler")
    protected AuthenticationFailureHandler failureHandler;

    @Resource(name="blCsrfFilter")
    protected Filter securityFilter;

    @Resource(name="blUserDetailsService")
    protected UserDetailsService userDetailsService;

    @Resource(name="blPasswordEncoder")
    protected PasswordEncoder passwordEncoder;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers("/css/**")
                .antMatchers("/fonts/**")
                .antMatchers("/img/**")
                .antMatchers("/js/**")
                .antMatchers("/**/"+assetServerUrlPrefixInternal+"/**")
                .antMatchers("/favicon.ico");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean(name="blAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .headers().frameOptions().disable().and()
            .sessionManagement()
                .sessionFixation()
                .migrateSession()
                .enableSessionUrlRewriting(false)
                .and()
            .formLogin()
                .permitAll()
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .loginPage("/login")
                .loginProcessingUrl("/login_post.htm")
                .and()
            .authorizeRequests()
                .antMatchers("/account/wishlist/**", "/account/**")
                .access("isAuthenticated()")
                .and()
            .requiresChannel()
                .antMatchers("/","/**")
                .requiresSecure()
                .and()
            .logout()
                .invalidateHttpSession(true)
                .deleteCookies("ActiveID")
                .logoutUrl("/logout")
                .and()
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Don't allow the auto registration of the filter for the main request flow. This filter should be limited
     * to the spring security chain.
     *
     * @param filter the Filter instance to disable in the main flow
     * @return the registration bean that designates the filter as being disabled in the main flow
     */
    @Bean
    @DependsOn("blCacheManager")
    public FilterRegistrationBean blCsrfFilterFilterRegistrationBean(@Qualifier("blCsrfFilter") SecurityFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }


}
