package com.contactmgr.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contactmgr.app.dao.UserRepository;
import com.contactmgr.app.entities.User;

import jakarta.validation.Valid;

@Controller
public class MainController {
	@Autowired
	private UserRepository userDao;
	
	
	@RequestMapping("/")
	public String goHome(Model model) {
		System.out.println("go home ..");
		model.addAttribute("title","Home - Smart Contacts Manager");
		return "index";
	}
	
	@RequestMapping("/about")
	public String goAbout(Model model) {
		System.out.println("go about ..");
		model.addAttribute("title","About - Smart Contacts Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String goSignup(Model model) {
		System.out.println("go sign up page...");
		User user = new User();
		model.addAttribute("userObj",user);
		model.addAttribute("title","Register - Smart Contacts Manager");
		return "signup";
	}
	
	@PostMapping("/registerUser")
	public String handleSignup(Model model,
			@Valid @ModelAttribute("userObj") User user, //It fetches the "userObj" from the model (created in goSignup)
			BindingResult result) {
		if(result.hasErrors()) {
			// stay on the page
			System.out.println("has error");
			System.out.println(result);
			return "signup";
		}
		System.out.println(user.getName());
		System.out.println(user.getPassword());
		System.out.println(user.getEmail());
		System.out.println(user.getGender());
		User sUser = userDao.save(user);
		System.out.println("saved user id : "+sUser.getId());
		return "login";
	}
	
	
	@RequestMapping("/login")
	public String goLogin(Model model) {
		System.out.println("go login page...");
		model.addAttribute("title","Login - Smart Contacts Manager");
		return "login";
	}
	
}
