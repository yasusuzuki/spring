package com.example.model;

public class TinyProduct{
	  private String policyNo;
	  private String contractorName;
	  private String contractorAddress;
	  private String contractStartDate;
	  private String productCode;
	  private String productCategory;
	  private String premium;
	  public TinyProduct(String policyNo,String contractorName,String contractorAddress, String contractStartDate, String productCode, String productCategory, String premium){
		  this.policyNo = policyNo;
		  this.contractorName = contractorName;
		  this.contractorAddress = contractorAddress;
		  this.contractStartDate = contractStartDate;
		  this.productCode = productCode;
		  this.productCategory = productCategory;
		  this.premium = premium;
	  }
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public String getContractorName() {
		return contractorName;
	}
	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}
	public String getContractorAddress() {
		return contractorAddress;
	}
	public void setContractorAddress(String contractorAddress) {
		this.contractorAddress = contractorAddress;
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
	public String getPremium() {
		return premium;
	}
	public void setPremium(String premium) {
		this.premium = premium;
	}
	  
	  

}
