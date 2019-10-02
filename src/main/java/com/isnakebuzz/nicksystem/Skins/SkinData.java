package com.isnakebuzz.nicksystem.Skins;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkinData implements Serializable {

    private String name;
    private String value;
    private String signature;

    public SkinData(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "SkinData{name=" + name + ",value" + value + ",signature" + signature + "}";
    }

}
