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
import model.File;
import model.Tag;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import controller.exceptions.NonexistentEntityException;
import model.FileTags;

/**
 *
 * @author Elise
 */
public class FileTagsJpaController implements Serializable {

    public FileTagsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FileTags filetags) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            File idfile= filetags.getIdfile();
            if (idfile != null) {
                idfile= em.getReference(idfile.getClass(), idfile.getIdfile());
                filetags.setIdfile(idfile);
            }
            Tag idtag = filetags.getIdtag();
            if (idtag!= null) {
                idtag = em.getReference(idtag.getClass(), idtag.getIdtag());
                filetags.setIdtag(idtag);
            }
            em.persist(filetags);
            if (idfile != null) {
                idfile.getFileTagsList().add(filetags);
                idfile = em.merge(idfile);
            }
            if (idtag != null) {
                idtag.getFileTagsList().add(filetags);
                idtag = em.merge(idtag);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FileTags filetags) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FileTags persistentFileTags = em.find(FileTags.class, filetags.getIdfiletags());
            File idfileOld = persistentFileTags.getIdfile();
            File idfileNew = filetags.getIdfile();
            Tag idtagOld = persistentFileTags.getIdtag();
            Tag idtagNew = filetags.getIdtag();
            if (idfileNew != null) {
                idfileNew = em.getReference(idfileNew.getClass(), idfileNew.getIdfile());
                filetags.setIdfile(idfileNew);
            }
            if (idtagNew != null) {
                idtagNew = em.getReference(idtagNew.getClass(), idtagNew.getIdtag());
                filetags.setIdtag(idtagNew);
            }
            filetags = em.merge(filetags);
            if (idfileOld != null && !idfileOld.equals(idfileNew)) {
                idfileOld.getFileTagsList().remove(filetags);
                idfileOld = em.merge(idfileOld);
            }
            if (idfileNew != null && !idfileNew.equals(idfileOld)) {
                idfileNew.getFileTagsList().add(filetags);
                idfileNew = em.merge(idfileNew);
            }
            if (idtagOld != null && !idtagOld.equals(idtagNew)) {
                idtagOld.getFileTagsList().remove(filetags);
                idtagOld = em.merge(idtagOld);
            }
            if (idtagNew != null && !idtagNew.equals(idtagOld)) {
                idtagNew.getFileTagsList().add(filetags);
                idtagNew = em.merge(idtagNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = filetags.getIdfiletags();
                if (findFileTags(id) == null) {
                    throw new NonexistentEntityException("The filetags with id " + id + " no longer exists.");
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
            FileTags filetags;
            try {
                filetags = em.getReference(FileTags.class, id);
                filetags.getIdfiletags();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The filetags with id " + id + " no longer exists.", enfe);
            }
            File idfile = filetags.getIdfile();
            if (idfile!= null) {
                idfile.getFileTagsList().remove(filetags);
                idfile = em.merge(idfile);
            }
            Tag idtag= filetags.getIdtag();
            if (idtag!= null) {
                idtag.getFileTagsList().remove(filetags);
                idtag = em.merge(idtag);
            }
            em.remove(filetags);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FileTags> findFileTagsEntities() {
        return findFileTagsEntities(true, -1, -1);
    }

    public List<FileTags> findFileTagsEntities(int maxResults, int firstResult) {
        return findFileTagsEntities(false, maxResults, firstResult);
    }

    private List<FileTags> findFileTagsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FileTags.class));
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

    public FileTags findFileTags(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FileTags.class, id);
        } finally {
            em.close();
        }
    }

    public int getFileTagsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<File> rt = cq.from(FileTags.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
