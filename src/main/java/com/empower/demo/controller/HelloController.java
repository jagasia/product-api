package com.empower.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.empower.demo.entity.Product;
import com.empower.demo.service.ProductService;

@RestController
@RequestMapping("/hello")
@CrossOrigin(origins={"http://localhost:5174","http://localhost:5173"})
public class HelloController {
	
	
	@GetMapping
	public String getAllProducts() throws InterruptedException {
		return "Hello this is created in second commit";
	}
	
	
	
}
