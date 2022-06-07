package com.chs.sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.chs.sample.model.Customer;
import com.chs.sample.repository.CustomerRepository;

@Service
@Component
public class CustomerService {

	@Autowired
	private CustomerRepository repository;
	
	public List<Customer> getCustomers(){
		
		List<Customer> customers=repository.findAll();
		return customers;
	}
}
