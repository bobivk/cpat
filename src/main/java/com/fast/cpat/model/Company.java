package com.fast.cpat.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    private String id;
    private String name;
    private String industry;

    @ElementCollection
    @CollectionTable(name = "company_metrics", joinColumns = @JoinColumn(name = "company_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, Double> metrics;

}


