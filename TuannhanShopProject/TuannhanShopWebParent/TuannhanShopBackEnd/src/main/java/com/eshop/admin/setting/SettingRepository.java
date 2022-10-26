package com.eshop.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.eshop.common.entity.Setting;
import com.eshop.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {
    public List<Setting> findByCategory(SettingCategory category);
}