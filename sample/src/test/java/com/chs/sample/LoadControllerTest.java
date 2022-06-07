package com.chs.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.chs.sample.controller.LoadController;
import com.chs.sample.model.Customer;
import com.chs.sample.repository.CustomerRepository;
import com.chs.sample.service.CustomerService;


@SpringBootTest(classes= {LoadControllerTest.class})
class LoadControllerTest {
	
	@Mock
	CustomerRepository repo;
	
	@InjectMocks
	CustomerService service;
	
	public List<Customer> myCustomers;

	@Test
	void testAdd() {
		LoadController loadController=new LoadController();
		int expected=1110;
		int actual=loadController.add(10,99);
		
		assertEquals(expected, actual,"message");
	}
	
	@Test
	public void testCustomer() {
		
		List<Customer> myCustomers=new ArrayList<Customer>();
		myCustomers.add(new Customer(1,"wew","fwe","dfgd","bcfv","grg","vfdb","d","dfb","db"));	
		myCustomers.add(new Customer(2,"wew","fwe","dfgd","bcfv","grg","vfdb","d","dfb","db"));	
		myCustomers.add(new Customer(3,"wew","fwe","dfgd","bcfv","grg","vfdb","d","dfb","db"));	

		when(repo.findAll()).thenReturn(myCustomers);
		
		assertEquals(2,service.getCustomers().size());
	}




}
