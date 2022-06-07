package com.chs.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chs.sample.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
