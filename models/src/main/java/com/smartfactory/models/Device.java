package com.smartfactory.models;

public class Device {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    private Long id;
    private String type;
    private String description;
    private String softwareVersion;

    public Device() {
    }

    public Device(Long id,
                  String type,
                  String description,
                  String softwareVersion) {

        this.id = id;
        this.type = type;
        this.description = description;
        this.softwareVersion = softwareVersion;
    }
}
