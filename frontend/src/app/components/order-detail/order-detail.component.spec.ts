import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { OrderDetailComponent } from './order-detail.component';
import { OrderService } from '../../services/order.service';
import { CartService } from '../../services/cart.service';
import { Order, OrderStatus, PaymentMethod } from '../../models/order/order.model';

describe('OrderDetailComponent', () => {
  let component: OrderDetailComponent;
  let fixture: ComponentFixture<OrderDetailComponent>;
  let orderServiceSpy: jasmine.SpyObj<OrderService>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockOrder: Order = {
    id: 'order-1',
    orderNumber: 'ORD-001',
    userId: 'user-1',
    status: OrderStatus.PENDING,
    paymentMethod: PaymentMethod.PAY_ON_DELIVERY,
    items: [
      {
        productId: 'prod-1',
        productName: 'Test Product',
        sellerId: 'seller-1',
        sellerName: 'Test Seller',
        price: 99.99,
        quantity: 2,
      },
    ],
    shippingAddress: {
      fullName: 'Test User',
      street: '123 Main St',
      city: 'Test City',
      country: 'USA',
      zipCode: '12345',
      phone: '+1234567890',
    },
    subtotal: 199.98,
    total: 199.98,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  };

  function createComponent(orderNumber: string | null = 'ORD-001') {
    return TestBed.configureTestingModule({
      imports: [OrderDetailComponent, NoopAnimationsModule, HttpClientTestingModule],
    })
      .overrideProvider(OrderService, { useValue: orderServiceSpy })
      .overrideProvider(CartService, { useValue: cartServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: {
            paramMap: convertToParamMap({ orderNumber }),
          },
        },
      })
      .compileComponents();
  }

  beforeEach(() => {
    orderServiceSpy = jasmine.createSpyObj('OrderService', ['getOrder']);
    cartServiceSpy = jasmine.createSpyObj('CartService', ['getSellerName', 'getCachedSellerName']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    orderServiceSpy.getOrder.and.returnValue(of(mockOrder));
    cartServiceSpy.getSellerName.and.returnValue(of('Test Seller'));
    cartServiceSpy.getCachedSellerName.and.returnValue('Test Seller');
  });

  describe('with valid order number', () => {
    beforeEach(async () => {
      await createComponent('ORD-001');
      fixture = TestBed.createComponent(OrderDetailComponent);
      component = fixture.componentInstance;
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should load order on init', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(orderServiceSpy.getOrder).toHaveBeenCalledWith('ORD-001');
      expect(component.order).toEqual(mockOrder);
      expect(component.loading).toBeFalse();
      expect(component.error).toBeFalse();
    }));

    it('should load seller names for order items', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(cartServiceSpy.getSellerName).toHaveBeenCalledWith('seller-1');
    }));

    it('should handle order load error', fakeAsync(() => {
      orderServiceSpy.getOrder.and.returnValue(throwError(() => new Error('Load error')));
      fixture.detectChanges();
      tick();

      expect(component.error).toBeTrue();
      expect(component.loading).toBeFalse();
    }));
  });

  describe('without order number', () => {
    beforeEach(async () => {
      await createComponent(null);
      fixture = TestBed.createComponent(OrderDetailComponent);
      component = fixture.componentInstance;
    });

    it('should show error when no order number', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.error).toBeTrue();
      expect(component.loading).toBeFalse();
      expect(orderServiceSpy.getOrder).not.toHaveBeenCalled();
    }));
  });

  describe('getStatusClass', () => {
    beforeEach(async () => {
      await createComponent('ORD-001');
      fixture = TestBed.createComponent(OrderDetailComponent);
      component = fixture.componentInstance;
    });

    it('should return correct class for PENDING', () => {
      expect(component.getStatusClass(OrderStatus.PENDING)).toBe('status-pending');
    });

    it('should return correct class for CONFIRMED', () => {
      expect(component.getStatusClass(OrderStatus.CONFIRMED)).toBe('status-confirmed');
    });

    it('should return correct class for SHIPPED', () => {
      expect(component.getStatusClass(OrderStatus.SHIPPED)).toBe('status-shipped');
    });

    it('should return correct class for DELIVERED', () => {
      expect(component.getStatusClass(OrderStatus.DELIVERED)).toBe('status-delivered');
    });

    it('should return correct class for CANCELLED', () => {
      expect(component.getStatusClass(OrderStatus.CANCELLED)).toBe('status-cancelled');
    });
  });
});
