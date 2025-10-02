package com.example.mamolog.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.mamolog.entity.User;
import com.example.mamolog.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// Optional<User>型のため .orElseThrow() で「ユーザーがいなければ例外」を設定
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりませんでした。"));

		String userRoleName = "ROLE_" + user.getRole().getName();
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(userRoleName));

		return org.springframework.security.core.userdetails.User
	            .withUsername(user.getEmail())   // ログインID = email
	            .password(user.getPassword())    // DBから取ってきたハッシュ済パスワード
	            .authorities(authorities)
	            .accountExpired(false)
	            .accountLocked(false)
	            .credentialsExpired(false)
	            .disabled(!user.isEnabled())
	            .build();
	}

}
