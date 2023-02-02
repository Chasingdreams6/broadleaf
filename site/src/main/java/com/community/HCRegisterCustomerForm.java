package com.community;

import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;

public class HCRegisterCustomerForm extends RegisterCustomerForm {
    public String referralCode;

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
}
