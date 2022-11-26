package com.storage.entity;


/**
 *
 * @author Phùng Quốc Vinh
 */
public class ProductE {

    public String idProduct, productName, idType, idBrand;
    public float price, totalMoney;
    public int amount;
    public String dateAdded, img;

    public ProductE() {
    }

    public ProductE(String idProduct, String productName, String idType, String idBrand,float price, int amount, float totalMoney, String dateAdded, String img) {
        this.idProduct = idProduct;
        this.productName = productName;
        this.idType = idType;
        this.idBrand = idBrand;
        this.price = price;
        this.amount = amount;
        this.totalMoney = totalMoney;
        this.dateAdded = dateAdded;
        this.img = img;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdBrand() {
        return idBrand;
    }

    public void setIdBrand(String idBrand) {
        this.idBrand = idBrand;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(float totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
