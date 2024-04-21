package com.contactmgr.app.controllers;

import java.io.File;
import java.net.http.HttpClient.Redirect;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

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
	@Autowired
	private BCryptPasswordEncoder passEncoder;

	
	
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
		model.addAttribute("title","Profile - Smart Contacts Manager");
		return "normal/userDashboard";
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
	
	@RequestMapping("/show-contacts/{page}")
	public String viewContacts(Principal principal,
			Model model,
			@PathVariable("page") Integer page
			) {
		// fetch user 
		User cUser = this.userDao.getUserByEmail(principal.getName());
		// we are using pagination to get subsets of contact list
//		we have page_index(page) in request which we are fetching using
//		path variable,  PageRequest implements pageabe and it takes two args
//		that is (current page, number of obj(contacts) per page
		Pageable pageable = PageRequest.of(page, 4);
		
		// now lets fetch pages from contact repository
		Page<Contact> allContacts = this.contactDao.allContactByUser(cUser.getId(), pageable);
		
		// add attributes to model for view
		model.addAttribute("userContacts",allContacts);
		model.addAttribute("currPage",page);
		model.addAttribute("totalPages",allContacts.getTotalPages());
		return "normal/show-contacts";
	}
	
	@RequestMapping("/get-contact")
	public String getContactById(@RequestParam("contactId")int contactId, Model model) {
		
		Optional<Contact> opcontact = this.contactDao.findById(contactId);
		Contact con = opcontact.get();
		System.out.println(con);
		model.addAttribute("title","Contact Info");
		model.addAttribute("contact",con);
		return "normal/contactCard";
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
	
	@PostMapping("/updateUser")
	public RedirectView updateUser (@ModelAttribute User myUser) {
		System.out.println("recieved user");
		System.out.println(myUser);
		String rdUrl = "index";
		return new RedirectView(rdUrl,true);
	}
	
	@PostMapping("/update-contact")
	public RedirectView updateHandler(@ModelAttribute Contact con,
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
		
		String rdUrl = "show-contacts/"+0;
		return new RedirectView(rdUrl, true);
	}
	
	
	
//	delete contact
	@RequestMapping("/delete-contact")
	public RedirectView deleteContact(@RequestParam("contactId") int contactId,
			Principal princiapal
			) {
		
		try {
			//fetch user
			User cUser = this.userDao.getUserByEmail(princiapal.getName());
			//fetch contact
			Optional<Contact> opcontact = this.contactDao.findById(contactId);
			Contact contact = opcontact.get();
			// remove association this will result in deletion of orphan contact
			cUser.getContacts().remove(contact);
			this.userDao.save(cUser);
			
			System.out.println("Successfully Deleted");
		} catch (Exception e) {
			System.out.println("Failed : Cannot Delete Contact with ID : "+contactId);
			e.printStackTrace();
		}
		
		String rdUrl = "show-contacts/"+0;
		return new RedirectView(rdUrl);
	}
	
	@RequestMapping("/update-user")
	public String updateUser(Model model) {
		model.addAttribute("title","Profile Settings - Smart Contacts Manager");
		return "normal/updateProfile";
	}
	
	@PostMapping("/process-update-profile")
	public String updateUserHandler(Principal principal,
			@RequestParam("name") String name,
			@RequestParam("gender") String gender,
			@RequestParam("password") String oldPassword,
			@RequestParam("password_new") String newPassword,
			Model model
			) {
		// fetch cUser to be updated
		User cUser = this.userDao.getUserByEmail(principal.getName());
		
		cUser.setName(name);
		cUser.setGender(gender);
		
		try {
			
			// update if old password matches in db
			if(this.passEncoder.matches(oldPassword, cUser.getPassword())){
				// change pass
				cUser.setPassword(this.passEncoder.encode(newPassword));
				this.userDao.save(cUser);
			}else {
				System.out.println("password didn't match with existing password ! Please try again...");
				model.addAttribute("matchingErr","password didn't match with existing password ! Please try again...");
			}
			
		} catch (Exception e) {
			System.out.println("exception occured");
			e.printStackTrace();
		}
		
		return "normal/userDashboard";
	}
	
	
	
}
