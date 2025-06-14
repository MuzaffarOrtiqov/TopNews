package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.attach.AttachDTO;
import api.kun.uz.dto.profile.ProfileInfoDTO;
import api.kun.uz.entity.AttachEntity;
import api.kun.uz.entity.ProfileEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.AttachRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class AttachService {
    @Value("${attach.upload.folder}")
    private String folderName;
    @Value("${attach.url}")
    private String attachUrl;

    @Autowired
    private AttachRepository attachRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public AttachDTO upload(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("File is empty");
            throw new AppBadException("File not found");
        }

        try {
            String pathFolder = getYmDString(); // 2024/09/27
            String key = UUID.randomUUID().toString(); // dasdasd-dasdasda-asdasda-asdasd
            String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename())); // .jpg, .png, .mp4

            // create folder if not exists
            File folder = new File(folderName + "/" + pathFolder);
            if (!folder.exists()) {
                boolean t = folder.mkdirs();
            }

            // save to system
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folderName + "/" + pathFolder + "/" + key + "." + extension);
            Files.write(path, bytes);

            // save to db
            AttachEntity entity = new AttachEntity();
            entity.setId(key + "." + extension);
            entity.setPath(pathFolder);
            entity.setSize(file.getSize());
            entity.setOriginName(file.getOriginalFilename());
            entity.setExtension(extension);
            entity.setVisible(true);
            attachRepository.save(entity);

            return toDTO(entity);
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("File upload failed error : {}", e.getMessage());
        }
        return null;
    }

    public ResponseEntity<Resource> open(String id, AppLanguage lang) {
        AttachEntity entity = getEntity(id, lang);
        Path filePath = Paths.get(getPath(entity)).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                log.warn("File not found");
                throw new RuntimeException("File not found: " + id);
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback content type
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("File download failed error : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<byte[]> download(String fileId, AppLanguage lang) {
        AttachEntity attachEntity = getEntity(fileId, lang);
        Path filePath = Paths.get(getPath(attachEntity));
        // Get the original file name
        String originalFilename = attachEntity.getOriginName();
        try {
            byte[] bytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Page<AttachDTO> pagination(Integer page, Integer size, AppLanguage lang) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AttachEntity> attachEntityPage = attachRepository.findAllAttaches(pageable);
        long totalElements = attachEntityPage.getTotalElements();
        List<AttachDTO> profileInfoDTOList = attachEntityPage.stream().map(this::toDTO).toList();
        return new PageImpl<>(profileInfoDTOList, pageable, totalElements);
    }

    /**
     * Other Util Methods
     */
    private String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    private String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    private AttachDTO toDTO(AttachEntity entity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(entity.getId());
        attachDTO.setOriginName(entity.getOriginName());
        attachDTO.setSize(entity.getSize());
        attachDTO.setExtension(entity.getExtension());
        attachDTO.setCreatedData(entity.getCreatedDate());
        attachDTO.setUrl(openURL(entity.getId()));
        return attachDTO;
    }

    public AttachEntity getEntity(String id, AppLanguage lang) {
        Optional<AttachEntity> optional = attachRepository.findById(id);
        if (optional.isEmpty()) {
            log.warn("File not found");
            throw new AppBadException(resourceBundleMessageService.getMessage("file.not.found", lang));
        }
        return optional.get();
    }

    private String getPath(AttachEntity entity) {
        return folderName + "/" + entity.getPath() + "/" + entity.getId();
    }

    public String openURL(String fileName) {
        return attachUrl + "/open/" + fileName;
    }

    public boolean delete(String id, AppLanguage lang) {
        AttachEntity entity = getEntity(id, lang);
        attachRepository.delete(entity);
        File file = new File(getPath(entity));
        boolean b = false;
        if (file.exists()) {
            b = file.delete();
        }
        return b;
    }

    public AttachDTO attachShortInfo(String photoId) {
        if (photoId == null) return null;
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(photoId);
        attachDTO.setUrl(openURL(photoId));
        return attachDTO;
    }

    public AppResponse<String> deleteAttach(String fileId, AppLanguage lang) {
        if (!delete(fileId, lang)) {
            throw new AppBadException(resourceBundleMessageService.getMessage("file.delete.fail", lang));
        }
        return new AppResponse<>(resourceBundleMessageService.getMessage("file.delete.success", lang));
    }
}

