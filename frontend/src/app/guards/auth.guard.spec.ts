import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });

    guard = TestBed.inject(AuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate', () => {
    it('should allow access when authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);

      const mockRoute = { } as any;
      const mockState = { url: '/protected-page' } as any;

      const result = guard.canActivate(mockRoute, mockState);

      expect(result).toBeTrue();
      expect(routerSpy.navigate).not.toHaveBeenCalled();
    });

    it('should deny access and redirect when not authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);

      const mockRoute = { } as any;
      const mockState = { url: '/protected-page' } as any;

      const result = guard.canActivate(mockRoute, mockState);

      expect(result).toBeFalse();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/signin'], {
        queryParams: { returnUrl: '/protected-page' },
      });
    });

    it('should pass return URL for redirect', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);

      const mockRoute = { } as any;
      const mockState = { url: '/checkout' } as any;

      guard.canActivate(mockRoute, mockState);

      expect(routerSpy.navigate).toHaveBeenCalledWith(['/signin'], {
        queryParams: { returnUrl: '/checkout' },
      });
    });
  });
});
