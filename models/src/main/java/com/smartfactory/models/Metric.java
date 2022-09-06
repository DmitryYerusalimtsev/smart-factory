package com.smartfactory.models;

public class Metric {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    private Long id;
    private String name;
    private String uom;

    public Metric() {
    }

    public Metric(Long id,
                  String name,
                  String uom) {

        this.id = id;
        this.name = name;
        this.uom = uom;
    }
}
