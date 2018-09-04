package fr.idm.myWebsite;

import java.sql.SQLException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
     // @formatter:off
        http.authorizeRequests()
        .antMatchers("/", "/register","/XSS","/CSRF","/users").permitAll()
        .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
        .anyRequest().authenticated()
        .and().formLogin().loginPage("/login").permitAll()
        .and().logout().permitAll();
        http
        .headers()
          .addHeaderWriter(new StaticHeadersWriter("X-XSS-Protection","0"));
     // @formatter:oN
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsManager());
    }

    @Bean
    public SQLiteUserDetailsManager userDetailsManager() throws SQLException {
        final Properties users = new Properties();
        users.put("admin", "adminidm,ROLE_ADMIN,enabled");
        users.put("guest", "guest,ROLE_GUEST,enabled");
        return new SQLiteUserDetailsManager(users);
    }
}
