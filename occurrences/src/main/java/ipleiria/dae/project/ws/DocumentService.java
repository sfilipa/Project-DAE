package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.DocumentDTO;
import ipleiria.dae.project.ejbs.DocumentBean;
import ipleiria.dae.project.ejbs.OccurrenceBean;
import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.security.Authenticated;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Path("documents")
@Authenticated
public class DocumentService {
    @EJB
    private OccurrenceBean occurrenceBean;

    @EJB
    private DocumentBean documentBean;

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/{occurrenceId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(@PathParam("occurrenceId") long occurrenceId, MultipartFormDataInput input) throws IOException {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        List<InputPart> inputParts = uploadForm.get("file");
        var documents = new LinkedList<Document>();

        for (InputPart inputPart : inputParts) {
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String filename = getFilename(headers);

            // convert the uploaded file to inputstream
            InputStream inputStream = inputPart.getBody(InputStream.class, null);

            byte[] bytes = IOUtils.toByteArray(inputStream);

            String homedir = System.getProperty("user.home");
            String dirpath = homedir + File.separator + "uploads" + File.separator + occurrenceId;
            mkdirIfNotExists(dirpath);

            String filepath =  dirpath + File.separator + filename;
            writeFile(bytes, filepath);

            var document = documentBean.create(filepath, filename, occurrenceId);
            documents.add(document);
        }

        return Response.ok(DocumentDTO.from(documents)).build();
    }

    private void mkdirIfNotExists(String path) {
        File file = new File(path);

        if (! file.exists()) {
            file.mkdirs();
        }
    }

    @GET
    @Path("download/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("id") Long id) {
        var document = documentBean.find(id);
        if (document == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var response = Response.ok(new File(document.getFilepath()));

        response.header("Content-Disposition", "attachment;filename=" + document.getFilename());

        return response.build();
    }

    @GET
    @Path("/{occurrenceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocuments(@PathParam("occurrenceId") long occurrenceId) {
        var documents = documentBean.getOccurenceDocuments(occurrenceId);
        return Response.ok(DocumentDTO.from(documents)).build();
    }

    @GET
    @Path("/{occurrenceId}/exists")
    public Response hasDocuments(@PathParam("occurrenceId") long occurrenceId) {
        Boolean hasDocuments = documentBean.seeIfOccurrencyHasDocuments(occurrenceId);

        return Response.status(Response.Status.OK).entity(hasDocuments).build();
    }

    private String getFilename(MultivaluedMap<String, String> headers) {
        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                return name[1].trim().replaceAll("\"", "");
            }
        }

        return "unknown";
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
