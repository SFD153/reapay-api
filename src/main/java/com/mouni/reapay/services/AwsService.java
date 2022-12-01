package com.mouni.reapay.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketAccelerateConfiguration;
import com.amazonaws.services.s3.model.BucketAccelerateStatus;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.mouni.reapay.models.RealtorClosingTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AwsService {

    private AmazonS3 awsClientS3;

    private MongoTemplate mongoTemplate;

    private ExecutorService threadPool;

    @Value("${upload.threads}")
    private String threads;

    @Value("${aws.media.bucket}")
    private String BUCKET;

    @Value("${aws.media.folder}")
    private String FOLDER;

    @Value("${aws.accessKeyId}")
    private String AWS_ACCESS_KEY_ID;

    @Value("${aws.secretKey}")
    private String AWS_SECRET_KEY;

    @Value("${aws.region}")
    private String AWS_REGION;

    @PostConstruct
    public void init() {
        getClient();
        getThreadPool();
    }

    private ExecutorService getThreadPool() {
        threadPool = Executors.newFixedThreadPool(Integer.parseInt(threads));
        return threadPool;
    }

    private AmazonS3 getClient() {
        if (!Optional.ofNullable(awsClientS3).isPresent()) {
            BasicAWSCredentials creds = new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
            awsClientS3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).withRegion(AWS_REGION).build();
        }
        awsClientS3.setBucketAccelerateConfiguration(BUCKET, new BucketAccelerateConfiguration(BucketAccelerateStatus.Enabled));
        return awsClientS3;
    }

    @Autowired
    public AwsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void downloadTransactionTemplatePdfS3() {
        try {
            S3Object o = awsClientS3.getObject(BUCKET, "pdf/PdfTransactionTemplate.pdf");
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File("PdfTransactionTemplate.pdf"));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void uploadTransactionPdfS3(String transactionId, String fileName, String filePath) {
        Thread th = new AwsService.UploadAsync(transactionId, fileName, filePath);
        threadPool.submit(th);
    }

    class UploadAsync extends Thread {
        private final String fileName;

        private final String filePath;

        private final String transactionId;

        public UploadAsync(String transactionId, String fileName, String filePath) {
            this.transactionId = transactionId;
            this.fileName = fileName;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                awsClientS3.putObject(BUCKET, FOLDER + "/" + fileName, new File(filePath));
                updateTransaction(transactionId, fileName);
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                ;
            }
        }

        private void updateTransaction(String transactionId, String fileName) {
            Update update = new Update();
            update.set("bucket", BUCKET);
            update.set("folder", FOLDER);
            update.set("pdfName", fileName);
            System.out.println("Fac Save in S3");
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(transactionId)), update, RealtorClosingTransaction.class);
        }
    }


}
