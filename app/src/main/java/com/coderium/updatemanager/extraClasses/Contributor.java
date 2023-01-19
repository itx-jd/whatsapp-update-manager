package com.coderium.updatemanager.extraClasses;

import java.util.ArrayList;
import java.util.List;

public class Contributor {

    public List<String> nameList;
    public List<String> socialLink;

    public Contributor() {
        nameList = new ArrayList<>();
        socialLink = new ArrayList<>();
    }

    public void setNameList(List<String> nameList) {
        this.nameList = nameList;
    }

    public void setSocialLink(List<String> socialLink) {
        this.socialLink = socialLink;
    }

    public List<String> getNameList() {
        return nameList;
    }

    public List<String> getSocialLink() {
        return socialLink;
    }
}
