import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

interface Metric {
  name: string;
  value: string;
}

interface Company {
  id: number;
  name: string;
  industry: string;
  metrics: Record<string, string>;
  summary: string;
}

interface Analysis {
  summary: string;
  questions: string[];
  companyId: number;
}

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.css'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgForOf,
    NgIf
  ]
})
export class CompanyComponent implements OnInit {
  companyForm: FormGroup;
  validationError: string | null = null;
  companyId: number = 1;

  constructor(private fb: FormBuilder) {
    this.companyForm = this.fb.group({
      name: ['', Validators.required],
      industry: ['', Validators.required],
      metrics: this.fb.array([]), // FormArray for metrics
    });
  }

  ngOnInit() {
    this.addMetric(); // Add an initial empty metric row
    this.fetchCompanies();
  }

  get metrics(): FormArray {
    return this.companyForm.get('metrics') as FormArray;
  }

  addMetric() {
    // Add a new metric row with empty values
    this.metrics.push(
      this.fb.group({
        name: ['', Validators.required],
        value: ['', Validators.required],
      })
    );
  }

  removeMetric(index: number) {
    this.metrics.removeAt(index);
  }

  async fetchCompanies() {
    try {
      const response = await fetch('http://localhost:8080/api/company/all');
      const data: Company[] = await response.json();
      console.log('Fetched companies:', data);
    } catch (error) {
      console.error('Error fetching companies:', error);
    }
  }

  async saveCompany() {
    if (this.companyForm.invalid) {
      this.showValidationError('All fields and metrics must be completed before saving.');
      return;
    }

    // Convert metrics FormArray into an object
    const metricsObject = this.metrics.value.reduce(
      (acc: Record<string, string>, metric: Metric) => {
        acc[metric.name.trim()] = metric.value.trim();
        return acc;
      },
      {}
    );

    const company: Company = {
      id: this.companyId,
      name: this.companyForm.value.name,
      industry: this.companyForm.value.industry,
      metrics: metricsObject,
      summary: '',
    };

    console.log('Saving company:', company);

    try {
      const response = await fetch('http://localhost:8080/api/company', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(company),
      });
      const data = await response.json();
      console.log('Company saved:', data);

      // Reset metrics after saving
      this.metrics.clear();
      this.addMetric(); // Add a fresh empty row
    } catch (error) {
      console.error('Error saving company:', error);
    }
  }

  async analyzeCompany() {
    if (!this.companyId) {
      this.showValidationError('No company ID provided.');
      return;
    }

    const url = `http://localhost:8080/api/analyze/${this.companyId}`;
    console.log('Fetching analysis:', url);

    try {
      const response = await fetch(url);
      const data: Analysis = await response.json();
      console.log('Analysis result:', data);
      alert(`Analysis Summary: ${data.summary}\nQuestions: ${data.questions.join('\n')}`);
    } catch (error) {
      console.error('Error fetching analysis:', error);
    }
  }

  showValidationError(message: string | null) {
    this.validationError = message;
    if (message) {
      setTimeout(() => (this.validationError = null), 3000); // Clear error after 3 seconds
    }
  }
}
