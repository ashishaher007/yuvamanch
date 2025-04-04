package com.ymanch.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ymanch.entity.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomJwtUserDetail implements UserDetails {

	private User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> simpleGrantedAuthority = new ArrayList<>();
		simpleGrantedAuthority.add(new SimpleGrantedAuthority(user.getUserRole()));

		return simpleGrantedAuthority;
	}

	@Override
	public String getPassword() {
		return user.getUserpassword();
	}

	@Override
	public String getUsername() {
		return user.getUserEmail();
	}

}
