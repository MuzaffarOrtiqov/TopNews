package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.tag.TagCreateDto;
import api.kun.uz.dto.tag.TagResponseDto;
import api.kun.uz.entity.TagEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.repository.TagRepository;
import api.kun.uz.util.SpringSecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public AppResponse<String> createTag(TagCreateDto tagCreateDto, AppLanguage lang) {
        Optional<TagEntity> optional = tagRepository.findByName(tagCreateDto.getTagName());
        if (optional.isPresent()) {
            return new AppResponse<>(optional.get().getName());
        }
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tagCreateDto.getTagName());
        tagEntity.setProfileId(SpringSecurityUtil.getCurrentProfileId());
        tagRepository.save(tagEntity);
        return new AppResponse<>(tagEntity.getName());
    }

    public List<TagResponseDto> getAllTags(AppLanguage lang) {
        List<TagEntity> tagEntityList = tagRepository.findAll();
        return tagEntityList.stream().map(this::toTagResponseDto).toList();
    }

    private TagResponseDto toTagResponseDto(TagEntity tagEntity) {
        TagResponseDto dto = new TagResponseDto();
        dto.setId(tagEntity.getId());
        dto.setTagName(tagEntity.getName());
        dto.setCreatedDate(tagEntity.getCreatedDate());
        dto.setVisible(tagEntity.getVisible());
        return dto;
    }

    public List<String> createTag(List<String> tagName, String profileId, AppLanguage lang) {
        List<String> tagIdList = new LinkedList<>();
        tagName.forEach(tag -> {
            Optional<TagEntity> optional = tagRepository.findByName(tag);
            if (optional.isEmpty()) {
                TagEntity tagEntity = new TagEntity();
                tagEntity.setName(tag);
                tagEntity.setProfileId(profileId);
                tagRepository.save(tagEntity);
                tagIdList.add(tagEntity.getId());
            }
            if(optional.isPresent()){
                tagIdList.add(optional.get().getId());
            }

        });
        return tagIdList;
    }
}

