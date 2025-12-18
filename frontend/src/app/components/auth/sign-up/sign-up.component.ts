import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css'],
  imports: [
    RouterModule,
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    NgIf,
    MatRadioModule,
  ],
})
export class SignUpComponent {
  uploadProgress = 0;
  avatar: string = 'assets/avatars/user-default.png';
  avatarError: string = '';
  errorMessage = '';
  isLoading = false;

  form: FormGroup;

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router: Router = inject(Router);

  constructor() {
    this.form = this.fb.group(
      {
        name: ['', [Validators.required, Validators.minLength(2)]],
        email: ['', [Validators.required, Validators.email]],
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(8),
            Validators.pattern('^(?=.*[a-z])(?=.*\\d).{8,}$'),
          ],
        ],
        confirmPassword: ['', Validators.required],
        role: ['client', Validators.required],
      },
      { validators: SignUpComponent.matchPasswords },
    );
  }

  static matchPasswords(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : { notMatching: true };
  }

  submit() {
    if (this.form.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    const { name, email, password, role } = this.form.value;

    // Transform to match backend RegisterUserRequest
    const payload = {
      name: name!,
      email: email!,
      password: password!,
      role: role.toUpperCase(), // 'client' | 'seller' to 'CLIENT' | 'SELLER'
      avatar: this.avatar,
    };

    this.authService.signup(payload).subscribe({
      next: (_user: any) => {
        this.isLoading = false;
        console.log('Sign-up successful', _user);
        this.router.navigate(['/signin']);
      },
      error: (error: HttpErrorResponse) => {
        this.isLoading = false;
        if (error.status === 409 && error.error?.message === 'Email already exists') {
          this.errorMessage = 'This email is already taken.';
        } else {
          this.errorMessage = 'Sign-up failed. Please try again.';
        }
        console.error('Sign-up error', error);
      },
    });
  }
}
