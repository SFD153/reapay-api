package com.mouni.reapay.services;

import com.mouni.reapay.models.PdfRequest;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PdfService {

    private MongoTemplate mongoTemplate;

    private AwsService awsService;

    private ExecutorService threadPool;

    @Value("${upload.threads}")
    private String threads;

    @PostConstruct
    public void init() {
        getThreadPool();
    }

    private ExecutorService getThreadPool() {
        threadPool = Executors.newFixedThreadPool(Integer.parseInt(threads));
        return threadPool;
    }

    @Autowired
    public PdfService(MongoTemplate mongoTemplate, AwsService awsService) {
        this.mongoTemplate = mongoTemplate;
        this.awsService = awsService;
    }

    public void createPdf(PdfRequest pdfRequest) {
        Thread th = new PdfService.PdfAsync(pdfRequest);
        threadPool.submit(th);
    }

    class PdfAsync extends Thread {

        private final String TEMPLATE_PATH_PDF = "src/main/resources/PdfTransactionTemplate.pdf";

        private final String PATH_PARTIAL_PDF = "src/main/resources/";

        private final String PATH_FULL_PDF;

        private final String FILE_NAME;

        private final String EXTENSION = ".pdf";

        private final String transactionId;

        private final String closerCompanyName;

        private final String closerCompanyAddress;

        private final String closerName;

        private final String closerEmail;

        private final String closerPhone;

        private final String closingDate;

        private final String realtorName;

        private final String brokerCompanyName;

        private final String mls;

        private final String propertyAddress;

        private final String firstParagraph;

        private final String secondParagraph;

        private final String thirdParagraph;

        private final String fourthParagraph;

        private final String fifthParagraph;

        public PdfAsync(PdfRequest pdfRequest) {
            this.transactionId = pdfRequest.getTransactionId();
            this.closerCompanyName = pdfRequest.getCloserCompanyName();
            this.closerCompanyAddress = pdfRequest.getCloserCompanyAddress();
            this.closerName = pdfRequest.getCloserName();
            this.closerEmail = pdfRequest.getCloserEmail();
            this.closerPhone = pdfRequest.getCloserPhone();
            this.closingDate = pdfRequest.getClosingDate();
            this.realtorName = pdfRequest.getRealtorName();
            this.brokerCompanyName = pdfRequest.getBrokerCompanyName();
            this.mls = pdfRequest.getMls();
            this.propertyAddress = pdfRequest.getPropertyAddress();
            this.firstParagraph = pdfRequest.getFirstParagraph();
            this.secondParagraph = pdfRequest.getSecondParagraph();
            this.thirdParagraph = pdfRequest.getThirdParagraph();
            this.fourthParagraph = pdfRequest.getFourthParagraph();
            this.fifthParagraph = pdfRequest.getFifthParagraph();
            this.FILE_NAME = "PdfTransactionResult" + transactionId;
            this.PATH_FULL_PDF = FILE_NAME + EXTENSION;
            System.out.println("Am ajuns Aici 1");
        }

        @SneakyThrows
        @Override
        public void run() {
            PDDocument pDDocument = PDDocument.load(new File("PdfTransactionTemplate.pdf"));
            PDAcroForm pDAcroForm = pDDocument.getDocumentCatalog().getAcroForm();
            pDAcroForm.getField("transactionId").setValue(transactionId);
            pDAcroForm.getField("transactionId").setReadOnly(true);
            pDAcroForm.getField("closerCompanyName").setValue(closerCompanyName);
            pDAcroForm.getField("closerCompanyName").setReadOnly(true);
            pDAcroForm.getField("closerCompanyAddress").setValue(closerCompanyAddress);
            pDAcroForm.getField("closerCompanyAddress").setReadOnly(true);
            pDAcroForm.getField("closerName").setValue(closerName);
            pDAcroForm.getField("closerName").setReadOnly(true);
            pDAcroForm.getField("closerEmail").setValue(closerEmail);
            pDAcroForm.getField("closerEmail").setReadOnly(true);
            pDAcroForm.getField("closerPhone").setValue(closerPhone);
            pDAcroForm.getField("closerPhone").setReadOnly(true);
            pDAcroForm.getField("closingDate").setValue(closingDate);
            pDAcroForm.getField("closingDate").setReadOnly(true);
            pDAcroForm.getField("realtorName").setValue(realtorName);
            pDAcroForm.getField("realtorName").setReadOnly(true);
            pDAcroForm.getField("brokerCompanyName").setValue(brokerCompanyName);
            pDAcroForm.getField("brokerCompanyName").setReadOnly(true);
            pDAcroForm.getField("mls").setValue(mls);
            pDAcroForm.getField("mls").setReadOnly(true);
            pDAcroForm.getField("propertyAddress").setValue(propertyAddress);
            pDAcroForm.getField("propertyAddress").setReadOnly(true);
            pDAcroForm.getField("firstParagraph").setValue(firstParagraph);
            pDAcroForm.getField("firstParagraph").setReadOnly(true);
            pDAcroForm.getField("secondParagraph").setValue(secondParagraph);
            pDAcroForm.getField("secondParagraph").setReadOnly(true);
            pDAcroForm.getField("thirdParagraph").setValue(thirdParagraph);
            pDAcroForm.getField("thirdParagraph").setReadOnly(true);
            pDAcroForm.getField("fourthParagraph").setValue(fourthParagraph);
            pDAcroForm.getField("fourthParagraph").setReadOnly(true);
            pDAcroForm.getField("fifthParagraph").setValue(fifthParagraph);
            pDAcroForm.getField("fifthParagraph").setReadOnly(true);
            System.out.println("Am ajuns Aici 4");
            pDDocument.save(PATH_FULL_PDF);
            System.out.println("Am ajuns Aici 5");

            pDDocument.close();
            awsService.uploadTransactionPdfS3(transactionId, FILE_NAME + EXTENSION, PATH_FULL_PDF);
        }
    }
}
