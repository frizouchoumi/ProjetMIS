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
import model.Image;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import controller.exceptions.NonexistentEntityException;
import model.File;
import model.Note;

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
        if (file.getImageList() == null) {
            file.setImageList(new ArrayList<Image>());
        }
        if (file.getNoteList() == null) {
            file.setNoteList(new ArrayList<Note>());
        }
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
            List<Image> attachedImageList = new ArrayList<Image>();
            for (Image imageListImageToAttach : file.getImageList()) {
                imageListImageToAttach = em.getReference(imageListImageToAttach.getClass(), imageListImageToAttach.getIdimage());
                attachedImageList.add(imageListImageToAttach);
            }
            file.setImageList(attachedImageList);
            List<Note> attachedNoteList = new ArrayList<Note>();
            for (Note noteListNoteToAttach : file.getNoteList()) {
                noteListNoteToAttach = em.getReference(noteListNoteToAttach.getClass(), noteListNoteToAttach.getIdnote());
                attachedNoteList.add(noteListNoteToAttach);
            }
            file.setNoteList(attachedNoteList);
            em.persist(file);
            if (iddoctor != null) {
                iddoctor.getFileList().add(file);
                iddoctor = em.merge(iddoctor);
            }
            if (idpatient != null) {
                idpatient.getFileList().add(file);
                idpatient = em.merge(idpatient);
            }
            for (Image imageListImage : file.getImageList()) {
                File oldIdfileOfImageListImage = imageListImage.getIdfile();
                imageListImage.setIdfile(file);
                imageListImage = em.merge(imageListImage);
                if (oldIdfileOfImageListImage != null) {
                    oldIdfiletOfImageListImage.getImageList().remove(imageListImage);
                    oldIdfileOfImageListImage = em.merge(oldIdfileOfImageListImage);
                }
            }
            for (Note noteListNote : file.getNoteList()) {
                File oldIdfileOfNoteListNote = noteListNote.getIdfile();
                noteListNote.setIdfile(file);
                noteListNote = em.merge(noteListNote);
                if (oldIdfileOfNoteListNote != null) {
                    oldIdfileOfNoteListNote.getNoteList().remove(noteListNote);
                    oldIdfileOfNoteListNote = em.merge(oldIdfileOfNoteListNote);
                }
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
            List<Image> imageListOld = persistentFile.getImageList();
            List<Image> imageListNew = file.getImageList();
            List<Note> noteListOld = persistentFile.getNoteList();
            List<Note> noteListNew = file.getNoteList();
            if (iddoctorNew != null) {
                iddoctorNew = em.getReference(iddoctorNew.getClass(), iddoctorNew.getIddoctor());
                file.setIddoctor(iddoctorNew);
            }
            if (idpatientNew != null) {
                idpatientNew = em.getReference(idpatientNew.getClass(), idpatientNew.getIdpatient());
                file.setIdpatient(idpatientNew);
            }
            List<Image> attachedImageListNew = new ArrayList<Image>();
            for (Image imageListNewImageToAttach : imageListNew) {
                imageListNewImageToAttach = em.getReference(imageListNewImageToAttach.getClass(), imageListNewImageToAttach.getIdimage());
                attachedImageListNew.add(imageListNewImageToAttach);
            }
            imageListNew = attachedImageListNew;
            file.setImageList(imageListNew);
            List<Note> attachedNoteListNew = new ArrayList<Note>();
            for (Note noteListNewNoteToAttach : noteListNew) {
                noteListNewNoteToAttach = em.getReference(noteListNewNoteToAttach.getClass(), noteListNewNoteToAttach.getIdnote());
                attachedNoteListNew.add(noteListNewNoteToAttach);
            }
            noteListNew = attachedNoteListNew;
            file.setNoteList(noteListNew);
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
            for (Image imageListOldImage : imageListOld) {
                if (!imageListNew.contains(imageListOldImage)) {
                    imageListOldImage.setIdfile(null);
                    imageListOldImage = em.merge(imageListOldImage);
                }
            }
            for (Image imageListNewImage : imageListNew) {
                if (!imageListOld.contains(imageListNewImage)) {
                    File oldIdfileOfImageListNewImage = imageListNewImage.getIdfile();
                    imageListNewImage.setIdfile(file);
                    imageListNewImage = em.merge(imageListNewImage);
                    if (oldIdfileOfImageListNewImage != null && !oldFiletOfImageListNewImage.equals(file)) {
                        oldIdfileOfImageListNewImage.getImageList().remove(imageListNewImage);
                        oldIdfileOfImageListNewImage = em.merge(oldIdfileOfImageListNewImage);
                    }
                }
            }
            for (Note noteListOldNote : noteListOld) {
                if (!noteListNew.contains(noteListOldNote)) {
                    noteListOldNote.setIdfile(null);
                    noteListOldNote = em.merge(noteListOldNote);
                }
            }
            for (Note noteListNewNote : noteListNew) {
                if (!noteListOld.contains(noteListNewNote)) {
                    File oldFileOfNoteListNewNote = noteListNewNote.getFile();
                    noteListNewNote.setIdfile(file);
                    noteListNewNote = em.merge(noteListNewNote);
                    if (oldIdfileOfNoteListNewNote != null && !oldIdfileOfNoteListNewNote.equals(file)) {
                        oldIdfileOfNoteListNewNote.getNoteList().remove(noteListNewNote);
                        oldIdfileOfNoteListNewNote = em.merge(oldIdfileOfNoteListNewNote);
                    }
                }
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
                throw new NonexistentEntityException("The appointment with id " + id + " no longer exists.", enfe);
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
            List<Image> imageList = file.getImageList();
            for (Image imageListImage : imageList) {
                imageListImage.setIdfile(null);
                imageListImage = em.merge(imageListImage);
            }
            List<Note> noteList = file.getNoteList();
            for (Note noteListNote : noteList) {
                noteListNote.setIdfile(null);
                noteListNote = em.merge(noteListNote);
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