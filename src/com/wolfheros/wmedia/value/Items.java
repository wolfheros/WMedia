package com.wolfheros.wmedia.value;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Items implements Serializable {
    String image_url;
    String media_url;
    String name;
    Map<Integer, List<ItemEpisode>> sourceMap;
    List<String> stringList;

    public Map<Integer, List<ItemEpisode>> getSourceMap() {
        return this.sourceMap;
    }

    public void setSourceMap(Map<Integer, List<ItemEpisode>> sourceMap2) {
        this.sourceMap = sourceMap2;
    }

    public List<String> getStringList() {
        return this.stringList;
    }

    public void setStringList(List<String> stringList2) {
        this.stringList = stringList2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2.replace("\\s", "").split("\\(")[0];
    }

    public String getImage_url() {
        return this.image_url;
    }

    public void setImage_url(String image_url2) {
        this.image_url = image_url2;
    }

    public String getMedia_url() {
        return this.media_url;
    }

    public void setMedia_url(String media_url2) {
        this.media_url = media_url2;
    }
}