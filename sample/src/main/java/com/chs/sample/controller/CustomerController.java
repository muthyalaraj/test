package com.chs.sample.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chs.sample.model.Customer;
import com.chs.sample.service.CustomerService;

@RestController
public class CustomerController {
	
	@Autowired
	private CustomerService service;
	
	@GetMapping("/customer")
	public List<Customer> findAllCustomers(){
		return service.getCustomers();
	}

}
