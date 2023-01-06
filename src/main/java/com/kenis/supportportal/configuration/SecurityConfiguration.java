package com.kenis.supportportal.configuration;

import com.kenis.supportportal.constant.SecurityConstant;
import com.kenis.supportportal.filter.JwtAccessDeniedHandler;
import com.kenis.supportportal.filter.JwtAuthenticationEntryPoint;
import com.kenis.supportportal.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private JwtAuthorizationFilter jwtAuthorizationFilter;
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private UserDetailsService userDetailsService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public SecurityConfiguration (JwtAuthorizationFilter jwtAuthorizationFilter,
                                  JwtAccessDeniedHandler jwtAccessDeniedHandler,
                                  JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                  @Qualifier("UserDetailsService") UserDetailsService userDetailsService,
                                  BCryptPasswordEncoder bCryptPasswordEncoder
                                  ) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // disabling Cross Site Request Forgery bq using the application as backend
                .cors() // adding Cross-origin resource sharing to restricted resources on a web page to be requested from another domain
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// this the application will never create an HttpSession, and it will never use it to obtain the SecurityContext
                .and()
                .authorizeRequests().antMatchers(SecurityConstant.PUBLIC_URLS).permitAll()// specifier the public url that can be accessed with authentication
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
