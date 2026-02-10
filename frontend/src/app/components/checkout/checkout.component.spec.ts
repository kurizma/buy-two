import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { CheckoutComponent } from './checkout.component';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { PaymentMethod } from '../../models/order/order.model';

describe('CheckoutComponent', () => {
  let component: CheckoutComponent;
  let fixture: ComponentFixture<CheckoutComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let orderServiceSpy: jasmine.SpyObj<OrderService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let cartItemsSubject: BehaviorSubject<any[]>;

  const mockCartItems = [
    {
      id: 'item-1',
      productId: 'prod-1',
      productName: 'Test Product',
      price: 99.99,
      quantity: 2,
      sellerId: 'seller-1',
    },
  ];

  const mockOrder = {
    id: 'order-1',
    orderNumber: 'ORD-001',
    status: 'PENDING',
    total: 199.98,
  };

  beforeEach(async () => {
    localStorage.clear();
    cartItemsSubject = new BehaviorSubject(mockCartItems);

    authServiceSpy = jasmine.createSpyObj('AuthService', ['getUserId'], {
      currentUserValue: { id: 'user-1', name: 'Test User' },
    });
    authServiceSpy.getUserId.and.returnValue('user-1');

    cartServiceSpy = jasmine.createSpyObj('CartService', [
      'getTotal',
      'getSubtotal',
      'clearCart',
    ], {
      cartItems$: cartItemsSubject.asObservable(),
    });
    cartServiceSpy.getTotal.and.returnValue(199.98);
    cartServiceSpy.getSubtotal.and.returnValue(199.98);

    orderServiceSpy = jasmine.createSpyObj('OrderService', ['createOrder']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CheckoutComponent, ReactiveFormsModule, FormsModule, NoopAnimationsModule, HttpClientTestingModule],
    })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .overrideProvider(CartService, { useValue: cartServiceSpy })
      .overrideProvider(OrderService, { useValue: orderServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(ActivatedRoute, { useValue: { snapshot: { queryParams: {} } } })
      .compileComponents();

    fixture = TestBed.createComponent(CheckoutComponent);
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
    it('should create checkout form with required fields', () => {
      expect(component.checkoutForm.contains('fullName')).toBeTrue();
      expect(component.checkoutForm.contains('street')).toBeTrue();
      expect(component.checkoutForm.contains('city')).toBeTrue();
      expect(component.checkoutForm.contains('zipCode')).toBeTrue();
      expect(component.checkoutForm.contains('country')).toBeTrue();
      expect(component.checkoutForm.contains('phone')).toBeTrue();
    });

    it('should create review form', () => {
      expect(component.reviewForm.contains('confirmed')).toBeTrue();
    });

    it('should have default payment method', () => {
      expect(component.selectedPayment).toBe(PaymentMethod.PAY_ON_DELIVERY);
    });
  });

  describe('form validation', () => {
    it('should require fullName', () => {
      const control = component.checkoutForm.get('fullName');
      control?.setValue('');
      expect(control?.valid).toBeFalse();
    });

    it('should validate zipCode pattern', () => {
      const control = component.checkoutForm.get('zipCode');
      
      control?.setValue('invalid');
      expect(control?.errors?.['pattern']).toBeTruthy();

      control?.setValue('12345');
      expect(control?.errors).toBeNull();
    });

    it('should validate phone pattern', () => {
      const control = component.checkoutForm.get('phone');
      
      control?.setValue('123');
      expect(control?.errors?.['pattern']).toBeTruthy();

      control?.setValue('+1234567890');
      expect(control?.errors).toBeNull();
    });
  });

  describe('cart subscription', () => {
    it('should update cart items on change', fakeAsync(() => {
      tick();
      expect(component.cartItems.length).toBe(1);
      expect(component.total).toBe(199.98);
    }));

    it('should handle empty cart', fakeAsync(() => {
      cartItemsSubject.next([]);
      tick();
      expect(component.cartItems.length).toBe(0);
    }));
  });

  describe('saved address', () => {
    it('should generate correct saved address key', () => {
      expect(component.savedAddressKey).toBe('saved-address-user-1');
    });
  });
});
