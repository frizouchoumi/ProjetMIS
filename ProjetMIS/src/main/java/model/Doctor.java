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
 * @author Elise
 */
public class Doctor implements Serializable{
    private int iddoctor;
    private int inami;
    private Person idperson;
    private List<File> fileList;

    public Doctor(int iddoctor, int inami) {
        this.iddoctor = iddoctor;
        this.inami = inami;
    }

    public int getIddoctor() {
        return iddoctor;
    }

    public void setIddoctor(int iddoctor) {
        this.iddoctor = iddoctor;
    }

    public int getInami() {
        return inami;
    }

    public void setInami(int inami) {
        this.inami = inami;
    }

    public Person getIdperson() {
        return idperson;
    }

    public void setIdperson(Person idperson) {
        this.idperson = idperson;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }
    
    
}
