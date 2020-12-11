package com.wolfheros.wmedia.value;

import java.io.Serializable;

public class ItemEpisode implements Serializable {
    String bbr;
    String key;
    String resource_name;
    String resource_url;

    public String getResource_name() {
        return this.resource_name;
    }

    public void setResource_name(String resource_name2) {
        this.resource_name = resource_name2;
    }

    public String getResource_url() {
        return this.resource_url;
    }

    public void setResource_url(String resource_url2) {
        this.resource_url = resource_url2;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key2) {
        this.key = key2;
    }

    public String getBbr() {
        return this.bbr;
    }

    public void setBbr(String bbr2) {
        this.bbr = bbr2;
    }
}