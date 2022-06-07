package com.chs.sample.constants;

public enum GenderType {
	
	MALE("MALE"), FEMALE("FEMALE"),OTHERS("OTHERS"); 
	private String code;
	
	
	public String getCode() {
		return this.code;
	}

	private GenderType(String code) {
		this.code = code;
	}

}
