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
    private String Name;
    private Date DateOfBirth; /*Year/Month/Day*/
    private List<Doctor> doctorList;
    private List<Patient> patientList;

    public Person(int id, String Name, Date DateOfBirth) {
        this.idperson = idperson;
        this.Name = Name;
        this.DateOfBirth = DateOfBirth;
    }

    public int getIdperson() {
        return idperson;
    }

    public void setIdperson(int id) {
        this.idperson= idperson;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
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
