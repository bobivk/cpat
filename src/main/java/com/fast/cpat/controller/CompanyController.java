package com.fast.cpat.controller;

import com.fast.cpat.model.Analysis;
import com.fast.cpat.model.Company;
import com.fast.cpat.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity<Company> saveCompany(@RequestBody Company company) {
        Company savedCompany = companyService.saveCompany(company);
        return ResponseEntity.ok(savedCompany);
    }

    @PostMapping("/company/{companyId}")
    public ResponseEntity<Company> updateCompany(@PathVariable String companyId, @RequestBody Company company) {
        Company updatedCompany = companyService.updateCompany(companyId, company);
        return ResponseEntity.ok(updatedCompany);
    }

    @GetMapping("/analysis/{companyId}")
    public ResponseEntity<Analysis> analyzeCompany(@PathVariable String companyId) {
        Analysis analysis = companyService.analyze(companyId);
        return ResponseEntity.ok(analysis);
    }

    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable String companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }
}
