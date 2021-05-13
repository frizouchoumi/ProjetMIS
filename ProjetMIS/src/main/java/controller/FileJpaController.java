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
import model.Doctor;
import model.Patient;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import controller.exceptions.NonexistentEntityException;
import model.File;

/**
 *
 * @author Elise
 */
public class FileJpaController implements Serializable {

    public FileJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(File file) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doctor iddoctor = file.getIddoctor();
            if (iddoctor != null) {
                iddoctor = em.getReference(iddoctor.getClass(), iddoctor.getIddoctor());
                file.setIddoctor(iddoctor);
            }
            Patient idpatient = file.getIdpatient();
            if (idpatient != null) {
                idpatient = em.getReference(idpatient.getClass(), idpatient.getIdpatient());
                file.setIdpatient(idpatient);
            }
            em.persist(file);
            if (iddoctor != null) {
                iddoctor.getFileList().add(file);
                iddoctor = em.merge(iddoctor);
            }
            if (idpatient != null) {
                idpatient.getFileList().add(file);
                idpatient = em.merge(idpatient);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(File file) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            File persistentFile = em.find(File.class, file.getIdfile());
            Doctor iddoctorOld = persistentFile.getIddoctor();
            Doctor iddoctorNew = file.getIddoctor();
            Patient idpatientOld = persistentFile.getIdpatient();
            Patient idpatientNew = file.getIdpatient();
            if (iddoctorNew != null) {
                iddoctorNew = em.getReference(iddoctorNew.getClass(), iddoctorNew.getIddoctor());
                file.setIddoctor(iddoctorNew);
            }
            if (idpatientNew != null) {
                idpatientNew = em.getReference(idpatientNew.getClass(), idpatientNew.getIdpatient());
                file.setIdpatient(idpatientNew);
            }
            file = em.merge(file);
            if (iddoctorOld != null && !iddoctorOld.equals(iddoctorNew)) {
                iddoctorOld.getFileList().remove(file);
                iddoctorOld = em.merge(iddoctorOld);
            }
            if (iddoctorNew != null && !iddoctorNew.equals(iddoctorOld)) {
                iddoctorNew.getFileList().add(file);
                iddoctorNew = em.merge(iddoctorNew);
            }
            if (idpatientOld != null && !idpatientOld.equals(idpatientNew)) {
                idpatientOld.getFileList().remove(file);
                idpatientOld = em.merge(idpatientOld);
            }
            if (idpatientNew != null && !idpatientNew.equals(idpatientOld)) {
                idpatientNew.getFileList().add(file);
                idpatientNew = em.merge(idpatientNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = file.getIdfile();
                if (findFile(id) == null) {
                    throw new NonexistentEntityException("The appointment with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            File file;
            try {
                file = em.getReference(File.class, id);
                file.getIdfile();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The file with id " + id + " no longer exists.", enfe);
            }
            Doctor iddoctor = file.getIddoctor();
            if (iddoctor != null) {
                iddoctor.getFileList().remove(file);
                iddoctor = em.merge(iddoctor);
            }
            Patient idpatient = file.getIdpatient();
            if (idpatient != null) {
                idpatient.getFileList().remove(file);
                idpatient = em.merge(idpatient);
            }
            em.remove(file);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<File> findFileEntities() {
        return findFileEntities(true, -1, -1);
    }

    public List<File> findFileEntities(int maxResults, int firstResult) {
        return findFileEntities(false, maxResults, firstResult);
    }

    private List<File> findFileEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(File.class));
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

    public File findFile(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(File.class, id);
        } finally {
            em.close();
        }
    }

    public int getFileCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<File> rt = cq.from(File.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}