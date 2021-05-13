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
import model.FileTags;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import model.Tag;

/**
 *
 * @author Elise
 */
public class TagJpaController implements Serializable {

    public TagJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tag tag) {
        if (tag.getFileTagsList() == null) {
            tag.setFileTagsList(new ArrayList<FileTags>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<FileTags> attachedFileTagsList = new ArrayList<FileTags>();
            for (FileTags filetagsListFileTagsToAttach : tag.getFileTagsList()) {
                filetagsListFileTagsToAttach = em.getReference(filetagsListFileTagsToAttach.getClass(), filetagsListFileTagsToAttach.getIdfiletags());
                attachedFileTagsList.add(filetagsListFileTagsToAttach);
            }
            tag.setFileTagsList(attachedFileTagsList);
            em.persist(tag);
            for (FileTags filetagsListFileTags : tag.getFileTagsList()) {
                Tag oldIdtagOfFileTagsListFileTags = filetagsListFileTags.getIdtag();
                filetagsListFileTags.setIdtag(tag);
                filetagsListFileTags = em.merge(filetagsListFileTags);
                if (oldIdtagOfFileTagsListFileTags != null) {
                    oldIdtagOfFileTagsListFileTags.getFileTagsList().remove(filetagsListFileTags);
                    oldIdtagOfFileTagsListFileTags = em.merge(oldIdtagOfFileTagsListFileTags);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tag tag) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tag persistentTag = em.find(Tag.class, tag.getIdtag());
            List<FileTags> filetagsListOld = persistentTag.getFileTagsList();
            List<FileTags> filetagsListNew = tag.getFileTagsList();
            List<String> illegalOrphanMessages = null;
            for (FileTags filetagsListOldFileTags : filetagsListOld) {
                if (!filetagsListNew.contains(filetagsListOldFileTags)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain FileTags " + filetagsListOldFileTags + " since its idtag field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<FileTags> attachedFileTagsListNew = new ArrayList<FileTags>();
            for (FileTags filetagsListNewFileTagsToAttach : filetagsListNew) {
                filetagsListNewFileTagsToAttach = em.getReference(filetagsListNewFileTagsToAttach.getClass(), filetagsListNewFileTagsToAttach.getIdfiletags());
                attachedFileTagsListNew.add(filetagsListNewFileTagsToAttach);
            }
            filetagsListNew = attachedFileTagsListNew;
            tag.setFileTagsList(filetagsListNew);
            tag = em.merge(tag);
            for (FileTags filetagsListNewFileTags : filetagsListNew) {
                if (!filetagsListOld.contains(filetagsListNewFileTags)) {
                    Tag oldIdtagOfFileTagsListNewFileTags = filetagsListNewFileTags.getIdtag();
                    filetagsListNewFileTags.setIdtag(tag);
                    filetagsListNewFileTags = em.merge(filetagsListNewFileTags);
                    if (oldIdtagOfFileTagsListNewFileTags != null && !oldIdtagOfFileTagsListNewFileTags.equals(tag)) {
                        oldIdtagOfFileTagsListNewFileTags.getFileTagsList().remove(filetagsListNewFileTags);
                        oldIdtagOfFileTagsListNewFileTags = em.merge(oldIdtagOfFileTagsListNewFileTags);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tag.getIdtag();
                if (findTag(id) == null) {
                    throw new NonexistentEntityException("The tag with id " + id + " no longer exists.");
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
            Tag tag;
            try {
                tag = em.getReference(Tag.class, id);
                tag.getIdtag();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tag with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<FileTags> filetagsListOrphanCheck = tag.getFileTagsList();
            for (FileTags filetagsListOrphanCheckFileTags : filetagsListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tag (" + tag + ") cannot be destroyed since the FileTags " + filetagsListOrphanCheckFileTags + " in its filetagsList field has a non-nullable idtag field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tag);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tag> findTagEntities() {
        return findTagEntities(true, -1, -1);
    }

    public List<Tag> findTagEntities(int maxResults, int firstResult) {
        return findTagEntities(false, maxResults, firstResult);
    }

    private List<Tag> findTagEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tag.class));
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

    public Tag findTag(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tag.class, id);
        } finally {
            em.close();
        }
    }

    public int getTagCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tag> rt = cq.from(Tag.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}