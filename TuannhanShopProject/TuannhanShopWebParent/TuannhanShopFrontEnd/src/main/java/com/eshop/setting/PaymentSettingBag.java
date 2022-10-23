package com.eshop.setting;

import java.util.List;

import com.eshop.common.entity.Setting;
import com.eshop.common.entity.SettingBag;

public class PaymentSettingBag extends SettingBag {

	public PaymentSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}

	public String getURL() {
		return "https://api-m.sandbox.paypal.com";
	}
	
	public String getClientID() {
		return "AfnU7buI3EXXT_4r9UK9LYTwYqF6lkmlh2f3e1Fe_L7WDCHtMd8muEzOk33bJmM4GgC3tB6xtWmHePR1";
	}
	
	public String getClientSecret() {
		return "EGCSfahmWNgAz76-12HnqSaEx4W4oStaQKsq57vu8m-v-M5wD_HyDODvdmselhGnrLQq2dbrsiBdh1ru";
	}
}