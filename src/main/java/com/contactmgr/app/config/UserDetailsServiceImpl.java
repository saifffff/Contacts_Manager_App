package com.contactmgr.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contactmgr.app.dao.UserRepository;
import com.contactmgr.app.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepository userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetch user from db and return UserDetails
		User user = userDao.getUserByEmail(username);
		if(user == null) {
			throw new UsernameNotFoundException("Username not found");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		
		return customUserDetails;
	}

}
