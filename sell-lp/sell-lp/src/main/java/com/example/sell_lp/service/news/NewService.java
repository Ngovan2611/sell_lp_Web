package com.example.sell_lp.service.news;

import com.example.sell_lp.dto.request.news.NewRequest;
import com.example.sell_lp.dto.response.news.NewResponse;
import com.example.sell_lp.entity.New;
import com.example.sell_lp.mapper.NewMapper;
import com.example.sell_lp.repository.news.NewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewService {

    NewRepository newRepository;
    NewMapper newMapper;

    public List<NewResponse> getAllNews() {
        return newRepository.findAll().stream()
                .map(newMapper::toNewResponse)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveNews(NewRequest newsRequest) {
        New entity = newMapper.toNew(newsRequest);
        entity.setActive(true);
        newRepository.save(entity);
    }

    @Transactional
    public void updateNews(Integer id, NewRequest newsRequest) {
        New existingNew = newRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));

        existingNew.setTitle(newsRequest.getTitle());
        existingNew.setShortDescription(newsRequest.getShortDescription());
        existingNew.setContent(newsRequest.getContent());
        existingNew.setThumbnail(newsRequest.getThumbnail());
        existingNew.setActive(newsRequest.isActive());

        newRepository.save(existingNew);
    }

    @Transactional
    public void toggleActive(Integer id) {
        New existingNew = newRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));

        existingNew.setActive(!existingNew.isActive());
        newRepository.save(existingNew);
    }

    @Transactional
    public void deleteNews(Integer id) {
        if (!newRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bài viết cần xóa với ID: " + id);
        }
        newRepository.deleteById(id);
    }
}