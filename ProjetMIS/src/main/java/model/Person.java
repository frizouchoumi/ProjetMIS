/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Elise
 */
public class Person implements Serializable{
    private int idperson;
    private String FirstName;
    private String LastName;
    private Date DateOfBirth; /*Year/Month/Day*/
    private List<Doctor> doctorList;
    private List<Patient> patientList;

    public Person(){
        
    }
    public Person(int id, String FirstName, String LastName, Date DateOfBirth) {
        this.idperson = idperson;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.DateOfBirth = DateOfBirth;
    }

    public int getIdperson() {
        return idperson;
    }

    public void setIdperson(int id) {
        this.idperson= idperson;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }
    
    public Date getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(Date DateOfBirth) {
        this.DateOfBirth = DateOfBirth;
    }
    
    public List<Doctor> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }
    
    public List<Patient> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }
    
    
  
    
}
