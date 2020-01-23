package com.mrityunjay.secureapp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for mapping image upload and delete and get request.
 * 
 * @author mrityunjaykumar
 *
 */
@RestController
@RequestMapping("/storage/")
public class BucketController {

    private AmazonClient amazonClient;

    @Autowired
    BucketController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    /**
     * Map request to upload file..
     * 
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return this.amazonClient.uploadFile(file);
    }

    /**
     * Delete mapping to delete file from s3.
     * 
     * @param fileUrl
     * @return
     */
    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestPart(value = "url") String fileUrl) {
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }
    
    /**
     * Request for getting files from s3 bucket..
     * 
     * @return
     */
    @GetMapping("/get-all-files")
    public List<Object> deleteFile() {
        return this.amazonClient.getAllFiles();
    }
}