package com.inopek.duvana.sink.beans;

import java.io.Serializable;
import java.util.Date;

public class UserBean implements Serializable {

    private static final long serialVersionUID = 1118930171242157139L;
    private final String imiNumber;
    private Long id;
    private Date creationDate;
    private Date updateDate;

    public UserBean(String imiNumber) {
        this.imiNumber = imiNumber;
    }

    public String getImiNumber() {
        return imiNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
