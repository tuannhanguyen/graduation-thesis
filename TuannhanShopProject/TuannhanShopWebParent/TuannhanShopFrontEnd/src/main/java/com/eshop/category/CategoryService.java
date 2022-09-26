package com.eshop.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Category;

@Service
public class CategoryService {
	@Autowired private CategoryReposeitory categoryReposeitory;

	public List<Category> listNoChildrenCategories() {
		List<Category> listNoChildrenCategories = new ArrayList<>();

		List<Category> listEnabledCategories = categoryReposeitory.findAllEnabled();

		listEnabledCategories.forEach(category -> {
			Set<Category> children = category.getChildren();
			if (children == null || children.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		});

		return listNoChildrenCategories;
	}

	public Category getCategory(String alias) {
		return categoryReposeitory.findByAliasEnabled(alias);
	}
	
	public List<Category> getCategoryParents(Category child) {
		List<Category> listParents = new ArrayList<>();
		
		Category parent = child.getParent();
		
		while (parent != null) {
			listParents.add(0, parent);
			parent = parent.getParent();
		}
		
		listParents.add(child);
		
		return listParents;
	}
}
