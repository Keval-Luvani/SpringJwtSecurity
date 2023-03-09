package com.keval.SpringSecurity.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.keval.SpringSecurity.helper.JwtUtil;
import com.keval.SpringSecurity.service.UserDetailService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	
	@Autowired
	private UserDetailService userDetailService; 
 	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authorizationToken = request.getHeader("Authorization");
		System.out.println(authorizationToken);
		String username = null;
		String jwtToken = null;
		
		if(authorizationToken!=null && authorizationToken.startsWith("Bearer ")) {
			jwtToken = authorizationToken.substring(7);
			System.out.print(jwtToken);
			try { 
				username = this.jwtUtil.extractUsername(jwtToken);
				System.out.print(username);
			}catch (Exception e) {
				e.printStackTrace();
			}
			UserDetails userDetails = this.userDetailService.loadUserByUsername(username);
			
			if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {	
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}else {
				System.out.println("token is not valid");
			}
		}
		System.out.println("next filter"+filterChain.getClass());
		filterChain.doFilter(request, response);
	}
}
