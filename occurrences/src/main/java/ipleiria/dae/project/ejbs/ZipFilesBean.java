package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.entities.Occurrence;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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
        String homedir = System.getProperty("user.home");
        String folder = homedir + File.separator + "uploads" + File.separator + occurrence.getId();

        String zipFilename = occurrence.getId() + "_anexos.zip";

        String dirpath = homedir + File.separator + "uploads" + File.separator + "zippedFiles";
        mkdirIfNotExists(dirpath);

        String filepathZip = dirpath + File.separator + zipFilename;

        //Create a file output stream
        FileOutputStream fileOutputStream = new FileOutputStream(filepathZip);

        //Create a zip output stream
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

        Path directory = Paths.get(folder);
        compressFolder(directory, zipOutputStream);

        // Close the streams
        zipOutputStream.close();
        fileOutputStream.close();

        Files.walk(directory).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        for (Document document : occurrence.getDocuments()) {
            documentBean.delete(document);
        }
        documentBean.create(filepathZip, zipFilename, occurrence.getId());
    }

    private void compressFolder(Path directory, ZipOutputStream zipOutputStream) throws IOException {
        Files.walk(directory)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        String zipFilePath = directory.relativize(path).toString();
                        ZipEntry zipEntry = new ZipEntry(zipFilePath);
                        zipOutputStream.putNextEntry(zipEntry);
                        Files.copy(path, zipOutputStream);
                        zipOutputStream.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }

    private void mkdirIfNotExists(String path) {
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
