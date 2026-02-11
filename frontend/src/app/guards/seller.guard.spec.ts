import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { SellerGuard } from './seller.guard';
import { AuthService } from '../services/auth.service';

describe('SellerGuard', () => {
  let guard: SellerGuard;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isSeller']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        SellerGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });

    guard = TestBed.inject(SellerGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate', () => {
    it('should allow access for seller', () => {
      authServiceSpy.isSeller.and.returnValue(true);

      const result = guard.canActivate();

      expect(result).toBeTrue();
      expect(routerSpy.navigate).not.toHaveBeenCalled();
    });

    it('should deny access and redirect for non-seller', () => {
      authServiceSpy.isSeller.and.returnValue(false);

      const result = guard.canActivate();

      expect(result).toBeFalse();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });

    it('should redirect client to home', () => {
      authServiceSpy.isSeller.and.returnValue(false);

      guard.canActivate();

      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });
  });
});
