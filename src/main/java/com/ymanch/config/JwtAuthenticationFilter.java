//package com.ymanch.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.ymanch.helper.JwtHelper;
//import com.ymanch.service.CustomJwtUserDetailServcie;
//
//import io.jsonwebtoken.io.IOException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//	@Autowired
//	private CustomJwtUserDetailServcie customJwtUserDetailServcie;
//
//	@Autowired
//	private JwtHelper jwtUtil;
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest arg0, HttpServletResponse arg1, FilterChain arg2)
//			throws ServletException, IOException, java.io.IOException {
//		// get jwt
//		// Bearer
//		// validate
//		String requestHeaderToken = arg0.getHeader("Authorization");
//		String username = null;
//		String jwtToken = null;
//		// null and format
//		if (requestHeaderToken != null && requestHeaderToken.startsWith("Bearer ")) {
//			// here we get token
//			jwtToken = requestHeaderToken.substring(7);
//			try {
//				// we get username from token
//				username = jwtUtil.extractUsername(jwtToken);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			// error here always rememeber
//			UserDetails userDetails = customJwtUserDetailServcie.loadUserByUsername(username);
//			// UserDetails userDetails1 =
//			// customAdminDetailsService.loadUserByUsername(username);
//
//			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//				// for user
//				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//						userDetails, null, userDetails.getAuthorities());
//				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(arg0));
//				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//
//			} else {
//				System.out.println("Token is not validated..");
//			}
//		}
//		doFilter(arg0, arg1, arg2);
//	}
//}

package com.ymanch.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ymanch.exception.AuthenticationException;
import com.ymanch.helper.JwtHelper;
import com.ymanch.service.CustomJwtUserDetailServcie;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
	private CustomJwtUserDetailServcie customJwtUserDetailServcie;

	@Autowired
	private JwtHelper jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException, AuthenticationException {
		// Get JWT
		String requestHeaderToken = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;

		if (requestHeaderToken != null && requestHeaderToken.startsWith("Bearer ")) {
			jwtToken = requestHeaderToken.substring(7);
			try {
				// Extract username from the token
				username = jwtUtil.extractUsername(jwtToken);
				request.setAttribute("userId", jwtUtil.extractUserId(jwtToken));
			} catch (ExpiredJwtException e) {
				throw new AuthenticationException("Token has expired", e);
			} catch (JwtException e) {
				throw new AuthenticationException("Invalid token", e);
			}

			// Validate token
			UserDetails userDetails = customJwtUserDetailServcie.loadUserByUsername(username);
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		chain.doFilter(request, response);
	}
}
