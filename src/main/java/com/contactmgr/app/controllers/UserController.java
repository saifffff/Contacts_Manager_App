package com.contactmgr.app.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contactmgr.app.dao.UserRepository;
import com.contactmgr.app.entities.Contact;
import com.contactmgr.app.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userDao;
	
	@ModelAttribute
	public void commonTask(Model model, Principal principal) {
		// getting currently authenticated user 
				String uEmail = principal.getName();
				// using this email we can get user Object
				User cUser = userDao.getUserByEmail(uEmail);
				model.addAttribute("cUser",cUser);
	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title","Dashboard - Smart Contacts Manager");
		return "normal/userDashboard";
	}
	
	@RequestMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("title","Profile - Smart Contacts Manager");
		return "normal/userProfile";
	}
	
	@RequestMapping("/add-contact")
	public String addContact(Model model) {
		model.addAttribute("title","Add Contact - Smart Contacts Manager");
		model.addAttribute("contact", new Contact());
		return "normal/add-contact";
	}
	
}
