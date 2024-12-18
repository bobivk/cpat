import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { CompanyComponent } from './company/company.component';

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppComponent,        // Import standalone components
    CompanyComponent,    // Import standalone components
  ],
  bootstrapApplication: [AppComponent],
})
export class AppModule {}
