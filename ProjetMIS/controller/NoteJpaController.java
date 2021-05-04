/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import controller.exceptions.NonexistentEntityException;
import model.File;
import model.Note;

/**
 *
 * @author Elise
 */
public class NoteJpaController implements Serializable {

    public NoteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Note note) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            File idfile = note.getIdfile();
            if (idfile != null) {
                idfile = em.getReference(idfile.getClass(), idfile.getIdfile());
                note.setIdfile(idfile);
            }
            em.persist(note);
            if (idfile != null) {
                idfile.getNoteList().add(note);
                idfile = em.merge(idfile);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Note note) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Note persistentNote = em.find(Note.class, note.getIdnote());
            File idfileOld = persistentNote.getIdfile();
            File idfileNew = note.getIdfile();
            if (idfileNew != null) {
                idfileNew = em.getReference(idfileNew.getClass(), idfileNew.getIdfile());
                note.setIdfile(idfileNew);
            }
            note = em.merge(note);
            if (idfileOld != null && !idfileOld.equals(idfileNew)) {
                idfileOld.getNoteList().remove(note);
                idfileOld = em.merge(idfileOld);
            }
            if (idfileNew != null && !idfileNew.equals(idfileOld)) {
                idfileNew.getNoteList().add(note);
                idfileNew = em.merge(idfileNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = note.getIdnote();
                if (findNote(id) == null) {
                    throw new NonexistentEntityException("The note with id " + id + " no longer exists.");
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
            Note note;
            try {
                note = em.getReference(Note.class, id);
                note.getIdnote();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The note with id " + id + " no longer exists.", enfe);
            }
            File idfile = note.getIdfile();
            if (idfile != null) {
                idfile.getNoteList().remove(note);
                idfile = em.merge(idfile);
            }
            em.remove(note);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Note> findNoteEntities() {
        return findNoteEntities(true, -1, -1);
    }

    public List<Note> findNoteEntities(int maxResults, int firstResult) {
        return findNoteEntities(false, maxResults, firstResult);
    }

    private List<Note> findNoteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Note.class));
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

    public Note findNote(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Note.class, id);
        } finally {
            em.close();
        }
    }

    public int getNoteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Note> rt = cq.from(Note.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}