import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { CheckoutComponent } from './checkout.component';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { PaymentMethod, OrderStatus, Order } from '../../models/order/order.model';

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

  const mockOrder: Order = {
    id: 'order-1',
    orderNumber: 'ORD-001',
    userId: 'user-1',
    status: OrderStatus.PENDING,
    paymentMethod: PaymentMethod.PAY_ON_DELIVERY,
    items: [],
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
      'getShippingCost',
      'getVatAmount',
      'clearCartAfterOrder',
    ], {
      cartItems$: cartItemsSubject.asObservable(),
    });
    cartServiceSpy.getTotal.and.returnValue(199.98);
    cartServiceSpy.getSubtotal.and.returnValue(199.98);
    cartServiceSpy.getShippingCost.and.returnValue(0);
    cartServiceSpy.getVatAmount.and.returnValue(20);

    orderServiceSpy = jasmine.createSpyObj('OrderService', ['createOrder']);

    await TestBed.configureTestingModule({
      imports: [CheckoutComponent, ReactiveFormsModule, FormsModule, NoopAnimationsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: CartService, useValue: cartServiceSpy },
        { provide: OrderService, useValue: orderServiceSpy },
      ],
    }).compileComponents();

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

    it('should require street', () => {
      const control = component.checkoutForm.get('street');
      control?.setValue('');
      expect(control?.valid).toBeFalse();
    });

    it('should require city', () => {
      const control = component.checkoutForm.get('city');
      control?.setValue('');
      expect(control?.valid).toBeFalse();
    });

    it('should require country', () => {
      const control = component.checkoutForm.get('country');
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

    it('should be invalid when review not confirmed', () => {
      expect(component.reviewForm.valid).toBeFalse();
    });

    it('should be valid when review confirmed', () => {
      component.reviewForm.patchValue({ confirmed: true });
      expect(component.reviewForm.valid).toBeTrue();
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

    it('should load saved address from localStorage', () => {
      const savedAddress = {
        fullName: 'Saved User',
        street: '123 Saved St',
        city: 'Saved City',
        zipCode: '12345',
        country: 'USA',
        phone: '+1234567890',
      };
      localStorage.setItem('saved-address-user-1', JSON.stringify(savedAddress));
      
      component.ngOnInit();
      
      expect(component.checkoutForm.get('fullName')?.value).toBe('Saved User');
    });

    it('should auto-save address changes', fakeAsync(() => {
      component.ngAfterViewInit();
      component.checkoutForm.patchValue({ fullName: 'Auto Saved' });
      tick();
      
      const saved = localStorage.getItem('saved-address-user-1');
      expect(saved).toBeTruthy();
      expect(JSON.parse(saved!).fullName).toBe('Auto Saved');
    }));
  });

  describe('confirmReview', () => {
    it('should set confirmed to true', () => {
      const mockStepper = { next: jasmine.createSpy('next') } as unknown as any;
      component.confirmReview(mockStepper);
      expect(component.reviewForm.get('confirmed')?.value).toBeTrue();
    });

    it('should advance stepper', () => {
      const mockStepper = { next: jasmine.createSpy('next') } as unknown as any;
      component.confirmReview(mockStepper);
      expect(mockStepper.next).toHaveBeenCalled();
    });
  });

  describe('placeOrder', () => {
    beforeEach(() => {
      component.checkoutForm.patchValue({
        fullName: 'Test User',
        street: '123 Test St',
        city: 'Test City',
        state: 'TS',
        zipCode: '12345',
        country: 'USA',
        phone: '+1234567890',
      });
      component.reviewForm.patchValue({ confirmed: true });
    });

    it('should call createOrder with address', () => {
      // Return response without orderNumber to avoid navigation
      orderServiceSpy.createOrder.and.returnValue(of({ success: true, data: { id: 'order-1' } } as any));
      
      component.placeOrder();
      
      expect(orderServiceSpy.createOrder).toHaveBeenCalled();
      const request = orderServiceSpy.createOrder.calls.mostRecent().args[0];
      expect(request.shippingAddress.fullName).toBe('Test User');
      expect(request.shippingAddress.street).toBe('123 Test St');
    });

    it('should not place order if form invalid', () => {
      component.checkoutForm.patchValue({ fullName: '' });
      
      component.placeOrder();
      
      expect(orderServiceSpy.createOrder).not.toHaveBeenCalled();
    });

    it('should not place order if review not confirmed', () => {
      component.reviewForm.patchValue({ confirmed: false });
      
      component.placeOrder();
      
      expect(orderServiceSpy.createOrder).not.toHaveBeenCalled();
    });

    it('should handle order error', fakeAsync(() => {
      orderServiceSpy.createOrder.and.returnValue(throwError(() => new Error('Order failed')));
      spyOn(console, 'error');
      
      component.placeOrder();
      tick(100);
      
      expect(console.error).toHaveBeenCalled();
    }));
  });
});