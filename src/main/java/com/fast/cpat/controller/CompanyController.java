package com.fast.cpat.controller;

import com.fast.cpat.model.Analysis;
import com.fast.cpat.model.Company;
import com.fast.cpat.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity<Company> saveCompany(@RequestBody Company company) {
        Company savedCompany = companyService.saveCompany(company);
        return ResponseEntity.ok(savedCompany);
    }

    @GetMapping("/company/all")
    public ResponseEntity<List<Company>> findAll() {
        List<Company> companies = companyService.findAll();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<Company> findById(@PathVariable Long companyId) {
        Company company = companyService.findById(companyId);
        return ResponseEntity.ok(company);
    }

    @PostMapping("/company/{companyId}")
    public ResponseEntity<Company> updateCompany(@PathVariable Long companyId, @RequestBody Company company) {
        Company updatedCompany = companyService.updateCompany(companyId, company);
        return ResponseEntity.ok(updatedCompany);
    }

    @GetMapping("/analysis/{companyId}")
    public ResponseEntity<Analysis> analyzeCompany(@RequestBody Company company) {
        Analysis analysis = companyService.getSummary(company);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/questions/{companyId}")
    public ResponseEntity<Analysis> getCompanyQuestions(@RequestBody Company company) {
        Analysis analysis = companyService.getQuestions(company);
        return ResponseEntity.ok(analysis);
    }

    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }
}
