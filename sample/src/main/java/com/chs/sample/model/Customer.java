package com.chs.sample.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.chs.sample.constants.GenderType;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	private String firstName;
	private String middleName; 
	private String lastName;
	// @Enumerated(EnumType.STRING)
	// private GenderType gender;
	private String gender;
	private String emailId;
	private String mobileNumber;
	private String pan; 
	private String aadhaar;

	public Customer(Integer id, String title, String firstName, String middleName, String lastName, String gender,
			String emailId, String mobileNumber, String pan, String aadhaar) {
		this.id = id;
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.gender = gender;
		this.emailId = emailId;
		this.mobileNumber = mobileNumber;
		this.pan = pan;
		this.aadhaar = aadhaar;
	}

	public Customer() {}

	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getMiddleName() {
		return middleName;
	}


	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getEmailId() {
		return emailId;
	}


	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}


	public String getMobileNumber() {
		return mobileNumber;
	}


	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}


	public String getPan() {
		return pan;
	}


	public void setPan(String pan) {
		this.pan = pan;
	}


	public String getAadhaar() {
		return aadhaar;
	}


	public void setAadhaar(String aadhaar) {
		this.aadhaar = aadhaar;
	}






}