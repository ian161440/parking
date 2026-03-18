package com.pust.parking.controller;

import com.pust.parking.common.ApiResponse;
import com.pust.parking.entity.Announcement;
import com.pust.parking.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public ApiResponse<List<Announcement>> listAll() {
        return ApiResponse.success(announcementService.listAll());
    }

    @PostMapping("/admin")
    public ApiResponse<Announcement> create(@RequestBody Announcement announcement) {
        return ApiResponse.success(announcementService.create(announcement));
    }

    @PutMapping("/admin/{id}")
    public ApiResponse<Announcement> update(@PathVariable("id") Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        return ApiResponse.success(announcementService.update(announcement));
    }

    @DeleteMapping("/admin/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        announcementService.delete(id);
        return ApiResponse.success(null);
    }
}
