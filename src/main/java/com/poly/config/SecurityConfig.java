package com.poly.config;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.poly.bean.Users;
import com.poly.service.UsersService;
import com.poly.util.SessionService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	BCryptPasswordEncoder pe;

	@Autowired
	UsersService userService;

	@Autowired
	SessionService session;

	// Password encryption mechanism
	@Bean
	public BCryptPasswordEncoder getpaBCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// Provide login data
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(
				username -> {
				try {
					Users user = userService.findById(username);
					//user k ton tai
					if(user == null) {
						session.setAttribute("checkUser", false);
					}
					//tk bi khoa vinh vien
					if(user.isActive() == false && user.getCreate_block() != null) {
						session.setAttribute("usermail", user.getEmail());
						session.setAttribute("checkActive", false);
						session.setAttribute("BlockAcc", false);
						session.setAttribute("checkPass", true);
						session.setAttribute("checkUser", true);
						session.setAttribute("permanentlyLocked", true);
						throw new DisabledException("Tài khoản bị khóa");
					}
					//tk ch kich hoat
					if(user.isActive() == false && user.getCreate_block() == null) {
						session.setAttribute("usermail", user.getEmail());
						session.setAttribute("permanentlyLocked", false);
						session.setAttribute("checkActive", false);
						session.setAttribute("BlockAcc", false);
						session.setAttribute("checkPass", true);
						session.setAttribute("checkUser", true);
						throw new DisabledException("Tài khoản chưa kích hoạt");
					}
					//ktr fail login
					if(user.getFail_login() == 5) {
						session.setAttribute("checkUser", true);
						session.setAttribute("BlockAcc", true);
						session.setAttribute("checkPass", true);
						session.setAttribute("permanentlyLocked", false);
						throw new DisabledException("Tài khoản bị khoá vui lòng chọn QMK");
					}
					//saimk
					session.setAttribute("BlockAcc", false);
					session.setAttribute("checkActive", true);
					session.setAttribute("checkUser", true);
					session.setAttribute("checkPass", false);
					session.setAttribute("permanentlyLocked", false);
					session.setAttribute("usermail", user.getEmail());
					String[] roles = user.getAuth().stream().map(ro -> ro.getRoles().getRoles_id())
									.collect(Collectors.toList()).toArray(new String[0]);
					
					Map<String, Object> authentication = new HashMap<>();
					
					byte[] token = (username + ":" + user.getPasswords()).getBytes();
					authentication.put("user", user);
					authentication.put("token", "Basic " + Base64.getEncoder().encodeToString(token));
					//Lưu tài khoản vào session
					session.setAttribute("user", user);
					session.setAttribute("authentication", authentication);
					//Lưu tài khoản vào session
					return User.withUsername(username).password(user.getPasswords()).roles(roles).build();
				} catch (Exception e) {
//					session.setAttribute("checkUser", false);
					throw new UsernameNotFoundException(username + " Not Found!!! 404");
				}
		});
	}

	// Cho phép truy cập restfull từ tên miền khác
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

	// Authorization of use
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();

		http.authorizeRequests()
		.antMatchers("/home/post", "/home/post-update", "/post/**", "/user/**", "/home/manager/**", "/find-by-post-likes", "/share-facebook")
		.authenticated()
		.antMatchers("/admin/**").hasAnyRole("admin")
		.antMatchers("/rest/authorities").hasRole("user")
		.anyRequest().permitAll();
		//.antMatchers("/rest/authorities", "/home/**", "/home/manager/**", "/doimk", "/home/post").hasRole("user")
		http.formLogin().loginPage("/login")
		.loginProcessingUrl("/login/action-test")
		.defaultSuccessUrl("/login/action/success", false)
		.failureUrl("/login/action/error");
		
		http.rememberMe().tokenValiditySeconds(86400);

		http.exceptionHandling().accessDeniedPage("/home/error");

// 		OAuth2- Đăng nhâp từ mang xã hôi
		http.oauth2Login().loginPage("/login")
			.defaultSuccessUrl("/oauth2/login/success", true)
			.failureUrl("/auth/login/error")
			.authorizationEndpoint()
			.baseUri("/oauth2/authorization");
		
		http.logout().logoutUrl("/logout").logoutSuccessUrl("/logout/success");
	}
}
