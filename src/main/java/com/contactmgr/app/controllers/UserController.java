package com.contactmgr.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

	@RequestMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("title","Dashboard - Smart Contacts Manager");
		return "normal/userDashboard";
	}
	
	@RequestMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("title","Profile - Smart Contacts Manager");
		return "normal/userProfile";
	}
	
}
