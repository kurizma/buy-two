import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';

import { AuthService } from '../../../services/auth.service';

// Import the Material modules you use in this component
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    NgIf,
  ],
})
export class SignInComponent implements OnInit {
  signInForm: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  constructor() {
    this.signInForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  ngOnInit(): void {
    // Redirect if already logged in
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
    }
  }

  onSubmit(): void {
    if (this.signInForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const { email, password } = this.signInForm.value;

      this.authService.login({ email, password }).subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log('Login successful', response);

          const user = this.authService.currentUserValue;
          if (user?.role === 'SELLER') {
            localStorage.setItem('token', response.token);
            this.router.navigate(['/profile']);
          } else {
            localStorage.setItem('token', response.token);
            this.router.navigate(['/profile']);
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Login failed. Please check your credentials.';
          console.error('Login error', error);
        },
      });
    }
  }

  get email() {
    return this.signInForm.get('email');
  }

  get password() {
    return this.signInForm.get('password');
  }
}
