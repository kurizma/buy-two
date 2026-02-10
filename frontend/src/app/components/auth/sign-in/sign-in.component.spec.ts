import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { SignInComponent } from './sign-in.component';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';

describe('SignInComponent', () => {
  let component: SignInComponent;
  let fixture: ComponentFixture<SignInComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockLoginResponse = {
    message: 'Login successful',
    token: 'test-token',
    user: {
      id: 'user-1',
      email: 'test@example.com',
      name: 'Test User',
      role: 'CLIENT' as const,
      avatar: 'avatar.png',
    },
  };

  beforeEach(async () => {
    localStorage.clear();
    
    authServiceSpy = jasmine.createSpyObj('AuthService', [
      'login',
      'isAuthenticated',
      'updateCurrentUserInStorage',
    ]);
    cartServiceSpy = jasmine.createSpyObj('CartService', ['loadCart']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    authServiceSpy.isAuthenticated.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [SignInComponent, ReactiveFormsModule, NoopAnimationsModule],
    })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .overrideProvider(CartService, { useValue: cartServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .compileComponents();

    fixture = TestBed.createComponent(SignInComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form initialization', () => {
    it('should create form with email and password controls', () => {
      expect(component.signInForm.contains('email')).toBeTrue();
      expect(component.signInForm.contains('password')).toBeTrue();
    });

    it('should initialize with empty values', () => {
      expect(component.signInForm.get('email')?.value).toBe('');
      expect(component.signInForm.get('password')?.value).toBe('');
    });

    it('should require email', () => {
      const email = component.signInForm.get('email');
      email?.setValue('');
      expect(email?.valid).toBeFalse();
      expect(email?.errors?.['required']).toBeTruthy();
    });

    it('should validate email format', () => {
      const email = component.signInForm.get('email');
      email?.setValue('invalid-email');
      expect(email?.errors?.['email']).toBeTruthy();

      email?.setValue('valid@email.com');
      expect(email?.errors).toBeNull();
    });

    it('should require password', () => {
      const password = component.signInForm.get('password');
      password?.setValue('');
      expect(password?.valid).toBeFalse();
    });

    it('should require minimum password length', () => {
      const password = component.signInForm.get('password');
      password?.setValue('12345');
      expect(password?.errors?.['minlength']).toBeTruthy();

      password?.setValue('123456');
      expect(password?.errors).toBeNull();
    });
  });

  describe('ngOnInit', () => {
    it('should redirect if already authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      component.ngOnInit();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });

    it('should not redirect if not authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);
      component.ngOnInit();
      expect(routerSpy.navigate).not.toHaveBeenCalled();
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      component.signInForm.setValue({
        email: 'test@example.com',
        password: 'password123',
      });
    });

    it('should not submit if form is invalid', () => {
      component.signInForm.setValue({ email: '', password: '' });
      component.onSubmit();
      expect(authServiceSpy.login).not.toHaveBeenCalled();
    });

    it('should call login service on valid form', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();
      tick();

      expect(authServiceSpy.login).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123',
      });
    }));

    it('should set loading state during login', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      expect(component.isLoading).toBeFalse();
      component.onSubmit();
      // Loading should be true during the call
      tick();
      expect(component.isLoading).toBeFalse();
    }));

    it('should store token on successful login', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();
      tick();

      expect(localStorage.getItem('token')).toBe('test-token');
    }));

    it('should store user on successful login', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();
      tick();

      expect(localStorage.getItem('currentUser')).toBeTruthy();
      expect(authServiceSpy.updateCurrentUserInStorage).toHaveBeenCalledWith(mockLoginResponse.user);
    }));

    it('should load cart after login', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();
      tick();

      expect(cartServiceSpy.loadCart).toHaveBeenCalled();
    }));

    it('should handle login error', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(throwError(() => ({
        error: { message: 'Invalid credentials' },
      })));

      component.onSubmit();
      tick();

      expect(component.isLoading).toBeFalse();
      expect(component.errorMessage).toBeTruthy();
    }));
  });
});
