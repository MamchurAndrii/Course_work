package com.yan.crm_project.security;

import com.yan.crm_project.constant.filter.AuthenticationFilter;
import com.yan.crm_project.constant.filter.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import static com.yan.crm_project.constant.ApplicationConstant.Role.*;
import static com.yan.crm_project.constant.ViewConstant.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppplicationSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSessionRequestCache httpSessionRequestCache;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var authenticationFilter = new AuthenticationFilter(authenticationManagerBean());
        authenticationFilter.setFilterProcessesUrl(API_VIEW + LOGIN_VIEW);
        http.csrf().disable().cors().disable().requestCache().requestCache(httpSessionRequestCache).and()
                .authorizeRequests()
                .antMatchers(API_VIEW + LOGIN_VIEW, API_VIEW + TOKEN_VIEW + REFRESH_VIEW, "/css/login.css").permitAll()
                .antMatchers(INDEX_VIEW, PROFILE_VIEW + FREE_VIEW, BLANK_VIEW, JOB_VIEW)
                .hasAnyRole(MEMBER, LEADER, ADMIN)
                .antMatchers(TASK_VIEW + FREE_VIEW, PROJECT_VIEW + FREE_VIEW, USER_VIEW + FREE_VIEW)
                .hasAnyRole(LEADER, ADMIN).antMatchers(ROLE_VIEW + FREE_VIEW).hasRole(ADMIN).anyRequest()
                .authenticated().and().formLogin().loginPage(LOGIN_VIEW).loginProcessingUrl(LOGIN_VIEW)
                .defaultSuccessUrl(INDEX_VIEW).failureUrl(LOGIN_VIEW + "?error=true").permitAll().and().logout()
                .invalidateHttpSession(true).clearAuthentication(true).permitAll().and().exceptionHandling()
                .accessDeniedPage(FORBIDDEN_VIEW).and().addFilter(authenticationFilter)
                .addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
