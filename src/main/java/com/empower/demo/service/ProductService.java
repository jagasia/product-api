package com.empower.demo.service;

import org.springframework.stereotype.Service;

import com.empower.demo.entity.Product;
import com.empower.demo.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	//crud
	public Product createProduct(Product product) {
		return productRepository.save(product);
	}
	
	public Product getProductById(Long id) {
		return productRepository.findById(id).orElse(null);
	}
	
	 public Product updateProduct(Long id, Product productDetails) {
		Product product = productRepository.findById(id).orElse(null);
		if (product != null) {
			product.setName(productDetails.getName());
			product.setCategory(productDetails.getCategory());
			product.setPrice(productDetails.getPrice());
			product.setImage(productDetails.getImage());
			return productRepository.save(product);
		}
		return null;
	}
	  public void deleteProduct(Long id) {
		  		productRepository.deleteById(id);
	  }
	  //all products
	   public java.util.List<Product> getAllProducts() {
		  		return productRepository.findAll();
	  }
}
