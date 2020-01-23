package com.mrityunjay.secureapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class AmazonClient {

	private AmazonS3 s3client;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	@Value("${amazonProperties.bucketName}")
	private String bucketName;
	@Value("${amazonProperties.accessKey}")
	private String accessKey;
	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	@SuppressWarnings("deprecation")
	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = new AmazonS3Client(credentials);
	}
	
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
	    File convFile = new File(file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}
	
	private String generateFileName(MultipartFile multiPart) {
	    return multiPart.getOriginalFilename().replace(" ", "_");
	}
	
	
	private void uploadFileTos3bucket(String fileName, File file) {
	    s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
	            .withCannedAcl(CannedAccessControlList.PublicRead));
	}
	
	
	
	public String uploadFile(MultipartFile multipartFile) {

	    String fileUrl = "";
	    try {
	        File file = convertMultiPartToFile(multipartFile);
	        String fileName = generateFileName(multipartFile);
	        fileUrl = endpointUrl + "/" + fileName;
	        uploadFileTos3bucket(fileName, file);
	        file.delete();
	    } catch (Exception e) {
	       e.printStackTrace();
	    }
	    return fileUrl;
	}
	
	
	public String deleteFileFromS3Bucket(String fileUrl) {
	    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);	   
	    
	    s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
	    return "Successfully deleted";
	}
	
	
	public List<Object> getAllFiles() {
		ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix("").withDelimiter("");
		ListObjectsV2Result listing = s3client.listObjectsV2(req);
		
	    List<Object> listObj = new ArrayList<Object>();
	    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	    Date dateobj = new Date();
	    
		for (S3ObjectSummary summary: listing.getObjectSummaries()) {
			HashMap<String, String> map = new HashMap<>();
			
			map.put("fileName", summary.getKey());
			map.put("date", df.format(dateobj));
			map.put("imageUrl", endpointUrl + "/" + summary.getKey());
			listObj.add(map);
		}
		
		return listObj;
	}
	
	
}