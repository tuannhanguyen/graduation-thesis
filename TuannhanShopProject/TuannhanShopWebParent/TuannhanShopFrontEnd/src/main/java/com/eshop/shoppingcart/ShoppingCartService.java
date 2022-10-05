package com.eshop.shoppingcart;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Product;
import com.eshop.product.ProductRepository;

@Transactional
@Service
public class ShoppingCartService {

	@Autowired private CartItemRepository cartRepo;
	@Autowired private ProductRepository productRepository;

	public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
		Integer updatedQuantity = quantity;

		Product product = new Product(productId);
		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);

		if (cartItem != null) {
			updatedQuantity = cartItem.getQuantity() + quantity;
			if(updatedQuantity > 5) {
				throw new ShoppingCartException("Could not add more " + quantity +" item(s)"
						+ " because there's are already " + cartItem.getQuantity() + " item(s)"
						+ " in your shopping cart. Maximum allowed is 5");
			}
		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}

		cartItem.setQuantity(updatedQuantity);

		cartRepo.save(cartItem);

		return updatedQuantity;
	}

	public List<CartItem> listCartItems(Customer customer){
		return cartRepo.findByCustomer(customer);
	}

	public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
		cartRepo.updateQuantity(quantity, customer.getId(), productId);
		Product product = productRepository.findById(productId).get();
		float subTotal = product.getDiscountPrice() * quantity;

		return subTotal;
	}

	public void removeProduct(Integer id, Customer customer) {
		cartRepo.deleteByCustomerAndProduct(customer.getId(), id);
	}

}