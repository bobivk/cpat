import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.css'],
  standalone: true,
})
export class CompanyComponent implements OnInit {
  companyForm: FormGroup;
  validationError: string | null = null;
  companyId: number = 1;

  summary: string | null = null;
  questions: string | null = null;

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
      const data = await response.json();
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

    const metricsObject = this.metrics.value.reduce(
      (acc: Record<string, string>, metric: { name: string; value: string }) => {
        acc[metric.name.trim()] = metric.value.trim();
        return acc;
      },
      {}
    );

    const company = {
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

      this.metrics.clear();
      this.addMetric();
    } catch (error) {
      console.error('Error saving company:', error);
    }
  }

  async fetchAnalysis() {
    const requestBody = this.createRequestBody();

    try {
      const response = await fetch('http://localhost:8080/api/analysis', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody),
      });
      const data = await response.text();
      this.summary = data;
      console.log('Analysis result:', data);
    } catch (error) {
      console.error('Error fetching analysis:', error);
    }
  }

  async fetchQuestions() {
    const requestBody = this.createRequestBody();

    try {
      const response = await fetch('http://localhost:8080/api/questions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody),
      });
      const data = await response.text();
      this.questions = data;
      console.log('Questions result:', data);
    } catch (error) {
      console.error('Error fetching questions:', error);
    }
  }

  createRequestBody() {
    const metricsObject = this.metrics.value.reduce(
      (acc: Record<string, string>, metric: { name: string; value: string }) => {
        acc[metric.name.trim()] = metric.value.trim();
        return acc;
      },
      {}
    );

    return {
      companyId: this.companyId,
      industry: this.companyForm.value.industry,
      metrics: metricsObject,
    };
  }

  showValidationError(message: string | null) {
    this.validationError = message;
    if (message) {
      setTimeout(() => (this.validationError = null), 3000); // Clear error after 3 seconds
    }
  }
}
