package com.inopek.duvana.sink.beans;


import java.util.Date;

public class SinkBean {

    private Long id;
    private Long sinkStatutId;
    private Long sinkTypeId;
    private Long lenght;
    private Long pipeLineDiameterId;
    private Long pipeLineLenght;
    private Long plumbOptionId;
    private String reference;
    private String imageBefore;
    private String imageAfter;
    private String observations;

    private AddressBean adresse;
    private ClientBean client;
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

    public Long getSinkStatutId() {
        return sinkStatutId;
    }

    public void setSinkStatutId(Long sinkStatutId) {
        this.sinkStatutId = sinkStatutId;
    }

    public Long getSinkTypeId() {
        return sinkTypeId;
    }

    public void setSinkTypeId(Long sinkTypeId) {
        this.sinkTypeId = sinkTypeId;
    }

    public AddressBean getAdresse() {
        return adresse;
    }

    public void setAdresse(AddressBean adresse) {
        this.adresse = adresse;
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

    public Long getLenght() {
        return lenght;
    }

    public void setLenght(Long lenght) {
        this.lenght = lenght;
    }

    public Long getPipeLineDiameterId() {
        return pipeLineDiameterId;
    }

    public void setPipeLineDiameterId(Long pipeLineDiameterId) {
        this.pipeLineDiameterId = pipeLineDiameterId;
    }

    public Long getPipeLineLenght() {
        return pipeLineLenght;
    }

    public void setPipeLineLenght(Long pipeLineLenght) {
        this.pipeLineLenght = pipeLineLenght;
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
}


