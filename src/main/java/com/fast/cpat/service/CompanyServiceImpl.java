package com.fast.cpat.service;

import com.fast.cpat.model.Analysis;
import com.fast.cpat.model.Company;
import com.fast.cpat.repository.CompanyRepository;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompanyServiceImpl implements CompanyService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String LLM_CONTEXT = "You are an expert consultant that specialises with analysing companies and knows how to ask deep questions about the company to its board and C-suite executives. You specialize in the industry of ";

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SecretsService secretsService;

    @Autowired
    private OkHttpClient okHttpClient;

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    public Company findById(Long companyId) {
        return companyRepository.findById(companyId).orElseThrow(NoSuchElementException::new);
    }

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompany(Long companyId, Company company) {
        checkCompanyExists(companyId);
        company.setId(companyId);
        return companyRepository.save(company);
    }

    public Analysis getQuestions(Company company) {
        checkCompanyExists(company.getId());
        String prompt = getResource("LLM_questions_prompt.txt");

        String response = callOpenAIAPI(prompt, company.getIndustry());
        return new Analysis(company.getId(), response);
    }

    public Analysis getSummary(Company company) {
        checkCompanyExists(company.getId());

        String prompt = getResource("LLM_summary_prompt.txt");
        prompt = company.getMetrics().toString() + "\n" + prompt;

        String response = callOpenAIAPI(prompt, company.getIndustry());
        return new Analysis(company.getId(), response);
    }

    private String getResource(String file) {
        String resource = "";
        try {
            Path resourcePath = Paths.get(
                    this.getClass().getClassLoader().getResource(file).toURI()
            );

            resource = Files.readString(resourcePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resource;
    }

    private String callOpenAIAPI(String prompt, String industry) {
        String openApiKey = this.secretsService.getSecret("openapi_key");

        String requestBodyJson = String.format("""
            {
              "model": "gpt-4o-mini",
              "messages": [
                {"role": "system", "content": "%s"},
                {"role": "user", "content": "%s"}
              ],
              "temperature": 0.7
            }
            """, LLM_CONTEXT + industry, prompt + industry);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + openApiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBodyJson, MediaType.get("application/json")))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                System.err.println("Error: " + response.code() + " - " + response.body().string());
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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
