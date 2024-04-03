package com.contactmgr.app.controllers;

import java.util.Optional;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactmgr.app.dao.UserRepository;
import com.contactmgr.app.entities.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MainController {
	@Autowired
	private UserRepository userDao;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
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
			@RequestParam(value="agreement", defaultValue = "false") Boolean agree,
			@Valid @ModelAttribute("userObj") User user, //It fetches the "userObj" from the model (created in goSignup)
			BindingResult result
			) {
				if(result.hasErrors() || agree == false) {
					// stay on the page
					System.out.println("has error");
					// agreement unchecked
					if(agree == false) {
						// Create a custom FieldError for the agreement field
				        // (even though it's not directly bound to a model attribute)
				        FieldError error = new FieldError("userObj", "agreement", "Please agree to terms and conditions to proceed.");
				        // Add the error to the BindingResult
				        result.addError(error);
					}
					System.out.println(result);
					return "signup";
		}
		
		try {
			user.setRole("USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User sUser = userDao.save(user);
			System.out.println("saved user id : "+sUser.getId());
			model.addAttribute("msg","Registraion successful for : "+user.getEmail());
		} catch (Exception e) {
			// TODO: handle exception	
			model.addAttribute("msg","Failed to register : "+user.getEmail());
			return "signup";
		}
		return "signup";
	}
	
	
	@RequestMapping("/login")
	public String goLogin(Model model) {
		System.out.println("go login page...");
		model.addAttribute("title","Login - Smart Contacts Manager");
		return "login";
	}
	
}
