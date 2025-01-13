package com.core.arnuv.services.imp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseFileService {
    private Storage storage;
    @Value("${firebase.storage.bucketName}")
    private String bucketName;
    @Value("${firebase.storage.projectId}")
    private String projectId;
    @Value("${firebase.storage.url}")
    private String url;

    @EventListener
    public void init(ApplicationReadyEvent event) {
        try {
            ClassPathResource serviceAccount = new ClassPathResource("serviceAccountKey.json");
            storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .setProjectId(projectId)
                    .build()
                    .getService();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String imageName = generateFileName(getFileExtension(file));
        BlobId blobId = BlobId.of(bucketName, imageName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getInputStream());
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        return url.concat(bucketName).concat("/").concat(imageName);
    }

    private static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return originalFilename.substring(dotIndex + 1);
    }

    private String generateFileName(String extensionFile) {
        return UUID.randomUUID().toString() + "." + extensionFile;
    }
}
