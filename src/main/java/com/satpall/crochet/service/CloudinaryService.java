package com.satpall.crochet.service;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png", "image/webp"};

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) {
        validateFile(file);

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "loomelle-crochet/products",
                    "resource_type", "image"
            ));

            String secureUrl = (String) uploadResult.get("secure_url");
            if (secureUrl == null || secureUrl.isBlank()) {
                throw new IllegalStateException("Cloudinary did not return a secure URL for the uploaded image.");
            }

            log.info("Uploaded image to Cloudinary: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new IllegalStateException("Image upload failed. Please try again later.", e);
        } catch (Exception e) {
            log.error("Cloudinary upload failed", e);
            throw new IllegalStateException("Image upload failed: " + e.getMessage(), e);
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Deleted image from Cloudinary: {}", imageUrl);
            }
        } catch (Exception e) { 
            log.warn("Could not delete image from Cloudinary: {}", imageUrl, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image file.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Image file must be 5MB or smaller.");
        }

        String contentType = file.getContentType();
        boolean isAllowed = contentType != null && java.util.Arrays.stream(ALLOWED_CONTENT_TYPES)
                .anyMatch(contentType::equalsIgnoreCase);

        if (!isAllowed) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, and WebP image files are allowed.");
        }
    }

    private String extractPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                return null;
            }

            String tail = parts[1];
            int queryIndex = tail.indexOf('?');
            if (queryIndex >= 0) {
                tail = tail.substring(0, queryIndex);
            }

            String[] segments = tail.split("/");
            if (segments.length == 0) {
                return null;
            }

            StringBuilder publicId = new StringBuilder();
            for (int i = 1; i < segments.length; i++) {
                if (i > 1) {
                    publicId.append('/');
                }
                publicId.append(segments[i]);
            }

            return publicId.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
