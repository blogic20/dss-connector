package ru.blogic.dss.api.dto.dsspolicy;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class TspService implements Serializable{
    private String name;
    private String title;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
