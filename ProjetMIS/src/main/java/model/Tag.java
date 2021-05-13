/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Daniel
 */
public class Tag implements Serializable{
    private int idtag;
    private String TagName;
    private List<FileTags> filetagsList;

    public Tag(int idtag, String TagName) {
        this.idtag = idtag;
        this.TagName = TagName;
    }

    public int getIdtag() {
        return idtag;
    }

    public void setIdtag(int idtag) {
        this.idtag = idtag;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String TagName) {
        this.TagName = TagName;
    }
      
    public List<FileTags> getFileTagsList() {
        return filetagsList;
    }

    public void setFileTagsList(List<FileTags> filetagsList) {
        this.filetagsList = filetagsList;
    }
    
}
