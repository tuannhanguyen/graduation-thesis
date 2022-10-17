package com.eshop.admin.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.customer.CustomerService;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.paging.PagingAndSortingParam;
import com.eshop.admin.security.ShopUserDetails;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Order;
import com.eshop.common.entity.OrderDetail;
import com.eshop.common.entity.OrderStatus;
import com.eshop.common.entity.OrderTrack;
import com.eshop.common.entity.Product;

@Controller
public class OrderController {
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
	
	@Autowired private OrderService orderService;
	//@Autowired private SettingService settingService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(
			@PathVariable(name = "pageNum") int pageNum, Model model,
					@Param("sortField") String sortField,
					@Param("sortDir") String sortDir,
					 @Param("keyword") String keyword,
			@AuthenticationPrincipal ShopUserDetails loggedUser) {

		Page<Order> page = orderService.listByPage(pageNum, "orderTime", "asc", null);
		List<Order> listOrders = page.getContent();
		
		System.out.println("SIZE: " + listOrders.size());

		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		//orderService.listByPage(pageNum, helper);
		//loadCurrencySetting(request);
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
			return "orders/orders_shipper";
		}
		
		return "orders/orders";
	}
	
//	private void loadCurrencySetting(HttpServletRequest request) {
//		List<Setting> currencySettings = settingService.getCurrencySettings();
//		
//		for (Setting setting : currencySettings) {
//			request.setAttribute(setting.getKey(), setting.getValue());
//		}	
//	}	
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra, HttpServletRequest request,
			@AuthenticationPrincipal ShopUserDetails loggedUser) {
		try {
			Order order = orderService.get(id);
			//loadCurrencySetting(request);			
			
			boolean isVisibleForAdminOrSalesperson = false;
			
			if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
				isVisibleForAdminOrSalesperson = true;
			}
			
			model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
			model.addAttribute("order", order);
			
			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
		
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			orderService.delete(id);;
			ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);;
			
			List<Country> listCountries = orderService.listAllCountries();
			
			model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);
			
			return "orders/order_form";
			
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
		
	}	
	
	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);
		
		updateProductDetails(order, request);
		updateOrderTracks(order, request);

		orderService.save(order);		
		
		ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");
		
		return defaultRedirectURL;
	}

	private void updateOrderTracks(Order order, HttpServletRequest request) {
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackNotes = request.getParameterValues("trackNotes");
		
		List<OrderTrack> orderTracks = order.getOrderTracks();
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		
		for (int i = 0; i < trackIds.length; i++) {
			OrderTrack trackRecord = new OrderTrack();
			
			Integer trackId = Integer.parseInt(trackIds[i]);
			if (trackId > 0) {
				trackRecord.setId(trackId);
			}
			
			trackRecord.setOrder(order);
			trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
			trackRecord.setNotes(trackNotes[i]);
			
			try {
				trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			orderTracks.add(trackRecord);
		}
	}

	private void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productDetailCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productSubtotals = request.getParameterValues("productSubtotal");
		String[] productShipCosts = request.getParameterValues("productShipCost");
		
		Set<OrderDetail> orderDetails = order.getOrderDetails();
		
		for (int i = 0; i < detailIds.length; i++) {
			System.out.println("Detail ID: " + detailIds[i]);
			System.out.println("\t Prodouct ID: " + productIds[i]);
			System.out.println("\t Cost: " + productDetailCosts[i]);
			System.out.println("\t Quantity: " + quantities[i]);
			System.out.println("\t Subtotal: " + productSubtotals[i]);
			System.out.println("\t Ship cost: " + productShipCosts[i]);
			
			OrderDetail orderDetail = new OrderDetail();
			Integer detailId = Integer.parseInt(detailIds[i]);
			if (detailId > 0) {
				orderDetail.setId(detailId);
			}
			
			orderDetail.setOrder(order);
			orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
			orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
			orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
			orderDetail.setQuantity(Integer.parseInt(quantities[i]));
			orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
			
			orderDetails.add(orderDetail);
			
		}
		
	}
	
}