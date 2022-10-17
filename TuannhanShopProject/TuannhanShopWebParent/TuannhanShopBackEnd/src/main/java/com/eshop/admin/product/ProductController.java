package com.eshop.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.eshop.admin.category.CategoryService;
import com.eshop.admin.security.ShopUserDetails;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Product;
import com.eshop.common.entity.ProductImage;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired CategoryService categoryService;

	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, "name", "asc", null, 0, model);
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId,
			Model model) {
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();

		List<Category> listCategories = categoryService.listCategoriesUsedInForm();

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		if (categoryId != null) model.addAttribute("categoryId", categoryId);

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listCategories", listCategories);

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
		model.addAttribute("numberOfExistingExtraImges", 0);


		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopUserDetails loggedUser ) throws IOException {

		if (loggedUser.hasRole("Salesperson")) {
			productService.saveProductPrice(product);
			redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");

			return "redirect:/products";

		}

		setMainImageName(mainImageMultipart, product);
		setExistingExtraImageNames(imageIDs, imageNames, product);
		setNewExtraImagesName(extraImageMultiparts, product);
		setProductDetails(detailIDs, detailNames, detailValues, product);

		Product savedProduct = productService.save(product);

		saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

		deleteExtraImagesWeredRemovedOnForm(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");

		return "redirect:/products";
	}

	private void deleteExtraImagesWeredRemovedOnForm(Product product) {
		String extraImageDir = "../product-images/" + product.getId() + "/extras";
		Path dirPath = Paths.get(extraImageDir);

		try {
			Files.list(dirPath).forEach(file -> {
				String fileName = file.toFile().getName();

				if (!product.containsImageName(fileName)) {
					try {
						Files.delete(file);
					} catch (IOException e) {
						// TODO: handle exception
					}
				}
			});
		} catch (IOException e) {
			// TODO: handle exception
		}

	}

	private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames,
			Product product) {
		if (imageIDs == null || imageIDs.length == 0) return;

		Set<ProductImage> images = new HashSet<>();

		for (int count = 0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];

			images.add(new ProductImage(id, name, product));
		}

		product.setImages(images);
	}

	private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
		if (detailNames == null || detailValues.length == 0) return;

		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);

			if (id != 0) {
				product.addDetails(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
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

	private void setNewExtraImagesName(MultipartFile[] extraImageMultiparts, Product product) {
		if (extraImageMultiparts.length > 0) {
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					if (!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}
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
			Integer numberOfExistingExtraImges = 0;

			if (product.getImages() == null) {
				numberOfExistingExtraImges = 0;
			} else {
				numberOfExistingExtraImges = product.getImages().size();
			}

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

	@GetMapping("/products/detail/{id}")
	public String viewProdutcDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			model.addAttribute("product", product);

			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

}
