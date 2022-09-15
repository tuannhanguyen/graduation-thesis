package com.eshop.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Category;

@Service
public class CategoryService {
	@Autowired CategoryRepository categoryRepository;

	public List<Category> listAll() {
		List<Category> rootCategories = categoryRepository.findRootCategories();

		return listHierarchicalCategories(rootCategories);
	}

	private List<Category> listHierarchicalCategories(List<Category> rootCategories) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));

			Set<Category> children = rootCategory.getChildren();

			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));

				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
			}
		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
			Category parent, int subLevel) {
		Set<Category> children = parent.getChildren();
		int newSubLevel = subLevel + 1;

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();

			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel);
		}
	}

	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesUsedInForm = new ArrayList<>();

		List<Category> categoriesInDb = (List<Category>) categoryRepository.findAll();

		for (Category category : categoriesInDb) {
			if (category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));

				Set<Category> children = category.getChildren();

				for (Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

					listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
				}
			}
		}

		return categoriesUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}

	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return categoryRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any category with ID " + id);
		}
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

		Category categoryByName = categoryRepository.findByName(name);

		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicatedName";
			} else {
				Category categoryByAlias = categoryRepository.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicatedAlias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicatedName";
			}

			Category categoryByAlias = categoryRepository.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicatedAlias";
			}
		}

		return "OK";
	}

}
