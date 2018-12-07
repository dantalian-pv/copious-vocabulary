package ru.dantalian.copvoc.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import ru.dantalian.copvoc.persist.api.PersistPrincipalManager;
import ru.dantalian.copvoc.web.security.DbUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PersistPrincipalManager principalManager;

  @Override
  protected void configure(final HttpSecurity aHttp) throws Exception {
    aHttp.authorizeRequests()
    				.antMatchers("/semantic/**", "/css/**", "/js/**").permitAll()
            .antMatchers("/").permitAll()
            .anyRequest().authenticated()
            .and()
        .formLogin()
            .loginPage("/page/login")
            .permitAll()
            .and()
        .logout()
            .permitAll();
  }

  @Bean
  @Override
  public UserDetailsService userDetailsService() {
    return new DbUserDetailsService(principalManager);
  }

}
