package ipleiria.dae.project.ws;

import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.entities.Student;
import ipleiria.dae.project.exceptions.MyConstraintViolationException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import ipleiria.dae.project.ejbs.DocumentBean;
import ipleiria.dae.project.ejbs.StudentBean;
import ipleiria.dae.project.security.Authenticated;

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

// imports ...
@Path("documents")
@Authenticated
@RolesAllowed({"Student", "Administrator"})
public class DocumentService {
    @EJB
    private StudentBean studentBean;
    @EJB
    private DocumentBean documentBean;
    @Context
    private SecurityContext securityContext;

    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(MultipartFormDataInput input) throws IOException, MyConstraintViolationException, MyEntityNotFoundException {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        String username = securityContext.getUserPrincipal().getName();
        List<InputPart> inputParts = uploadForm.get("file");
        List<Document> documents = new LinkedList<Document>();
        for (InputPart inputPart : inputParts) {
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String filename = getFilename(headers);
            // convert the uploaded file to inputstream
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String homedir = System.getProperty("user.home");
            String dirpath = homedir + File.separator + "uploads" + File.separator +
                    username;
            mkdirIfNotExists(dirpath);
            String filepath = dirpath + File.separator + filename;
            writeFile(bytes, filepath);
            Document document = documentBean.create(filepath, filename, username);
            documents.add(document);
        }
        return Response.ok(documents).build();
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
        Document document = documentBean.find(id);
        // TODO: if document is null, throw MyEntityNotFoundException ...
        // TODO: Can I download this? - Security Breach here! How do you fix it?
        Response.ResponseBuilder response = Response.ok(new File(document.getFilepath()));
        response.header("Content-Disposition", "attachment;filename=" +
                document.getFilename());
        return response.build();
    }
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocuments() throws MyEntityNotFoundException {
        String username = securityContext.getUserPrincipal().getName();
        List<Document> documents = documentBean.getStudentDocuments(username);
        return Response.ok(documents).build();
    }
    @GET
    @Path("exists")
    public Response hasDocuments() {
        String username = securityContext.getUserPrincipal().getName();
        Student student = studentBean.find(username);
        // TODO: if user is null, throw MyEntityNotFoundException ...
        return Response.status(Response.Status.OK)
                .entity(! student.getDocuments().isEmpty())
                .build();
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
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }
}
