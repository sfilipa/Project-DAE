package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.entities.Occurrence;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Stateless
public class BlobBean {
    @PersistenceContext
    EntityManager entityManager;

    public Blob convertDocumentsToBlob(Occurrence occurrence) {
        try {
            // Get the List of Document objects from the occurrence object
            List<Document> documents = occurrence.getDocuments();

            // Create a ByteArrayOutputStream to write the list of documents to
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Create an ObjectOutputStream to write the list of documents to the ByteArrayOutputStream
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            // Write the list of documents to the ObjectOutputStream
            oos.writeObject(documents);
            oos.close();

            // Get the byte array from the ByteArrayOutputStream
            byte[] data = baos.toByteArray();

            // Create a new SerialBlob from the byte array
            return new SerialBlob(data);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void storeOccurrenceInDb(Occurrence occurrence) {
        // Convert the documents attribute to a Blob
        Blob documentsBlob = convertDocumentsToBlob(occurrence);

        // Set the documentsBlob attribute of the occurrence object to the Blob
        occurrence.setDocumentsBlob(documentsBlob);

        // Persist the occurrence object to the database
        entityManager.persist(occurrence);
    }

    public List<Document> deserializeDocumentsBlob(Occurrence occurrence) {
        try {
            byte[] blobData = occurrence.getDocumentsBlob().getBytes(1, (int) occurrence.getDocumentsBlob().length());
            ByteArrayInputStream bais = new ByteArrayInputStream(blobData);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (List<Document>) ois.readObject();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
