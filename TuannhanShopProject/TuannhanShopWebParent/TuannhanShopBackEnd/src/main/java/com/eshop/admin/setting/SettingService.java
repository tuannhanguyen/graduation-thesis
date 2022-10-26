package com.eshop.admin.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Setting;
import com.eshop.common.entity.SettingCategory;

@Service
public class SettingService {
    @Autowired private SettingRepository repo;

    public List<Setting> listAllSettings() {
        return (List<Setting>) repo.findAll();
    }

    public List<Setting> getCurrencySettings() {
        return repo.findByCategory(SettingCategory.CURRENCY);
    }

    public List<Setting> getPaymentSettings() {
        return repo.findByCategory(SettingCategory.PAYMENT);
    }
}