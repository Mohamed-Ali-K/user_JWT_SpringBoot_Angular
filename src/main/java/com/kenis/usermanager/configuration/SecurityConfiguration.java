package com.kenis.usermanager.configuration;

import com.kenis.usermanager.constant.SecurityConstant;
import com.kenis.usermanager.filter.JwtAccessDeniedHandler;
import com.kenis.usermanager.filter.JwtAuthenticationEntryPoint;
import com.kenis.usermanager.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * This class represents the security configuration for the application.
 * It extends WebSecurityConfigurerAdapter and overrides its methods to provide
 * custom security configurations for the application.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Creates a new instance of the SecurityConfiguration class.
     *
     * @param jwtAuthorizationFilter The JWT authorization filter.
     * @param jwtAccessDeniedHandler The JWT access denied handler.
     * @param jwtAuthenticationEntryPoint The JWT authentication entry point.
     * @param userDetailsService The user details service.
     * @param bCryptPasswordEncoder The BCrypt password encoder.
     */
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


    /**
     * Configures the authentication manager.
     *
     * @param auth The authentication manager builder.
     * @throws Exception If an error occurs while configuring the authentication manager.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * Configures the HTTP security for the application.
     *
     * @param http The HTTP security configuration.
     * @throws Exception If an error occurs while configuring the HTTP security.
     */
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
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
