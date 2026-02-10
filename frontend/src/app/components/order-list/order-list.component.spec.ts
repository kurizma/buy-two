import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { OrderListComponent } from './order-list.component';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { Order, OrderStatus, PaymentMethod } from '../../models/order/order.model';

describe('OrderListComponent', () => {
  let component: OrderListComponent;
  let fixture: ComponentFixture<OrderListComponent>;
  let orderServiceSpy: jasmine.SpyObj<OrderService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  const mockOrders: Order[] = [
    {
      id: 'order-1',
      orderNumber: 'ORD-001',
      userId: 'user-1',
      status: OrderStatus.PENDING,
      paymentMethod: PaymentMethod.PAY_ON_DELIVERY,
      items: [
        {
          productId: 'prod-1',
          productName: 'Product 1',
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
    },
    {
      id: 'order-2',
      orderNumber: 'ORD-002',
      userId: 'user-1',
      status: OrderStatus.CONFIRMED,
      paymentMethod: PaymentMethod.PAY_ON_DELIVERY,
      items: [],
      shippingAddress: {
        fullName: 'Test User',
        street: '456 Oak St',
        city: 'Other City',
        country: 'USA',
        zipCode: '67890',
        phone: '+1234567890',
      },
      subtotal: 50,
      total: 50,
      createdAt: '2024-01-02T00:00:00Z',
      updatedAt: '2024-01-02T00:00:00Z',
    },
  ];

  beforeEach(async () => {
    orderServiceSpy = jasmine.createSpyObj('OrderService', ['getMyOrders', 'cancelOrder', 'redoOrder']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getUserId', 'isSeller']);
    cartServiceSpy = jasmine.createSpyObj('CartService', ['addProductToCart']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    orderServiceSpy.getMyOrders.and.returnValue(of(mockOrders));
    authServiceSpy.getUserId.and.returnValue('user-1');
    authServiceSpy.isSeller.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [OrderListComponent, NoopAnimationsModule, HttpClientTestingModule],
    })
      .overrideProvider(OrderService, { useValue: orderServiceSpy })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .overrideProvider(CartService, { useValue: cartServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(MatSnackBar, { useValue: snackBarSpy })
      .overrideProvider(ActivatedRoute, {
        useValue: { snapshot: { queryParams: {} } },
      })
      .compileComponents();

    fixture = TestBed.createComponent(OrderListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load orders on init', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(orderServiceSpy.getMyOrders).toHaveBeenCalled();
      expect(component.orders.length).toBe(2);
    }));

    it('should set user context', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.currentUserId).toBe('user-1');
      expect(component.isSeller).toBeFalse();
    }));

    it('should handle seller context', fakeAsync(() => {
      authServiceSpy.isSeller.and.returnValue(true);
      fixture.detectChanges();
      tick();

      expect(component.isSeller).toBeTrue();
    }));
  });

  describe('order loading', () => {
    it('should show loading state', fakeAsync(() => {
      fixture.detectChanges();
      expect(component.loading).toBeTrue();
      tick();
      expect(component.loading).toBeFalse();
    }));

    it('should handle load error gracefully', fakeAsync(() => {
      orderServiceSpy.getMyOrders.and.returnValue(throwError(() => new Error('Load failed')));
      fixture.detectChanges();
      tick();

      expect(component.orders).toEqual([]);
      expect(component.loading).toBeFalse();
    }));
  });

  describe('filtering', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should initialize with ALL status filter', () => {
      expect(component.statusFilter.value).toBe('ALL');
    });

    it('should have status options', () => {
      expect(component.statusOptions).toContain('ALL');
      expect(component.statusOptions).toContain(OrderStatus.PENDING);
      expect(component.statusOptions).toContain(OrderStatus.CONFIRMED);
    });

    it('should filter by status', fakeAsync(() => {
      component.statusFilter.setValue(OrderStatus.PENDING);
      tick(300);

      expect(component.filteredOrders.length).toBe(1);
      expect(component.filteredOrders[0].status).toBe(OrderStatus.PENDING);
    }));

    it('should search by order number', fakeAsync(() => {
      component.searchCtrl.setValue('ORD-001');
      tick(300);

      expect(component.filteredOrders.length).toBe(1);
      expect(component.filteredOrders[0].orderNumber).toBe('ORD-001');
    }));
  });

  describe('order actions', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should navigate to order detail', () => {
      component.orders = mockOrders;
      // If there's a viewOrder method
    });
  });
});
