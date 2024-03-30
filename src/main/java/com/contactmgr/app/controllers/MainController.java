package com.contactmgr.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contactmgr.app.dao.UserRepository;
import com.contactmgr.app.entities.User;

@Controller
public class MainController {
	@Autowired
	private UserRepository userDao;
	
	
	@RequestMapping("/")
	public String goHome() {
		System.out.println("go home ..");
		
		return "index";
	}
	
	@RequestMapping("/about")
	public String goAbout() {
		System.out.println("go about ..");
		
		return "about";
	}
}
