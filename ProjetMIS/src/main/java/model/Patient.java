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
public class Patient implements Serializable{

    private int idpatient;
    private int SocialSec;
    private Person idperson;
    private List<File> fileList;
    
    public Patient(int idpatient, int SocialSec) {
        this.idpatient = idpatient;
        this.SocialSec = SocialSec;
    }

    public int getIdpatient() {
        return idpatient;
    }

    public void setIdpatient(int idpatient) {
        this.idpatient = idpatient;
    }

    public int getSocialSec() {
        return SocialSec;
    }

    public void setSocialSec(int SocialSec) {
        this.SocialSec = SocialSec;
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
