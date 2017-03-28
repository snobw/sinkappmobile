package com.inopek.duvana.sink.beans;

import java.io.Serializable;

public class ClientReferenceBean implements Serializable {

    private Long id;
    private String reference;
    private String clientName;
    private String fileName;
    private String profileName;

    public ClientReferenceBean() {
    }

    public ClientReferenceBean(String reference, String clientName, String fileName, String profileName) {
        this.reference = reference;
        this.clientName = clientName;
        this.fileName = fileName;
        this.profileName = profileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
