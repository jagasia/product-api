package com.empower.demo.entity;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
public class Product {
	@Id
	private Long id;
	@Length(min = 1, message = "Name must be at least 1 character long")
	private String name;
	private String category;
	@Positive(message = "Price must be a positive number")
	private Double price;
	@Lob
	private String image;
	
}
