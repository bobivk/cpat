package com.fast.cpat.service;

import com.fast.cpat.model.Analysis;
import com.fast.cpat.model.Company;
import com.fast.cpat.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompany(Long companyId, Company company) {
        checkCompanyExists(companyId);
        company.setId(companyId);
        return companyRepository.save(company);
    }

    public Analysis analyze(Long companyId) {
        checkCompanyExists(companyId);
        // call openAI API to generate questions
        // call openAI API to generate a summary analysis based on industry and KPIs
        // return analysis object containing both and company id
        return new Analysis();
    }

    public void deleteCompany(Long companyId) {
        checkCompanyExists(companyId);
        companyRepository.deleteById(companyId);
    }

    private void checkCompanyExists(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new IllegalArgumentException("Company not found");
        }
    }
}
