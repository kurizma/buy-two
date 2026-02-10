import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ClientOnlyGuard } from './client-only.guard';
import { AuthService } from '../services/auth.service';

describe('ClientOnlyGuard', () => {
  let guard: ClientOnlyGuard;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isClient']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        ClientOnlyGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });

    guard = TestBed.inject(ClientOnlyGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate', () => {
    it('should allow access for client', () => {
      authServiceSpy.isClient.and.returnValue(true);

      const result = guard.canActivate();

      expect(result).toBeTrue();
      expect(routerSpy.navigate).not.toHaveBeenCalled();
    });

    it('should deny access and redirect for non-client', () => {
      authServiceSpy.isClient.and.returnValue(false);

      const result = guard.canActivate();

      expect(result).toBeFalse();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });

    it('should redirect seller to home', () => {
      authServiceSpy.isClient.and.returnValue(false);

      guard.canActivate();

      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });
  });
});
