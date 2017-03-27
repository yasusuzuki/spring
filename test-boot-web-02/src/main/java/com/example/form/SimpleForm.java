package com.example.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

public class SimpleForm {
	private static final long serialVersionUID = -157143280035400042L;

	@NotNull
	@Size(min = 1, max = 120)
	private String policyNo;

	@Pattern(regexp = "")
	private String customerName;

	private String customerAddress;

	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private String contractStartDate;

	private String productCode;

	@Pattern(regexp = "1|2|3|4|5")
	private String productCategory;


	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(String contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFareaNl2br() {
		// if (StringUtils.isNotEmpty(this.farea)) {
		// return this.farea.replaceAll("\n", "<br/>");
		// }
		return "";
	}

	@Override
	public String toString() {
		// return ToStringBuilder.reflectionToString(this,
		// ToStringStyle.DEFAULT_STYLE);
		return "SimpleForm "
		+ " policy no:" + getPolicyNo() 
		+ " contract start date: " + getContractStartDate()
		+ " customer address: " + getCustomerAddress()
		+ " product category: " + getProductCategory()
		+ " product code: " + getProductCode();
	}

}
