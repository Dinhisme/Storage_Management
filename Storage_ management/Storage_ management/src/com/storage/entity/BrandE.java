package com.storage.entity;

/**
 *
 * @author MyPC
 */
public class BrandE {
    String idBrand, Brand;

    public BrandE() {
    }

    public BrandE(String idBrand, String Brand) {
        this.idBrand = idBrand;
        this.Brand = Brand;
    }

    public String getIdBrand() {
        return idBrand;
    }

    public void setIdBrand(String idBrand) {
        this.idBrand = idBrand;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String Brand) {
        this.Brand = Brand;
    }   
    
}
