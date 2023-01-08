package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.entities.Occurrence;

import javax.ejb.Stateless;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Stateless
public class BlobBean {

    public void transformDocumentsIntoBlob(Occurrence occurrence) {
        // Transform Occurrence Documents into a Blob to save DB space
        try {
            List<Document> documents = occurrence.getDocuments();

            // Serialize the List of Document objects into a BLOB
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(documents);
            oos.close();

            byte[] data = baos.toByteArray();
            Blob blob = new SerialBlob(data);

            // Deserialize the BLOB back into a List of Document objects
            byte[] blobData = blob.getBytes(1, (int) blob.length());

            ByteArrayInputStream bais = new ByteArrayInputStream(blobData);
            ObjectInputStream ois = new ObjectInputStream(bais);

            List<Document> deserializedDocuments = (List<Document>) ois.readObject();

            // Set the documents field of the occurrence object to the List of Document objects
            occurrence.setDocuments(deserializedDocuments);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
