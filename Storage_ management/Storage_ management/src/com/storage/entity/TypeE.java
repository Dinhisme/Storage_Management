package com.storage.entity;

/**
 *
 * @author MyPC
 */
public class TypeE {

    public String idType, Type;

    public TypeE() {
    }

    public TypeE(String idType, String Type) {
        this.idType = idType;
        this.Type = Type;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

}
