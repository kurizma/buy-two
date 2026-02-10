import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { OrderService } from './order.service';
import { AuthService } from './auth.service';
import { Order, OrderStatus, PaymentMethod } from '../models/order/order.model';
import { CreateOrderRequest } from '../models/order/createOrderRequest.model';
import { environment } from '../../environments/environment.docker';

describe('OrderService', () => {
  let service: OrderService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

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
      country: 'Test Country',
      zipCode: '12345',
      phone: '+1234567890',
    },
    subtotal: 199.98,
    total: 199.98,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isSeller', 'isClient'], {
      currentUserValue: { id: 'user-1', role: 'CLIENT' },
    });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        OrderService,
        { provide: AuthService, useValue: authServiceSpy },
      ],
    });

    service = TestBed.inject(OrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('createOrder', () => {
    it('should create a new order', () => {
      const createReq: CreateOrderRequest = {
        shippingAddress: {
          fullName: 'Test User',
          street: '123 Main St',
          city: 'Test City',
          country: 'Test Country',
          zipCode: '12345',
          phone: '+1234567890',
        },
      };

      service.createOrder(createReq).subscribe((response) => {
        expect(response.success).toBeTrue();
        expect(response.data.orderNumber).toBe('ORD-001');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/checkout`);
      expect(req.request.method).toBe('POST');
      req.flush({ success: true, message: 'Created', data: mockOrder });
    });

    it('should handle create order error', () => {
      const createReq: CreateOrderRequest = {
        shippingAddress: {
          fullName: 'Test User',
          street: '123 Main St',
          city: 'Test City',
          country: 'Test Country',
          zipCode: '12345',
          phone: '+1234567890',
        },
      };

      service.createOrder(createReq).subscribe({
        error: (err) => {
          expect(err.status).toBe(400);
        },
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/checkout`);
      req.flush({ message: 'Bad Request' }, { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('getOrder', () => {
    it('should fetch order by number', () => {
      service.getOrder('ORD-001').subscribe((order) => {
        expect(order).toBeTruthy();
        expect(order?.orderNumber).toBe('ORD-001');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/ORD-001`);
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: mockOrder });
    });

    it('should return null on error', () => {
      service.getOrder('BAD-ORDER').subscribe((order) => {
        expect(order).toBeNull();
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/BAD-ORDER`);
      req.flush({ message: 'Not Found' }, { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getMyOrders', () => {
    it('should fetch buyer orders', () => {
      authServiceSpy.isSeller.and.returnValue(false);

      service.getMyOrders().subscribe((orders) => {
        expect(orders.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/buyer`);
      req.flush({ success: true, data: [mockOrder] });
    });

    it('should fetch seller orders with pagination', () => {
      authServiceSpy.isSeller.and.returnValue(true);

      service.getMyOrders().subscribe((orders) => {
        expect(orders.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/seller`);
      req.flush({ success: true, data: { content: [mockOrder] } });
    });

    it('should return empty array on error', () => {
      authServiceSpy.isSeller.and.returnValue(false);

      service.getMyOrders().subscribe((orders) => {
        expect(orders).toEqual([]);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/buyer`);
      req.flush({ message: 'Error' }, { status: 500, statusText: 'Server Error' });
    });

    it('should return empty array when no data', () => {
      authServiceSpy.isSeller.and.returnValue(false);

      service.getMyOrders().subscribe((orders) => {
        expect(orders).toEqual([]);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/buyer`);
      req.flush({ success: false, data: null });
    });
  });

  describe('cancelOrder', () => {
    it('should cancel an order', () => {
      service.cancelOrder('ORD-001').subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/ORD-001/cancel`);
      expect(req.request.method).toBe('POST');
      req.flush({ success: true });
    });
  });

  describe('redoOrder', () => {
    it('should redo a cancelled order', () => {
      service.redoOrder('ORD-001').subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/orders/ORD-001/redo`);
      expect(req.request.method).toBe('POST');
      req.flush({ success: true });
    });
  });
});
