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
	public String goHome(Model model) {
		System.out.println("go home ..");
		User myuser = new User();
		myuser.setName("shakure");
		myuser.setEmail("lalthakur@gmail.com");
		try {
			userDao.save(myuser);
			model.addAttribute("myuser", myuser);
			System.out.println("user save successfully..");
			
		} catch (Exception e) {
			System.out.println("failed to save user..");
			e.printStackTrace();
		}
		return "index";
	}
}
