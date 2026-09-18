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
@RequestMapping("/products")
@CrossOrigin(origins={"http://localhost:5174","http://localhost:5173"})
public class ProductController {
	
	private final Logger logger = LoggerFactory.getLogger(ProductController.class);

	private final ProductService productService;
	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	//let create endpoints for get, post, put, delete
	
	@GetMapping
	public List<Product> getAllProducts() throws InterruptedException {
		Thread.sleep(5000);
		return productService.getAllProducts();
	}
	
	@GetMapping("/{id}")
	public Product getProductById(@PathVariable("id") Long id) {
		return productService.getProductById(id);
	}

/*	
	@PostMapping
	public Product createProduct(@RequestParam("id")Long id, @RequestParam("name") String name, @RequestParam("price") Double price, @RequestParam("file") MultipartFile file) throws IOException {
		byte[]image=file.getBytes();
		logger.info(image.length+" bytes");
	    Product product = new Product();
	    product.setId(id);
	    product.setName(name);
	    product.setPrice(price);
	    product.setImage(image);
	    return productService.createProduct(product);
	}
*/
	
	@PostMapping
	public Product createProduct(@RequestBody Product product) {
		logger.info("Creating product: {}", product);
		return productService.createProduct(product);
	}
	
	@PutMapping("/{id}")
	public Product updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
		return productService.updateProduct(id, productDetails);
	}
	
	@DeleteMapping("/{id}")
	public void deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
	}
	
	
	
}
