package com.fast.cpat.service;

import com.fast.cpat.model.Analysis;
import com.fast.cpat.model.Company;

public interface CompanyService {
    Company saveCompany(Company company);
    Company updateCompany(Long companyId, Company company);
    void deleteCompany(Long companyId);
    Analysis analyze(Long companyId);
}
