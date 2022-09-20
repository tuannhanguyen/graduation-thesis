package com.eshop.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.brand.BrandService;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Product;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = productService.listAll();
		model.addAttribute("listProducts", listProducts);

		return "products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();

		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create new product");

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile mainImageMultipart,
			@RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException {

		setMainImageName(mainImageMultipart, product);
		setExtraImagesName(extraImageMultiparts, product);
		setProductDetails(detailNames, detailValues, product);

		Product savedProduct = productService.save(product);

		saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");

		return "redirect:/products";
	}

	private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
		if (detailNames == null || detailValues.length == 0) return;

		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];

			if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetails(name, value);
			}
		}

	}

	private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
			Product savedProduct) throws IOException {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}

		if (extraImageMultiparts.length > 0) {
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (multipartFile.isEmpty()) {
					continue;
				}

				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}

	}

	private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}

	private void setExtraImagesName(MultipartFile[] extraImageMultiparts, Product product) {
		if (extraImageMultiparts.length > 0) {
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					product.addExtraImage(fileName);
				}
			}
		}
	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes re) {
		productService.updateEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The product ID " + id + " has been " + status;
		re.addFlashAttribute("message", message);

		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String delete(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes re) {
		try {
			productService.delete(id);
			String productExtraImages = "../product-images/" + id + "/extras";
			String productImagesDir = "../product-images/" + id;

			FileUploadUtil.removeDir(productExtraImages);
			FileUploadUtil.removeDir(productImagesDir);

			re.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException e) {
			re.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/products";
	}

	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImges = product.getImages().size();

			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImges", numberOfExistingExtraImges);

			return "products/product_form";
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

}
