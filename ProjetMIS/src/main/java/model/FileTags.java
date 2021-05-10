/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Daniel
 */
public class FileTags implements Serializable{
    private int idfiletags;
    private String value;
    private File idfile;
    private Tag idtag;

    public FileTags(int idfiletags, String value) {
        this.idfiletags = idfiletags;
        this.value = value;
    }

    public int getIdfiletags() {
        return idfiletags;
    }

    public void setIdfiletags(int idfiletags) {
        this.idfiletags = idfiletags;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public File getIdfile() {
        return idfile;
    }

    public void setIdfile(File idfile) {
        this.idfile = idfile;
    }

    public Tag getIdtag() {
        return idtag;
    }

    public void setIdtag(Tag idtag) {
        this.idtag = idtag;
    }
    
    
    
    
}
