package tn.esprit.examen.nomPrenomClasseExamen.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload a file to Cloudinary with a specific folder
     *
     * @param file   The file to upload
     * @param folder The folder name in Cloudinary
     * @return The URL of the uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            if (file.isEmpty()) {
                return null;
            }

            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto",
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            return uploadResult.get("url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    /**
     * Upload a file to Cloudinary with default folder
     *
     * @param file The file to upload
     * @return The URL of the uploaded file
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, "default");
    }

    /**
     * Delete a file from Cloudinary
     *
     * @param publicId The public ID of the file to delete
     * @return The result of the deletion operation
     */
    public Map<String, Object> deleteFile(String publicId) {
        try {
            return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * Convert MultipartFile to File
     *
     * @param multipartFile The MultipartFile to convert
     * @return The converted File
     */
    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }
}