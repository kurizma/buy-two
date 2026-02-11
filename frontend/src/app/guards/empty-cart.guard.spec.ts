import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { EmptyCartGuard } from './empty-cart.guard';
import { CartService } from '../services/cart.service';

describe('EmptyCartGuard', () => {
  let guard: EmptyCartGuard;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    cartServiceSpy = jasmine.createSpyObj('CartService', ['getItemCount']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        EmptyCartGuard,
        { provide: CartService, useValue: cartServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });

    guard = TestBed.inject(EmptyCartGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate', () => {
    it('should allow access when cart has items', async () => {
      cartServiceSpy.getItemCount.and.returnValue(3);

      const result = await guard.canActivate();

      expect(result).toBeTrue();
      expect(routerSpy.navigate).not.toHaveBeenCalled();
    });

    it('should deny access and redirect when cart is empty', async () => {
      cartServiceSpy.getItemCount.and.returnValue(0);

      const result = await guard.canActivate();

      expect(result).toBeFalse();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/product-listing']);
    });

    it('should redirect to product listing when empty', async () => {
      cartServiceSpy.getItemCount.and.returnValue(0);

      await guard.canActivate();

      expect(routerSpy.navigate).toHaveBeenCalledWith(['/product-listing']);
    });

    it('should allow checkout with single item', async () => {
      cartServiceSpy.getItemCount.and.returnValue(1);

      const result = await guard.canActivate();

      expect(result).toBeTrue();
    });
  });
});
