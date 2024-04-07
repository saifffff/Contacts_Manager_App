package com.contactmgr.app.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmgr.app.dao.ContactRepository;
import com.contactmgr.app.dao.UserRepository;
import com.contactmgr.app.entities.Contact;
import com.contactmgr.app.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userDao;
	@Autowired
	private ContactRepository contactDao;

	
	
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
	
	@PostMapping("/process-contact")
	public String contactHandler(@ModelAttribute Contact contact,
			Principal principal,
			Model model,
			@RequestParam("profileImage") MultipartFile image
			) {
		/* we can save contact directly to the data base but we will require ContactRepository,
		 * for now we will use user repository instead, so we need user instance
		 */
		User cUser = this.userDao.getUserByEmail(principal.getName());
		cUser.getContacts().add(contact);
		contact.setUser(cUser);
		
		try {
			
			if(!image.isEmpty()) {
				/* we have to save this image in static/images, so first we need to fetch path
				 * we will use ClassPathResource
				 */
				File res  = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(res.getAbsolutePath()+File.separator+image.getOriginalFilename());
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImageURL(image.getOriginalFilename());
				System.out.println("image uploaded");
			}
			
			User sUser = this.userDao.save(cUser);
			System.out.println("saved : "+ contact.getName()+" for user : "+sUser.getName());
			model.addAttribute("msg","Contact Added Successfully");
		} catch (Exception e) {
			model.addAttribute("err",e.getMessage());
			e.printStackTrace();
		}
		
		
		return "normal/add-contact";
	}
	
	@RequestMapping("/show-contacts")
	public String viewContacts(Principal principal, Model model) {
		// fetch user 
		User cUser = this.userDao.getUserByEmail(principal.getName());
		// fetch contact using contactDao
		List<Contact> allContactByUser = this.contactDao.allContactByUser(cUser.getId());
		// add to model
		model.addAttribute("userContacts",allContactByUser);
		return "normal/show-contacts";
	}
	
	@RequestMapping("/update-contact")
	public String updateContact(@RequestParam("contactId")int contactId,
			Model model
			) {
		try {
			Optional<Contact> opcontact = this.contactDao.findById(contactId);
			Contact contact = opcontact.get();
			contact.setCid(contactId);
			model.addAttribute("myContact",contact);
			
			//System.out.println(contact);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "normal/update-contact";
	}
	
	
	@PostMapping("/update-contact")
	public String updateHandler(@ModelAttribute Contact con,
			@RequestParam("contactId") int contactId,
			@RequestParam("profileImage") MultipartFile image
			) {
		// now we have contact id to be updated lets fetch
		Optional<Contact> opcontact = this.contactDao.findById(contactId);
		Contact contact = opcontact.get();
		
		// transfer data from con to contact
		contact.setCid(contactId);
		contact.setName(con.getName());
		contact.setNickName(con.getNickName());
		contact.setWork(con.getWork());
		contact.setEmail(con.getEmail());
		contact.setAbout(con.getAbout());
		contact.setPhone(con.getPhone());
		
		try {
			if(!image.isEmpty()) {
				/* we have to save this image in static/images, so first we need to fetch path
				 * we will use ClassPathResource
				 */
				File res  = new ClassPathResource("static/images").getFile();
				//delete old image 
				Files.deleteIfExists(Paths.get(res.getAbsolutePath()+File.separator+contact.getImageURL()));
				//add new image
				Path path = Paths.get(res.getAbsolutePath()+File.separator+image.getOriginalFilename());
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImageURL(image.getOriginalFilename());
				System.out.println("image replaced");
			}
			
			this.contactDao.save(contact);
			System.out.println("update success");
			
		} catch (Exception e) {
			System.out.println("image replacement failed");
			e.printStackTrace();
		}
		
		
		
		return "normal/show-contacts";
	}
	
}
