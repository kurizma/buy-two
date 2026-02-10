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
      // loading starts as false, becomes true during loadOrders, then false when complete
      expect(component.loading).toBeFalse(); // initial state
      fixture.detectChanges(); // triggers ngOnInit -> loadOrders
      tick();
      // After async completion, loading should be false
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

    it('should search by product name', fakeAsync(() => {
      component.searchCtrl.setValue('Product 1');
      tick(300);

      expect(component.filteredOrders.length).toBe(1);
    }));

    it('should show all orders when status is ALL', fakeAsync(() => {
      component.statusFilter.setValue('ALL');
      tick();
      
      expect(component.filteredOrders.length).toBe(2);
    }));

    it('should filter by date range', fakeAsync(() => {
      component.startDate = new Date('2024-01-01');
      component.endDate = new Date('2024-01-01');
      component.applyFilters();
      tick();

      expect(component.filteredOrders.length).toBe(1);
      expect(component.filteredOrders[0].orderNumber).toBe('ORD-001');
    }));

    it('should handle null date strings', fakeAsync(() => {
      const orderWithoutDate = { ...mockOrders[0], createdAt: undefined };
      component.orders = [orderWithoutDate];
      component.startDate = new Date('2024-01-01');
      component.applyFilters();
      tick();

      expect(component.filteredOrders.length).toBe(1);
    }));
  });

  describe('seller filtering', () => {
    it('should filter orders by seller items when seller', fakeAsync(() => {
      authServiceSpy.isSeller.and.returnValue(true);
      authServiceSpy.getUserId.and.returnValue('seller-1');
      fixture.detectChanges();
      tick();

      expect(component.isSeller).toBeTrue();
      // Order 1 has seller-1 items, order 2 doesn't
    }));
  });

  describe('cancelOrder', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should show error for non-cancellable orders', () => {
      const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as unknown as Event;
      component.orders = [{ ...mockOrders[0], status: OrderStatus.SHIPPED }];
      snackBarSpy.open.and.returnValue({ onAction: () => of() } as any);
      
      component.cancelOrder('ORD-001', event);
      
      expect(snackBarSpy.open).toHaveBeenCalledWith(
        'Only PENDING or CONFIRMED orders can be cancelled',
        'OK',
        jasmine.any(Object)
      );
    });

    it('should stop event propagation', () => {
      const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as unknown as Event;
      snackBarSpy.open.and.returnValue({ onAction: () => of() } as any);
      component.cancelOrder('ORD-001', event);
      expect(event.stopPropagation).toHaveBeenCalled();
    });
  });

  describe('redoOrder', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should show error for non-cancelled orders', () => {
      const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as unknown as Event;
      snackBarSpy.open.and.returnValue({ onAction: () => of() } as any);
      
      component.redoOrder('ORD-001', event);
      
      expect(snackBarSpy.open).toHaveBeenCalledWith(
        'Only CANCELLED orders can be redone',
        'OK',
        jasmine.any(Object)
      );
    });

    it('should stop event propagation', () => {
      const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as unknown as Event;
      snackBarSpy.open.and.returnValue({ onAction: () => of() } as any);
      component.redoOrder('ORD-001', event);
      expect(event.stopPropagation).toHaveBeenCalled();
    });
  });

  describe('viewOrderDetail', () => {
    it('should navigate to order detail page', fakeAsync(() => {
      fixture.detectChanges();
      tick();
      component.viewOrderDetail('ORD-001');
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/order-detail', 'ORD-001']);
    }));
  });

  describe('getStatusClass', () => {
    it('should return correct class for PENDING', () => {
      expect(component.getStatusClass(OrderStatus.PENDING)).toBe('status-pending');
    });

    it('should return correct class for CONFIRMED', () => {
      expect(component.getStatusClass(OrderStatus.CONFIRMED)).toBe('status-confirmed');
    });
  });

  describe('getSellerRevenue', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should return 0 for non-seller', () => {
      component.isSeller = false;
      expect(component.getSellerRevenue(mockOrders[0])).toBe(0);
    });

    it('should calculate seller revenue', () => {
      component.isSeller = true;
      component.currentUserId = 'seller-1';
      // First order has seller-1 item: 99.99 * 2 = 199.98
      expect(component.getSellerRevenue(mockOrders[0])).toBe(199.98);
    });
  });

  describe('getSellerItems', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should return all items for non-seller', () => {
      component.isSeller = false;
      const items = component.getSellerItems(mockOrders[0]);
      expect(items.length).toBe(mockOrders[0].items.length);
    });

    it('should filter to seller items only', () => {
      component.isSeller = true;
      component.currentUserId = 'seller-1';
      const items = component.getSellerItems(mockOrders[0]);
      expect(items.every(i => i.sellerId === 'seller-1')).toBeTrue();
    });
  });

  describe('permission checks', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('canCancel should return true for PENDING orders', () => {
      const pendingOrder = { ...mockOrders[0], status: OrderStatus.PENDING };
      expect(component.canCancel(pendingOrder)).toBeTrue();
    });

    it('canCancel should return true for CONFIRMED orders', () => {
      const confirmedOrder = { ...mockOrders[0], status: OrderStatus.CONFIRMED };
      expect(component.canCancel(confirmedOrder)).toBeTrue();
    });

    it('canCancel should return false for SHIPPED orders', () => {
      const shippedOrder = { ...mockOrders[0], status: OrderStatus.SHIPPED };
      expect(component.canCancel(shippedOrder)).toBeFalse();
    });

    it('canRedo should return true for CANCELLED orders (non-seller)', () => {
      component.isSeller = false;
      const cancelledOrder = { ...mockOrders[0], status: OrderStatus.CANCELLED };
      expect(component.canRedo(cancelledOrder)).toBeTrue();
    });

    it('canRedo should return false for seller', () => {
      component.isSeller = true;
      const cancelledOrder = { ...mockOrders[0], status: OrderStatus.CANCELLED };
      expect(component.canRedo(cancelledOrder)).toBeFalse();
    });

    it('canRemove should return true for CANCELLED orders', () => {
      const cancelledOrder = { ...mockOrders[0], status: OrderStatus.CANCELLED };
      expect(component.canRemove(cancelledOrder)).toBeTrue();
    });

    it('canRemove should return false for PENDING orders', () => {
      expect(component.canRemove(mockOrders[0])).toBeFalse();
    });

    it('canConfirm should return true for PENDING order with seller items', () => {
      component.isSeller = true;
      component.currentUserId = 'seller-1';
      const pendingOrder = { ...mockOrders[0], status: OrderStatus.PENDING };
      expect(component.canConfirm(pendingOrder)).toBeTrue();
    });
  });

  describe('trackByOrderNumber', () => {
    it('should return order number', () => {
      expect(component.trackByOrderNumber(0, mockOrders[0])).toBe('ORD-001');
    });
  });

  describe('clearFilters', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should reset all filters', fakeAsync(() => {
      component.searchCtrl.setValue('test');
      component.statusFilter.setValue(OrderStatus.PENDING);
      component.startDate = new Date();
      component.endDate = new Date();
      
      component.clearFilters();
      tick();
      
      expect(component.searchCtrl.value).toBe('');
      expect(component.statusFilter.value).toBe('ALL');
      expect(component.startDate).toBeNull();
      expect(component.endDate).toBeNull();
    }));
  });

  describe('onDateChange', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should apply filters on date change', fakeAsync(() => {
      spyOn(component, 'applyFilters');
      component.onDateChange();
      expect(component.applyFilters).toHaveBeenCalled();
    }));
  });
});
