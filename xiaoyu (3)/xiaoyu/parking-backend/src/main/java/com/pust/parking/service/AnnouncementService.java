package com.pust.parking.service;

import com.pust.parking.entity.Announcement;

import java.util.List;

public interface AnnouncementService {

    List<Announcement> listAll();

    Announcement create(Announcement announcement);

    Announcement update(Announcement announcement);

    void delete(Long id);
}
