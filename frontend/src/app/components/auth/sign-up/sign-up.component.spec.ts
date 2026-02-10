import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SignUpComponent } from './sign-up.component';
import { AuthService } from '../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError, delay } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

describe('SignUpComponent', () => {
  let component: SignUpComponent;
  let fixture: ComponentFixture<SignUpComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['signup']);

    await TestBed.configureTestingModule({
      imports: [SignUpComponent, HttpClientTestingModule, RouterTestingModule, NoopAnimationsModule],
    })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(SignUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form initialization', () => {
    it('should have form with all required fields', () => {
      expect(component.form.contains('name')).toBeTrue();
      expect(component.form.contains('email')).toBeTrue();
      expect(component.form.contains('password')).toBeTrue();
      expect(component.form.contains('confirmPassword')).toBeTrue();
      expect(component.form.contains('role')).toBeTrue();
    });

    it('should have default role as client', () => {
      expect(component.form.get('role')?.value).toBe('client');
    });

    it('should have default avatar', () => {
      expect(component.avatar).toBe('assets/avatars/user-default.png');
    });

    it('should start with empty error message', () => {
      expect(component.errorMessage).toBe('');
    });

    it('should start with isLoading as false', () => {
      expect(component.isLoading).toBeFalse();
    });
  });

  describe('form validation', () => {
    it('should be invalid when empty', () => {
      expect(component.form.valid).toBeFalse();
    });

    it('should require name', () => {
      const nameCtrl = component.form.get('name');
      expect(nameCtrl?.hasError('required')).toBeTrue();
    });

    it('should require name to be at least 2 characters', () => {
      component.form.get('name')?.setValue('A');
      expect(component.form.get('name')?.hasError('minlength')).toBeTrue();
    });

    it('should require valid email', () => {
      component.form.get('email')?.setValue('invalid');
      expect(component.form.get('email')?.hasError('email')).toBeTrue();
    });

    it('should require password', () => {
      expect(component.form.get('password')?.hasError('required')).toBeTrue();
    });

    it('should require password to be at least 8 characters', () => {
      component.form.get('password')?.setValue('Pass1');
      expect(component.form.get('password')?.hasError('minlength')).toBeTrue();
    });

    it('should require password pattern (lowercase and digit)', () => {
      component.form.get('password')?.setValue('PASSWORD');
      expect(component.form.get('password')?.hasError('pattern')).toBeTrue();
    });

    it('should accept valid password', () => {
      component.form.get('password')?.setValue('Password123');
      expect(component.form.get('password')?.valid).toBeTrue();
    });

    it('should validate matching passwords', () => {
      component.form.get('password')?.setValue('Password123');
      component.form.get('confirmPassword')?.setValue('Different123');
      expect(component.form.hasError('notMatching')).toBeTrue();
    });

    it('should be valid when passwords match', () => {
      component.form.setValue({
        name: 'John Doe',
        email: 'john@example.com',
        password: 'Password123',
        confirmPassword: 'Password123',
        role: 'client',
      });
      expect(component.form.hasError('notMatching')).toBeFalse();
    });
  });

  describe('matchPasswords static method', () => {
    it('should return null when passwords match', () => {
      component.form.get('password')?.setValue('Password123');
      component.form.get('confirmPassword')?.setValue('Password123');
      const result = SignUpComponent.matchPasswords(component.form);
      expect(result).toBeNull();
    });

    it('should return error object when passwords do not match', () => {
      component.form.get('password')?.setValue('Password123');
      component.form.get('confirmPassword')?.setValue('Different123');
      const result = SignUpComponent.matchPasswords(component.form);
      expect(result).toEqual({ notMatching: true });
    });
  });

  describe('submit', () => {
    const validFormValues = {
      name: 'John Doe',
      email: 'john@example.com',
      password: 'Password123',
      confirmPassword: 'Password123',
      role: 'client',
    };

    it('should not submit when form is invalid', () => {
      component.submit();
      expect(authServiceSpy.signup).not.toHaveBeenCalled();
    });

    it('should set isLoading to true during submission', fakeAsync(() => {
      component.form.setValue(validFormValues);
      // Use delay to test intermediate state
      authServiceSpy.signup.and.returnValue(
        of({ id: '1', name: 'John', email: 'john@example.com', role: 'CLIENT' as const }).pipe(delay(100))
      );
      
      component.submit();
      expect(component.isLoading).toBeTrue();
      
      tick(100);
      expect(component.isLoading).toBeFalse();
    }));

    it('should call authService.signup with transformed payload', fakeAsync(() => {
      component.form.setValue(validFormValues);
      authServiceSpy.signup.and.returnValue(of({ id: '1', name: 'John', email: 'john@example.com', role: 'CLIENT' as const }));
      
      component.submit();
      tick();
      
      expect(authServiceSpy.signup).toHaveBeenCalledWith(jasmine.objectContaining({
        name: 'John Doe',
        email: 'john@example.com',
        password: 'Password123',
        avatar: 'assets/avatars/user-default.png',
      }));
    }));

    it('should navigate to signin on successful signup', fakeAsync(() => {
      component.form.setValue(validFormValues);
      authServiceSpy.signup.and.returnValue(of({ id: '1', name: 'John', email: 'john@example.com', role: 'CLIENT' as const }));
      
      component.submit();
      tick();
      
      expect(router.navigate).toHaveBeenCalledWith(['/signin']);
    }));

    it('should transform seller role to uppercase', fakeAsync(() => {
      component.form.setValue({ ...validFormValues, role: 'seller' });
      authServiceSpy.signup.and.returnValue(of({ id: '1', name: 'John', email: 'john@example.com', role: 'SELLER' as const }));
      
      component.submit();
      tick();
      
      expect(authServiceSpy.signup).toHaveBeenCalledWith(jasmine.objectContaining({
        role: 'SELLER',
      }));
    }));

    it('should show email taken error on 409 status', fakeAsync(() => {
      component.form.setValue(validFormValues);
      const errorResponse = new HttpErrorResponse({
        status: 409,
        error: { message: 'Email already exists' },
      });
      authServiceSpy.signup.and.returnValue(throwError(() => errorResponse));
      
      component.submit();
      tick();
      
      expect(component.errorMessage).toBe('This email is already taken.');
      expect(component.isLoading).toBeFalse();
    }));

    it('should show generic error on other errors', fakeAsync(() => {
      component.form.setValue(validFormValues);
      const errorResponse = new HttpErrorResponse({
        status: 500,
        error: { message: 'Server error' },
      });
      authServiceSpy.signup.and.returnValue(throwError(() => errorResponse));
      
      component.submit();
      tick();
      
      expect(component.errorMessage).toBe('Sign-up failed. Please try again.');
      expect(component.isLoading).toBeFalse();
    }));

    it('should clear error message before submission', fakeAsync(() => {
      component.form.setValue(validFormValues);
      component.errorMessage = 'Previous error';
      authServiceSpy.signup.and.returnValue(of({ id: '1', name: 'John', email: 'john@example.com', role: 'CLIENT' as const }));
      
      component.submit();
      tick();
      
      expect(component.errorMessage).toBe('');
    }));
  });

  describe('UI elements', () => {
    it('should display default avatar', () => {
      const avatarImg = fixture.nativeElement.querySelector('.avatar-preview');
      expect(avatarImg?.src).toContain('user-default.png');
    });

    it('should show error message when present', () => {
      component.errorMessage = 'Email already exists';
      fixture.detectChanges();
      const errorAlert = fixture.nativeElement.querySelector('.alert.alert-danger');
      expect(errorAlert?.textContent).toContain('Email already exists');
    });

    it('should disable submit button when form invalid', () => {
      const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
      expect(submitButton?.disabled).toBeTrue();
    });

    it('should enable submit button when form valid', () => {
      component.form.setValue({
        name: 'John Doe',
        email: 'john@example.com',
        password: 'Password123',
        confirmPassword: 'Password123',
        role: 'client',
      });
      fixture.detectChanges();
      const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
      expect(submitButton?.disabled).toBeFalse();
    });
  });
});
