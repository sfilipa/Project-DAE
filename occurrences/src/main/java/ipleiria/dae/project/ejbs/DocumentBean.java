package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.entities.Repairer;
import ipleiria.dae.project.enumerators.CoverageType;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.exceptions.MyConstraintViolationException;
import ipleiria.dae.project.exceptions.MyEntityCreationViolationException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.exceptions.mappers.MyEntityCreationViolationMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class DocumentBean {
    @EJB
    private OccurrenceBean occurrenceBean;
    @EJB
    private RepairerBean repairerBean;
    @EJB
    private ExpertBean expertBean;
    @PersistenceContext
    private EntityManager em;

    public Document create(String filepath, String filename, long occurenceId) {
        var occurence = occurrenceBean.find(occurenceId);
        if (occurence == null) {
            throw new MyEntityNotFoundException("Occurrence not found");
        }
        var document = new Document(filepath, filename, occurence);

        em.persist(document);
        occurence.addDocument(document);

        return document;
    }

    public Document createWithoutOccurrence(String filepath, String filename) {
        var document = new Document(filepath, filename);

        em.persist(document);

        return document;
    }

    public Document find(Long id) {
        return em.find(Document.class, id);
    }

    public Document findOrFail(Long id) {
        var document = em.getReference(Document.class, id);
        Hibernate.initialize(document);
        return document;
    }

    public List<Document> getOccurenceDocuments(long occurenceId) {
        return em.createNamedQuery("getOccurenceDocuments", Document.class).setParameter("id", occurenceId).getResultList();
    }

    public Boolean seeIfOccurrencyHasDocuments(long occurenceId) {
        var occurence = occurrenceBean.find(occurenceId);
        if (occurence == null) {
            throw new MyEntityNotFoundException("Occurrence not found");
        }

        return occurence.getDocuments().size() > 0;
    }

    public int readCSVFile(String filepath) {
        List<List<String>> records = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine && line.contains("usernameClient")) {
                    lineNumber=1;
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(";");
                records.add(Arrays.asList(values));
            }

            if (records.isEmpty()) {
                throw new MyEntityCreationViolationException("File is empty");
            }
        } catch (Exception e) {
            throw new MyConstraintViolationException((ConstraintViolationException) e);
        }

        createOccurrence(records, lineNumber);
        return records.size();
    }

    public int readExcel(String filepath) throws IOException {
        FileInputStream file = new FileInputStream(new File(filepath));
        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();
        int lineNumber = 0;
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        data.get(i).add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        data.get(i).add(formatter.format(cell.getDateCellValue()));
                        break;
                    case BOOLEAN:
                        data.get(i).add(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    case FORMULA:
                        break;
                    default:
                        data.get(i).add(" ");
                }
            }
            i++;
        }
        if (data.isEmpty()) {
            throw new MyEntityCreationViolationException("Excel file is empty");
        }
        if (data.get(0).contains("usernameClient")) {
            lineNumber = 1;
            data.remove(0);
        }
        createOccurrence(new ArrayList<>(data.values()), lineNumber);
        return data.size();
    }

    private void createOccurrence(List<List<String>> records, int lineNumber) {
        for (List<String> string : records) {
            lineNumber++;
            if (string.size() != 9) {
                throw new MyEntityCreationViolationException("File is not in the correct format should have 9 columns and has " + string.size());
            }
            String username = string.get(0);
            String entryDate = string.get(1);
            String finalDate = string.get(2);
            String stateString = string.get(3).toUpperCase();
            String insuranceCode = string.get(4);
            String coverageTypeString = string.get(5).toUpperCase();
            String description = string.get(6);
            String usernameRepairer = string.get(7);
            String expertsList = string.get(8);

            CoverageType coverageType = null;
            State state = null;

            try {
                state = State.valueOf(stateString);
                validateOccurrenceWithState(state, finalDate, usernameRepairer, expertsList, lineNumber);
            } catch (IllegalArgumentException e) {
                throw new MyEntityNotFoundException("State in line " + lineNumber + " not found.");
            }
            try {
                coverageType = CoverageType.valueOf(coverageTypeString);
            } catch (IllegalArgumentException e) {
                throw new MyEntityNotFoundException("Coverage Type in line " + lineNumber + " not found.");
            }

            Occurrence occurrence = occurrenceBean.create(username, entryDate, state, insuranceCode, coverageType, description);

            if (!finalDate.trim().equals("-")) {
                occurrence.setFinalDate(finalDate);
            }
            if (!usernameRepairer.trim().equals("-")) {
                Repairer repairer = repairerBean.find(usernameRepairer);
                if (repairer == null) {
                    throw new MyEntityNotFoundException("Repairer in the line " + lineNumber + " not found");
                }
                occurrence.setRepairer(repairer);
            }

            if (!expertsList.trim().equals("-")) {
                for (String expertUsername : expertsList.split(",")) {
                    Expert expert = expertBean.find(expertUsername.trim());
                    if (expert == null) {
                        throw new MyEntityNotFoundException("Expert with username: " + expertUsername + "in line " + lineNumber + " not found");
                    }
                    if (!expert.getInsuranceCompany().equals(occurrence.getInsurance().getInsuranceCompany())) {
                        throw new MyEntityCreationViolationException("Expert and Occurrence are not from the same company");
                    }
                    occurrence.addExpert(expert);
                    expert.addOccurrence(occurrence);
                }
            }
        }
    }

    private void validateOccurrenceWithState(State state, String finalDate, String usernameRepairer, String expertsList, int lineNumber) {
        if (state.equals(State.PENDING)) {
            if (!finalDate.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Final Date in line " + lineNumber + " must be empty");
            }
            if (!usernameRepairer.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Repairer in line " + lineNumber + " must be empty");
            }
            if (!expertsList.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Experts in line " + lineNumber + " must be empty");
            }
        } else if (state.equals(State.APPROVED) || state.equals(State.DISAPPROVED)) {
            if (!finalDate.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Final Date in line " + lineNumber + " must be empty");
            }
            if (!usernameRepairer.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Repairer in line " + lineNumber + " must be empty");
            }
            if (expertsList.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Experts in line " + lineNumber + " must not be empty");
            }
        } else if (state.equals(State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT) || state.equals(State.REPAIRER_WAITING_LIST) || state.equals(State.ACTIVE) || state.equals(State.FAILED)) {
            if (!finalDate.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Final Date in line " + lineNumber + " must be empty");
            }
            if (usernameRepairer.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Repairer in line " + lineNumber + " must not be empty");
            }
            if (expertsList.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Experts in line " + lineNumber + " must not be empty");
            }
        } else if (state.equals(State.RESOLVED)) {
            if (finalDate.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Final Date in line " + lineNumber + " must not be empty");
            }
            if (usernameRepairer.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Repairer in line " + lineNumber + " must not be empty");
            }
            if (expertsList.trim().equals("-")) {
                throw new MyEntityCreationViolationException("Experts in line " + lineNumber + " must not be empty");
            }
        }
    }
}
