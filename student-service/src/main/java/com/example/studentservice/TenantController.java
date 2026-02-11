package com.example.studentservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping("/create")
    public String createTenant(@RequestParam String tenantId) {
        tenantService.initDatabase(tenantId);
        return "Schema created and Liquibase executed for: " + tenantId;
    }
}