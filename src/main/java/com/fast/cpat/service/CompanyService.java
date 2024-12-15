package com.fast.cpat.service;

import com.fast.cpat.model.Analysis;
import com.fast.cpat.model.Company;

public interface CompanyService {
    Company saveCompany(Company company);
    Company updateCompany(String id, Company company);
    void deleteCompany(String id);
    Analysis analyze(String companyId);
}
