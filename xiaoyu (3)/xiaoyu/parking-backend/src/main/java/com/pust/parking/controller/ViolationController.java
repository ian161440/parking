package com.pust.parking.controller;

import com.pust.parking.common.ApiResponse;
import com.pust.parking.entity.ViolationRecord;
import com.pust.parking.service.ViolationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
@CrossOrigin
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<ViolationRecord>> listAll() {
        return ApiResponse.success(violationService.listAll());
    }

    @PostMapping("/admin")
    public ApiResponse<ViolationRecord> create(@RequestBody ViolationRecord record) {
        return ApiResponse.success(violationService.create(record));
    }

    @PutMapping("/admin/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable("id") Long id,
                                          @RequestParam("status") String status,
                                          @RequestParam(value = "handlerId", required = false) Long handlerId) {
        violationService.updateStatus(id, status, handlerId);
        return ApiResponse.success(null);
    }
}
