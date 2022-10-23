package com.eshop.setting;

import java.util.List;

import com.eshop.common.entity.Setting;
import com.eshop.common.entity.SettingBag;

public class CurrencySettingBag extends SettingBag {

	public CurrencySettingBag(List<Setting> listSettings) {
		super(listSettings);
	}

	public String getSymbol() {
		return "$";
	}
	
	public String getSymbolPosition() {
		return "After";
	}
	
	public String getDecimalPointType() {
		return "COMMA";
	}

	public String getThousandPointType() {
		return "POINT";
	}	
	
	public int getDecimalDigits() {
		return 1;
	}
}