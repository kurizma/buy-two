import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { LoginRequest } from '../models/users/loginRequest.model';
import { RegisterUserRequest } from '../models/users/registerUserRequest.model';
import { LoginResponse } from '../models/users/login-response.model';
import { UserResponse } from '../models/users/user-response.model';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockUser: UserResponse = {
    id: 'user-1',
    email: 'test@example.com',
    name: 'Test User',
    role: 'CLIENT',
    avatar: 'avatar.png',
  };

  const mockLoginResponse: LoginResponse = {
    message: 'Login successful',
    token: 'test-jwt-token',
    user: mockUser,
  };

  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear();

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should login and store token and user', () => {
      const credentials: LoginRequest = {
        email: 'test@example.com',
        password: 'password123',
      };

      service.login(credentials).subscribe((response) => {
        expect(response.token).toBe('test-jwt-token');
        expect(response.user).toEqual(mockUser);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/auth/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credentials);
      req.flush(mockLoginResponse);

      expect(localStorage.getItem('token')).toBe('test-jwt-token');
      expect(JSON.parse(localStorage.getItem('currentUser')!)).toEqual(mockUser);
    });
  });

  describe('signup', () => {
    it('should register a new user', () => {
      const userData: RegisterUserRequest = {
        email: 'new@example.com',
        password: 'password123',
        name: 'New User',
        role: 'client',
      };

      service.signup(userData).subscribe((response) => {
        expect(response).toEqual(mockUser);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/auth/register`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(userData);
      req.flush(mockUser);
    });
  });

  describe('logout', () => {
    it('should clear storage and user subject', () => {
      // Setup: login first
      localStorage.setItem('token', 'test-token');
      localStorage.setItem('currentUser', JSON.stringify(mockUser));
      localStorage.setItem('shopping_cart', '[]');

      service.logout();

      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('currentUser')).toBeNull();
      expect(localStorage.getItem('shopping_cart')).toBeNull();
      expect(service.currentUserValue).toBeNull();
    });
  });

  describe('isAuthenticated', () => {
    it('should return true when token exists', () => {
      localStorage.setItem('token', 'test-token');
      expect(service.isAuthenticated()).toBeTrue();
    });

    it('should return false when no token', () => {
      localStorage.removeItem('token');
      expect(service.isAuthenticated()).toBeFalse();
    });
  });

  describe('isSeller', () => {
    it('should return true for SELLER role', () => {
      const sellerUser: UserResponse = { ...mockUser, role: 'SELLER' };
      localStorage.setItem('currentUser', JSON.stringify(sellerUser));
      
      // Recreate service to pick up storage
      service = TestBed.inject(AuthService);
      service.updateCurrentUserInStorage(sellerUser);
      
      expect(service.isSeller()).toBeTrue();
    });

    it('should return false for CLIENT role', () => {
      service.updateCurrentUserInStorage(mockUser);
      expect(service.isSeller()).toBeFalse();
    });
  });

  describe('isClient', () => {
    it('should return true for CLIENT role', () => {
      service.updateCurrentUserInStorage(mockUser);
      expect(service.isClient()).toBeTrue();
    });

    it('should return false for SELLER role', () => {
      const sellerUser: UserResponse = { ...mockUser, role: 'SELLER' };
      service.updateCurrentUserInStorage(sellerUser);
      expect(service.isClient()).toBeFalse();
    });
  });

  describe('getUserId', () => {
    it('should return user id when logged in', () => {
      service.updateCurrentUserInStorage(mockUser);
      expect(service.getUserId()).toBe('user-1');
    });

    it('should return null when not logged in', () => {
      expect(service.getUserId()).toBeNull();
    });
  });

  describe('updateCurrentUserInStorage', () => {
    it('should update storage and subject', () => {
      const updatedUser = { ...mockUser, name: 'Updated Name' };
      
      service.updateCurrentUserInStorage(updatedUser);
      
      expect(JSON.parse(localStorage.getItem('currentUser')!)).toEqual(updatedUser);
      expect(service.currentUserValue).toEqual(updatedUser);
    });
  });

  describe('currentUser$', () => {
    it('should emit user updates', (done) => {
      service.currentUser$.subscribe((user) => {
        if (user?.id === 'user-1') {
          expect(user.name).toBe('Test User');
          done();
        }
      });

      service.updateCurrentUserInStorage(mockUser);
    });
  });
});
