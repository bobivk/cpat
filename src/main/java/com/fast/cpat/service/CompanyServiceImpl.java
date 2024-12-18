package com.fast.cpat.service;

import com.fast.cpat.model.Analysis;
import com.fast.cpat.model.Company;
import com.fast.cpat.repository.CompanyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        String prompt = this.secretsService.getSecret("LLM_questions_prompt");

        String response = callOpenAIAPI(prompt, company.getIndustry());
        return new Analysis(company.getId(), response);
    }

    public Analysis getSummary(Company company) {
        checkCompanyExists(company.getId());

        String prompt = this.secretsService.getSecret("LLM_summary_prompt");
        prompt = company.getMetrics().toString() + "\n" + prompt;

        String response = callOpenAIAPI(prompt, company.getIndustry());
        return new Analysis(company.getId(), response);
    }

    private String callOpenAIAPI(String prompt, String industry) {
        String openAIAPIKey = this.secretsService.getSecret("openapi_key");
        String model = this.secretsService.getSecret("model");

        String requestBodyJson = String.format("""
        {
          "model": "%s",
          "messages": [
            {"role": "system", "content": "%s"},
            {"role": "user", "content": "%s"}
          ],
          "temperature": 0.7
        }
        """, model, LLM_CONTEXT + industry, prompt + industry);
        System.out.println("Request body: " + requestBodyJson);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + openAIAPIKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBodyJson, MediaType.get("application/json")))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                // Parse JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                // Extract the "content" field inside "message"
                JsonNode contentNode = rootNode
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content");

                // Return the cleaned HTML string
                if (!contentNode.isMissingNode()) {
                    String rawContent = contentNode.asText();
                    return cleanHTML(rawContent);
                } else {
                    System.err.println("Error: 'content' field not found in response.");
                    return "";
                }
            } else {
                System.err.println("Error: " + response.code() + " - " + (response.body() != null ? response.body().string() : "No response body"));
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Helper method to clean HTML content
    private String cleanHTML(String rawContent) {
        // Remove code block markers and escape characters
        return rawContent
                .replaceAll("```html", "") // Remove opening code block
                .replaceAll("```", "")     // Remove closing code block
                .replace("\\n", "")        // Remove escaped newlines
                .replace("\n", "")         // Remove unescaped newlines
                .replace("\\", "");        // Unescape other characters
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
