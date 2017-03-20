package com.inopek.duvana.sink.beans;


import java.util.Date;

public class SinkBean {

    private Long id;
    private Long sinkStatusId;
    private Long sinkTypeId;
    private Long length;
    private Long pipeLineDiameterId;
    private Long pipeLineLength;
    private Long plumbOptionId;
    private String reference;
    private String imageBefore;
    private String imageAfter;
    private String observations;
    private String fileName;

    private AddressBean address;
    private ClientBean client;
    private UserBean userCreation;
    private UserBean userUpdate;
    private Date sinkCreationDate;
    private Date sinkUpdateDate;

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

    public String getImageBefore() {
        return imageBefore;
    }

    public void setImageBefore(String imageBefore) {
        this.imageBefore = imageBefore;
    }

    public String getImageAfter() {
        return imageAfter;
    }

    public void setImageAfter(String imageAfter) {
        this.imageAfter = imageAfter;
    }

    public Long getSinkStatusId() {
        return sinkStatusId;
    }

    public void setSinkStatusId(Long sinkStatusId) {
        this.sinkStatusId = sinkStatusId;
    }

    public Long getSinkTypeId() {
        return sinkTypeId;
    }

    public void setSinkTypeId(Long sinkTypeId) {
        this.sinkTypeId = sinkTypeId;
    }

    public AddressBean getAddress() {
        return address;
    }

    public void setAddress(AddressBean address) {
        this.address = address;
    }

    public Date getSinkCreationDate() {
        return sinkCreationDate;
    }

    public void setSinkCreationDate(Date sinkCreationDate) {
        this.sinkCreationDate = sinkCreationDate;
    }

    public Date getSinkUpdateDate() {
        return sinkUpdateDate;
    }

    public void setSinkUpdateDate(Date sinkUpdateDate) {
        this.sinkUpdateDate = sinkUpdateDate;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getPipeLineDiameterId() {
        return pipeLineDiameterId;
    }

    public void setPipeLineDiameterId(Long pipeLineDiameterId) {
        this.pipeLineDiameterId = pipeLineDiameterId;
    }

    public Long getPipeLineLength() {
        return pipeLineLength;
    }

    public void setPipeLineLength(Long pipeLineLength) {
        this.pipeLineLength = pipeLineLength;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Long getPlumbOptionId() {
        return plumbOptionId;
    }

    public void setPlumbOptionId(Long plumbOptionId) {
        this.plumbOptionId = plumbOptionId;
    }

    public ClientBean getClient() {
        return client;
    }

    public void setClient(ClientBean client) {
        this.client = client;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UserBean getUserCreation() {
        return userCreation;
    }

    public void setUserCreation(UserBean userCreation) {
        this.userCreation = userCreation;
    }

    public UserBean getUserUpdate() {
        return userUpdate;
    }

    public void setUserUpdate(UserBean userUpdate) {
        this.userUpdate = userUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SinkBean sinkBean = (SinkBean) o;

        return reference.equals(sinkBean.reference);

    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}


