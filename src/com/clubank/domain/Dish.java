package com.clubank.domain;

public class Dish {

	public String Name;
	public String Price;
	public String Code;
	public String Category;

	public Dish(String code, String name) {
		Code = code;
		Name = name;
	}

	public Dish(String name, String price, String code, String category) {
		Name = name;
		Price = price;
		Code = code;
		Category = category;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPrice() {
		return Price;
	}

	public void setPrice(String price) {
		Price = price;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}

}
