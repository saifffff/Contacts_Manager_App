package com.contactmgr.app.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contactmgr.app.dao.ContactRepository;
import com.contactmgr.app.dao.UserRepository;
import com.contactmgr.app.entities.Contact;
import com.contactmgr.app.entities.User;

//bec it won't return view it would return json
@RestController  
public class searchController {
	@Autowired
	private ContactRepository contactDao;
	@Autowired
	private UserRepository userDao;
	
	//	search Handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		// fetch user 
		String uEmail = principal.getName();
		// using this email we can get user Object
		User cUser = userDao.getUserByEmail(uEmail);
		
		//search in db 
		List<Contact> searchResult = this.contactDao.searchContacts(query, cUser);
		
		return ResponseEntity.ok(searchResult);
	}
	
}
