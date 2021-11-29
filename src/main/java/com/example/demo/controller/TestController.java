package com.example.demo.controller;



import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.dao.RoomRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.entities.Rooms;
import com.example.demo.entities.User;
import com.example.demo.helper.Message;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.example.demo.dao.UserRepository;
//import com.example.demo.entities.User;


@Controller
public class TestController {
	
@Autowired
private BCryptPasswordEncoder passwordEncoder;
	

	
@Autowired
private UserRepository userRepository;	
	
@Autowired
private RoomRepository roomRepository;	


@RequestMapping(value={"/","home"},method=RequestMethod.POST)
public String start() {
	return "hotel";
}

@RequestMapping(value= {"/signup"})
public String signup(Model model) {
	
	model.addAttribute("user",new User());
	return "signup";
}

@RequestMapping(value= {"/hotel"},method=RequestMethod.GET)
public String hotel() {
	return "hotel";
}


@RequestMapping(value={"/room_reservation"})
public String room_reservation(Model model){
	model.addAttribute("rooms",new Rooms());
	return "room_reservation";

}
@RequestMapping(value={"/facilities"})
public String facilities(){
	return "facilities";
}
@RequestMapping(value={"/restaurant"})
public String restaurant(){
	return "restaurant";
}

@RequestMapping(value={"/user/data"})
public String userdashboard(){
	return "userdashboard";
}


	
	


//@RequestMapping(value={"/logout"})
//public String logout(){
//	return "logout";
//}

@RequestMapping(value={"/admin"},method=RequestMethod.GET)
public String admin(Model model){
	 List<User> listUsers = userRepository.findAll();
	 model.addAttribute("listUsers", listUsers);
	return "admin";
}
@RequestMapping(value={"/employee"},method=RequestMethod.GET)
public String commonEmployee(Model model){
	 List<User> listUsers = userRepository.findAll();
	 model.addAttribute("listUsers", listUsers);
	return "employee";
}



//@Autowired
//private UserRepository userRepository;


//@GetMapping(value= {"/test"})
//@ResponseBody
//public String test() {
//	
//	User user=new User();
//	user.setName("Soham Kayal");
//	user.setEmail("sohamk4103@gmail.com");
//	
//	userRepository.save(user);
//	
//	return "Working";
//}


//handler for registering user
@RequestMapping(value= {"/do_register"},method=RequestMethod.POST)
public String registerUser(@ModelAttribute("user") User user,Model model,HttpSession session) {
	
	user.setRole("ROLE_USER");
	user.setEnabled(true);
	user.setPassword(passwordEncoder.encode(user.getPassword()));
	System.out.println("USER "+user);
	
	User result= this.userRepository.save(user);
	
	model.addAttribute("user", new User());
	session.setAttribute("message", new Message("Successfully Registered","alert-success"));
	return "signup";
}

@RequestMapping(value= {"/process_reservation"},method=RequestMethod.POST)
public String reserveRoom(@ModelAttribute("rooms") Rooms rooms,Model model,HttpSession session,Principal principal) {
	
	String name = principal.getName();
	User user=this.userRepository.getUserByUserName(name);
	
	user.getRooms().add(rooms);
	
	if(rooms.getCategory().equals("Luxury Room")) {
		rooms.setPrice(16950);
	}
	else if(rooms.getCategory().equals("Luxury Grande Room City View")) {
		rooms.setPrice(18700);
	}
	
	else if(rooms.getCategory().equals("Luxury Grande Room Sea View")) {
		rooms.setPrice(20825);
	}else {
		rooms.setPrice(0);
	}
	System.out.println("Rooms "+rooms);
	
	Rooms reservation=this.roomRepository.save(rooms);
	  
	
	model.addAttribute("rooms", new Rooms());
	session.setAttribute("message", new Message("Successfully Registered","alert-success"));
	return "room_reservation";
}





@RequestMapping(value= {"/dashboard"})
public String userDashboardinfo(@ModelAttribute("rooms") Rooms rooms,Model model,HttpSession session,Principal principal) {
	String name = principal.getName();
	
	User user=this.userRepository.getUserByUserName(name);
	model.addAttribute("user", user);
	System.out.println("User "+user);
	return "userdashboard";
}


//handler for deleting user data from admin login
@RequestMapping(value= {"/admin/delete/{cid}"})
public String deleteUser(@PathVariable("cid")Integer cId,Model model,HttpSession session) {
	
	Optional<User> userOptional=this.userRepository.findById(cId);
	User user=userOptional.get();
	
	this.userRepository.delete(user);
	
	session.setAttribute("message", new Message("Contact deleted Successfully...","success"));
	
	return "redirect:/admin";
}

//handler for opening update form for admin
@RequestMapping(value= {"/admin/update/{cid}"})
public String updateUser(@PathVariable("cid")Integer cId,Model model,HttpSession session) {
	Optional<User> userOptional=this.userRepository.findById(cId);
	User user=userOptional.get();
	model.addAttribute("user", user);
	return "admin_userinfo";
}

//handler for processing update form using admin login
@RequestMapping(value= {"/admin/do_update"},method = RequestMethod.POST)
public String processUpdate(@ModelAttribute User user,HttpSession session) {
	
	
	System.out.println("USER "+user);
	
	User result= this.userRepository.save(user);
	
	session.setAttribute("message", new Message("Contact updated Successfully...","success"));
	return "admin_userinfo";
}

}
  
