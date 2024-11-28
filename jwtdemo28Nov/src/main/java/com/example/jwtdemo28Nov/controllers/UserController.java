package com.example.jwtdemo28Nov.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwtdemo28Nov.dto.LoginRequestDTO;
import com.example.jwtdemo28Nov.dto.LoginResponseDTO;
import com.example.jwtdemo28Nov.dto.UserRegisterDTO;
import com.example.jwtdemo28Nov.entity.User;
import com.example.jwtdemo28Nov.jwt.JwtUtils;
import com.example.jwtdemo28Nov.service.UserDetailsServiceImpl;

/*
 * 
 * This is complete working example of JWT with login -- Perfect!!
 */

@RestController
@RequestMapping("api")
public class UserController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("home")
	public String home() {
		return "This is my home";
	}
	
	@GetMapping("test")
	public String test() {
		return "This is test case";
	}
	
	@PostMapping("register")
	public ResponseEntity<?> signup(@RequestBody UserRegisterDTO register){
		if(register!=null) {
			User user = User.builder()
			.username(register.getEmail())
			.name(register.getName())
			.password(passwordEncoder.encode(register.getPassword()))
			.build();
			user = userDetailsServiceImpl.saveUser(user);
			return new ResponseEntity<User>(user, HttpStatus.CREATED);
		}
		return new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO login){
		if(login!=null) {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
		    if(authentication.isAuthenticated()){
		    	LoginResponseDTO token = LoginResponseDTO
		    		   .builder()
		               .token(jwtUtils.GenerateToken(login.getUsername()))
		               .build();
		    	return new ResponseEntity<LoginResponseDTO>(token, HttpStatus.OK);
		    } else {
		        throw new UsernameNotFoundException("invalid user request..!!");
		    }
		}		
		return new ResponseEntity<String>("Bad Request!", HttpStatus.BAD_REQUEST);
	}
}