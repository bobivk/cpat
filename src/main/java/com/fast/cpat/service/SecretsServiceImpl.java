package com.fast.cpat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

@Service
public class SecretsServiceImpl implements SecretsService {

    @Autowired
    private SsmClient ssmClient;

    @Override
    public String getSecret(String key) {
        return this.ssmClient.getParameter(
                        GetParameterRequest.builder()
                                .name("/config/cpat/" + key)
                                .withDecryption(true)
                                .build())
                .parameter()
                .value();
    }
}
