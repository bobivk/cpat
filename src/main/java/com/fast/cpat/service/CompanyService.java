package com.fast.cpat.service;

import com.fast.cpat.model.Analysis;
import com.fast.cpat.model.Company;

import java.util.List;

public interface CompanyService {
    List<Company> findAll();
    Company findById(Long companyId);
    Company saveCompany(Company company);
    Company updateCompany(Long companyId, Company company);
    void deleteCompany(Long companyId);
    Analysis getQuestions(Company company);
    Analysis getSummary(Company company);
}
