package com.pust.parking.service.impl;

import com.pust.parking.entity.Announcement;
import com.pust.parking.mapper.AnnouncementMapper;
import com.pust.parking.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementServiceImpl(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    @Override
    public List<Announcement> listAll() {
        return announcementMapper.listAllOrderByTop();
    }

    @Override
    @Transactional
    public Announcement create(Announcement announcement) {
        if (announcement.getTitle() == null || announcement.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("公告标题不能为空");
        }
        if (announcement.getContent() == null || announcement.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("公告内容不能为空");
        }
        if (announcement.getIsTop() == null) {
            announcement.setIsTop(false);
        }
        announcementMapper.insert(announcement);
        return announcementMapper.findById(announcement.getId());
    }

    @Override
    @Transactional
    public Announcement update(Announcement announcement) {
        if (announcement.getId() == null) {
            throw new IllegalArgumentException("公告ID不能为空");
        }
        Announcement existing = announcementMapper.findById(announcement.getId());
        if (existing == null) {
            throw new IllegalArgumentException("公告不存在");
        }
        announcementMapper.update(announcement);
        return announcementMapper.findById(announcement.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        announcementMapper.deleteById(id);
    }
}
