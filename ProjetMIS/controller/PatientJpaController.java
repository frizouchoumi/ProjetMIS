/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Person;
import model.Image;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import model.File;
import model.Patient;

/**
 *
 * @author Elise
 */
public class PatientJpaController implements Serializable {

    public PatientJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Patient patient) {
        if (patient.getImageList() == null) {
            patient.setImageList(new ArrayList<Image>());
        }
        if (patient.getFileList() == null) {
            patient.setFileList(new ArrayList<File>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person idperson = patient.getIdperson();
            if (idperson != null) {
                idperson = em.getReference(idperson.getClass(), idperson.getIdperson());
                patient.setIdperson(idperson);
            }
            List<Image> attachedImageList = new ArrayList<Image>();
            for (Image imageListImageToAttach : patient.getImageList()) {
                imageListImageToAttach = em.getReference(imageListImageToAttach.getClass(), imageListImageToAttach.getIdimage());
                attachedImageList.add(imageListImageToAttach);
            }
            patient.setImageList(attachedImageList);
            List<File> attachedFileList = new ArrayList<File>();
            for (File fileListFileToAttach : patient.getFileList()) {
                fileListFileToAttach = em.getReference(fileListFileToAttach.getClass(), fileListFileToAttach.getIdfile());
                attachedFileList.add(fileListFileToAttach);
            }
            patient.setFileList(attachedFileList);
            em.persist(patient);
            if (idperson != null) {
                idperson.getPatientList().add(patient);
                idperson = em.merge(idperson);
            }
            for (Image imageListImage : patient.getImageList()) {
                Patient oldIdpatientOfImageListImage = imageListImage.getIdpatient();
                imageListImage.setIdpatient(patient);
                imageListImage = em.merge(imageListImage);
                if (oldIdpatientOfImageListImage != null) {
                    oldIdpatientOfImageListImage.getImageList().remove(imageListImage);
                    oldIdpatientOfImageListImage = em.merge(oldIdpatientOfImageListImage);
                }
            }
            for (File fileListFile : patient.getFileList()) {
                Patient oldIdpatientOfFileListFile = fileListFile.getIdpatient();
                fileListFile.setIdpatient(patient);
                fileListFile = em.merge(fileListFile);
                if (oldIdpatientOfFileListFile != null) {
                    oldIdpatientOfFileListFile.getFileList().remove(fileListFile);
                    oldIdpatientOfFileListFile = em.merge(oldIdpatientOfFileListFile);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Patient patient) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patient persistentPatient = em.find(Patient.class, patient.getIdpatient());
            Person idpersonOld = persistentPatient.getIdperson();
            Person idpersonNew = patient.getIdperson();
            List<Image> imageListOld = persistentPatient.getImageList();
            List<Image> imageListNew = patient.getImageList();
            List<File> fileListOld = persistentPatient.getFileList();
            List<File> fileListNew = patient.getFileList();
            List<String> illegalOrphanMessages = null;
            for (File fileListOldFile : fileListOld) {
                if (!fileListNew.contains(fileListOldFile)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain File " + fileListOldFile + " since its idpatient field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idpersonNew != null) {
                idpersonNew = em.getReference(idpersonNew.getClass(), idpersonNew.getIdperson());
                patient.setIdperson(idpersonNew);
            }
            List<Image> attachedImageListNew = new ArrayList<Image>();
            for (Image imageListNewImageToAttach : imageListNew) {
                imageListNewImageToAttach = em.getReference(imageListNewImageToAttach.getClass(), imageListNewImageToAttach.getIdimage());
                attachedImageListNew.add(imageListNewImageToAttach);
            }
            imageListNew = attachedImageListNew;
            patient.setImageList(imageListNew);
            List<File> attachedFileListNew = new ArrayList<File>();
            for (File fileListNewFileToAttach : fileListNew) {
                fileListNewFileToAttach = em.getReference(fileListNewFileToAttach.getClass(), fileListNewFileToAttach.getIdfile());
                attachedFileListNew.add(fileListNewFileToAttach);
            }
            fileListNew = attachedFileListNew;
            patient.setFileList(fileListNew);
            patient = em.merge(patient);
            if (idpersonOld != null && !idpersonOld.equals(idpersonNew)) {
                idpersonOld.getPatientList().remove(patient);
                idpersonOld = em.merge(idpersonOld);
            }
            if (idpersonNew != null && !idpersonNew.equals(idpersonOld)) {
                idpersonNew.getPatientList().add(patient);
                idpersonNew = em.merge(idpersonNew);
            }
            for (Image imageListOldImage : imageListOld) {
                if (!imageListNew.contains(imageListOldImage)) {
                    imageListOldImage.setIdpatient(null);
                    imageListOldImage = em.merge(imageListOldImage);
                }
            }
            for (Image imageListNewImage : imageListNew) {
                if (!imageListOld.contains(imageListNewImage)) {
                    Patient oldIdpatientOfImageListNewImage = imageListNewImage.getIdpatient();
                    imageListNewImage.setIdpatient(patient);
                    imageListNewImage = em.merge(imageListNewImage);
                    if (oldIdpatientOfImageListNewImage != null && !oldIdpatientOfImageListNewImage.equals(patient)) {
                        oldIdpatientOfImageListNewImage.getImageList().remove(imageListNewImage);
                        oldIdpatientOfImageListNewImage = em.merge(oldIdpatientOfImageListNewImage);
                    }
                }
            }
            for (File fileListNewFile : fileListNew) {
                if (!fileListOld.contains(fileListNewFile)) {
                    Patient oldIdpatientOfFileListNewFile = fileListNewFile.getIdpatient();
                    fileListNewFile.setIdpatient(patient);
                    fileListNewFile = em.merge(fileListNewFile);
                    if (oldIdpatientOfFileListNewFile != null && !oldIdpatientOfFileListNewFile.equals(patient)) {
                        oldIdpatientOfFileListNewFile.getFileList().remove(fileListNewFile);
                        oldIdpatientOfFileListNewFile = em.merge(oldIdpatientOfFileListNewFile);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = patient.getIdpatient();
                if (findPatient(id) == null) {
                    throw new NonexistentEntityException("The patient with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patient patient;
            try {
                patient = em.getReference(Patient.class, id);
                patient.getIdpatient();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The patient with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<File> fileListOrphanCheck = patient.getFileList();
            for (File fileListOrphanCheckFile : fileListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Patient (" + patient + ") cannot be destroyed since the File " + fileListOrphanCheckFile + " in its fileList field has a non-nullable idpatient field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Person idperson = patient.getIdperson();
            if (idperson != null) {
                idperson.getPatientList().remove(patient);
                idperson = em.merge(idperson);
            }
            List<Image> imageList = patient.getImageList();
            for (Image imageListImage : imageList) {
                imageListImage.setIdpatient(null);
                imageListImage = em.merge(imageListImage);
            }
            em.remove(patient);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Patient> findPatientEntities() {
        return findPatientEntities(true, -1, -1);
    }

    public List<Patient> findPatientEntities(int maxResults, int firstResult) {
        return findPatientEntities(false, maxResults, firstResult);
    }

    private List<Patient> findPatientEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Patient.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Patient findPatient(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Patient.class, id);
        } finally {
            em.close();
        }
    }

    public int getPatientCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Patient> rt = cq.from(Patient.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    /*public List<Patient> findPatientsByFamilyName(String familyName){
        EntityManager em = getEntityManager();
        List<Person> persons = em.createNamedQuery("Person.findByFamilyname").setParameter("familyname", familyName).getResultList();
        List<Patient> patients = new ArrayList();
        
        for( Person p : persons ){
            if( p.getPatientList().size() > 0 )
                patients.add( p.getPatientList().get(0) );
        } 
        
        return patients;
    }*/
    
}