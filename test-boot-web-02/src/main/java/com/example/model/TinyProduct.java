package com.example.model;

public class TinyProduct{
	  private String policyNo;
	  private String customerName;
	  private String customerAddress;
	  private String contractStartDate;
	  private String productCode;
	  private String productCategory;
	  private String price;
	  public TinyProduct(String policyNo,String customerName,String customerAddress, String contractStartDate, String productCode, String productCategory, String price){
		  this.policyNo = policyNo;
		  this.customerName = customerName;
		  this.customerAddress = customerAddress;
		  this.contractStartDate = contractStartDate;
		  this.productCode = productCode;
		  this.productCategory = productCategory;
		  this.price = price;
	  }
	  public String getPolicyNo(){return this.policyNo;}
	  public String getCustomerName() {
		return customerName;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public String getContractStartDate() {
		return contractStartDate;
	}
	public String getProductCode() {
		return productCode;
	}
	public String getProductCategory() {
		return productCategory;
	}
	public String getPrice(){return this.price;}
}
