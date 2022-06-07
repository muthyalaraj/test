package com.chs.sample.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chs.sample.model.Customer;
import com.chs.sample.repository.CustomerRepository;

@Component
public class DBWriter implements ItemWriter<Customer> {

    private CustomerRepository customerRepository;

    @Autowired
    public DBWriter (CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void write(List<? extends Customer> customers) throws Exception{
        System.out.println("Data Saved for Customers: " + customers);
        System.out.println("@@@@ Total Data Chunk Size  ::: "+customers.size());
        customerRepository.saveAll(customers);
    }
}
