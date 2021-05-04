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
import model.Doctor;

/**
 *
 * @author Elise
 */
public class DoctorJpaController implements Serializable {

    public DoctorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Doctor doctor) {
        if (doctor.getImageList() == null) {
            doctor.setImageList(new ArrayList<Image>());
        }
        if (doctor.getFileList() == null) {
            doctor.setFileList(new ArrayList<File>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person idperson = doctor.getIdperson();
            if (idperson != null) {
                idperson = em.getReference(idperson.getClass(), idperson.getIdperson());
                doctor.setIdperson(idperson);
            }
            List<Image> attachedImageList = new ArrayList<Image>();
            for (Image imageListImageToAttach : doctor.getImageList()) {
                imageListImageToAttach = em.getReference(imageListImageToAttach.getClass(), imageListImageToAttach.getIdimage());
                attachedImageList.add(imageListImageToAttach);
            }
            doctor.setImageList(attachedImageList);
            List<File> attachedFileList = new ArrayList<File>();
            for (File fileListFileToAttach : doctor.getFileList()) {
                fileListFileToAttach = em.getReference(fileListFileToAttach.getClass(), fileListFileToAttach.getIdfile());
                attachedFileList.add(fileListFileToAttach);
            }
            doctor.setFileList(attachedFileList);
            em.persist(doctor);
            if (idperson != null) {
                idperson.getDoctorList().add(doctor);
                idperson = em.merge(idperson);
            }
            for (Image imageListImage : doctor.getImageList()) {
                Doctor oldIddoctorOfImageListImage = imageListImage.getIddoctor();
                imageListImage.setIddoctor(doctor);
                imageListImage = em.merge(imageListImage);
                if (oldIddoctorOfImageListImage != null) {
                    oldIddoctorOfImageListImage.getImageList().remove(imageListImage);
                    oldIddoctorOfImageListImage = em.merge(oldIddoctorOfImageListImage);
                }
            }
            for (File fileListAppointment : doctor.getFileList()) {
                Doctor oldIddoctorOfFileListFile = fileListFile.getIddoctor();
                fileListAppointment.setIddoctor(doctor);
                fileListAppointment = em.merge(fileListFile);
                if (oldIddoctorOfFileListFile != null) {
                    oldIddoctorOfFileListFile.getFileList().remove(fileListFile);
                    oldIddoctorOfFileListFile = em.merge(oldIddoctorOfFileListFile);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Doctor doctor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doctor persistentDoctor = em.find(Doctor.class, doctor.getIddoctor());
            Person idpersonOld = persistentDoctor.getIdperson();
            Person idpersonNew = doctor.getIdperson();
            List<Image> imageListOld = persistentDoctor.getImageList();
            List<Image> imageListNew = doctor.getImageList();
            List<File> fileListOld = persistentDoctor.getFileList();
            List<File> fileListNew = doctor.getFileList();
            List<String> illegalOrphanMessages = null;
            for (File fileListOldFile : fileListOld) {
                if (!fileListNew.contains(fileListOldFile)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain File " + fileListOldFile + " since its iddoctor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idpersonNew != null) {
                idpersonNew = em.getReference(idpersonNew.getClass(), idpersonNew.getIdperson());
                doctor.setIdperson(idpersonNew);
            }
            List<Image> attachedImageListNew = new ArrayList<Image>();
            for (Image imageListNewImageToAttach : imageListNew) {
                imageListNewImageToAttach = em.getReference(imageListNewImageToAttach.getClass(), imageListNewImageToAttach.getIdimage());
                attachedImageListNew.add(imageListNewImageToAttach);
            }
            imageListNew = attachedImageListNew;
            doctor.setImageList(imageListNew);
            List<File> attachedFileListNew = new ArrayList<File>();
            for (File fileListNewFileToAttach : fileListNew) {
                fileListNewFileToAttach = em.getReference(fileListNewFileToAttach.getClass(), fileListNewFileToAttach.getIdfile());
                attachedFileListNew.add(fileListNewFileToAttach);
            }
            fileListNew = attachedFileListNew;
            doctor.setAppointmentList(fileListNew);
            doctor = em.merge(doctor);
            if (idpersonOld != null && !idpersonOld.equals(idpersonNew)) {
                idpersonOld.getDoctorList().remove(doctor);
                idpersonOld = em.merge(idpersonOld);
            }
            if (idpersonNew != null && !idpersonNew.equals(idpersonOld)) {
                idpersonNew.getDoctorList().add(doctor);
                idpersonNew = em.merge(idpersonNew);
            }
            for (Image imageListOldImage : imageListOld) {
                if (!imageListNew.contains(imageListOldImage)) {
                    imageListOldImage.setIddoctor(null);
                    imageListOldImage = em.merge(imageListOldImage);
                }
            }
            for (Image imageListNewImage : imageListNew) {
                if (!imageListOld.contains(imageListNewImage)) {
                    Doctor oldIddoctorOfImageListNewImage = imageListNewImage.getIddoctor();
                    imageListNewImage.setIddoctor(doctor);
                    imageListNewImage = em.merge(imageListNewImage);
                    if (oldIddoctorOfImageListNewImage != null && !oldIddoctorOfImageListNewImage.equals(doctor)) {
                        oldIddoctorOfImageListNewImage.getImageList().remove(imageListNewImage);
                        oldIddoctorOfImageListNewImage = em.merge(oldIddoctorOfImageListNewImage);
                    }
                }
            }
            for (File fileListNewFile : fileListNew) {
                if (!fileListOld.contains(fileListNewFile)) {
                    Doctor oldIddoctorOfFileListNewFile= fileListNewFile.getIddoctor();
                    fileListNewFile.setIddoctor(doctor);
                    fileListNewFile = em.merge(fileListNewFile);
                    if (oldIddoctorOfFileListNewFile != null && !oldIddoctorOfFileListNewFile.equals(doctor)) {
                        oldIddoctorOfFileListNewFile.getFileList().remove(fileListNewFile);
                        oldIddoctorOfFileListNewFile = em.merge(oldIddoctorOfFileListNewFile);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = doctor.getIddoctor();
                if (findDoctor(id) == null) {
                    throw new NonexistentEntityException("The doctor with id " + id + " no longer exists.");
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
            Doctor doctor;
            try {
                doctor = em.getReference(Doctor.class, id);
                doctor.getIddoctor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The doctor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<File> fileListOrphanCheck = doctor.getFileList();
            for (File fileListOrphanCheckFile : fileListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Doctor (" + doctor + ") cannot be destroyed since the File " + fileListOrphanCheckFile + " in its fileList field has a non-nullable iddoctor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Person idperson = doctor.getIdperson();
            if (idperson != null) {
                idperson.getDoctorList().remove(doctor);
                idperson = em.merge(idperson);
            }
            List<Image> imageList = doctor.getImageList();
            for (Image imageListImage : imageList) {
                imageListImage.setIddoctor(null);
                imageListImage = em.merge(imageListImage);
            }
            em.remove(doctor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Doctor> findDoctorEntities() {
        return findDoctorEntities(true, -1, -1);
    }

    public List<Doctor> findDoctorEntities(int maxResults, int firstResult) {
        return findDoctorEntities(false, maxResults, firstResult);
    }

    private List<Doctor> findDoctorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Doctor.class));
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

    public Doctor findDoctor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doctor.class, id);
        } finally {
            em.close();
        }
    }

    public int getDoctorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Doctor> rt = cq.from(Doctor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}