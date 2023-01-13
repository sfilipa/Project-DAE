package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.entities.Occurrence;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Stateless
public class ZipFilesBean {
    @PersistenceContext
    EntityManager entityManager;
    @EJB
    private DocumentBean documentBean;

    public void compressDocuments(Occurrence occurrence) throws IOException {
        // Get the List of Document objects from the occurrence object
        List<Document> documents = occurrence.getDocuments();
        for (Document document : documents) {

            String filepath = document.getFilepath();
            String filename = document.getFilename();

            String zipFilename = filename.lastIndexOf(".") > 0 ? filename.substring(0, filename.lastIndexOf(".")) + ".zip" : filename + ".zip";

            String homedir = System.getProperty("user.home");
            String dirpath = homedir + File.separator + "uploads" + File.separator + "zippedFiles" + File.separator + occurrence.getId();
            mkdirIfNotExists(dirpath);

            String filepathZip = dirpath + File.separator + zipFilename;

            byte[] buffer = new byte[1024];
            writeFile(buffer, filepathZip);

            //Create file input stream
            FileInputStream fileInputStream = new FileInputStream(filepath);

            //Create a file output stream
            FileOutputStream fileOutputStream = new FileOutputStream(filepathZip);

            //Create a zip output stream
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            // Create a new Zip entry
            ZipEntry entry = new ZipEntry(document.getFilename());
            zipOutputStream.putNextEntry(entry);

            // Read data from the source file and write it to the zip file
            int len;
            while ((len = fileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }

            // Close the streams
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            fileInputStream.close();
            fileOutputStream.close();

            File file = new File(filepath);
            if (file.delete()) {
                System.out.println("File deleted successfully");
            } else {
                System.out.println("Failed to delete the file");
            }
            documentBean.update(document.getId(), filepathZip, zipFilename, occurrence);
        }
    }

    private void mkdirIfNotExists(String path) {
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void writeFile(byte[] content, String filename) throws IOException {
        var file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();

        System.out.println("Written: " + filename);
    }

}
