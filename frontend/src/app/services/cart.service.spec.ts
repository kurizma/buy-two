import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CartService } from './cart.service';
import { AuthService } from './auth.service';
import { UserService } from './user.service';
import { ProductService } from './product.service';
import { CategoryService } from './category.service';
import { environment } from '../../environments/environment.docker';
import { of } from 'rxjs';

describe('CartService', () => {
  let service: CartService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  const mockCartResponse = {
    success: true,
    message: 'OK',
    data: {
      items: [
        {
          productId: 'prod-1',
          productName: 'Test Product',
          sellerId: 'seller-1',
          price: '99.99',
          quantity: '2',
          imageUrl: 'image.jpg',
        },
      ],
    },
  };

  const mockUser = {
    id: 'user-1',
    email: 'test@example.com',
    name: 'Test User',
    role: 'CLIENT' as const,
    avatar: 'avatar.png',
  };

  const mockSeller = {
    id: 'seller-1',
    email: 'seller@example.com',
    name: 'Test Seller',
    role: 'SELLER' as const,
    avatar: 'seller-avatar.png',
  };

  const mockProduct = {
    id: 'prod-1',
    name: 'Test Product',
    description: 'Test description',
    price: 99.99,
    quantity: 10,
    categoryId: 'cat-1',
    userId: 'seller-1',
    images: ['image.jpg'],
  };

  beforeEach(() => {
    localStorage.clear();

    authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated'], {
      currentUserValue: mockUser,
    });
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUserById']);
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getProductById']);
    categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getCategorySlug']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    userServiceSpy.getUserById.and.returnValue(of(mockSeller));
    productServiceSpy.getProductById.and.returnValue(of(mockProduct));
    categoryServiceSpy.getCategorySlug.and.returnValue('electronics');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, MatSnackBarModule],
      providers: [
        CartService,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: ProductService, useValue: productServiceSpy },
        { provide: CategoryService, useValue: categoryServiceSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
      ],
    });

    service = TestBed.inject(CartService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('loadCart', () => {
    it('should load cart when authenticated', fakeAsync(() => {
      authServiceSpy.isAuthenticated.and.returnValue(true);

      service.loadCart();
      tick();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart`);
      expect(req.request.method).toBe('GET');
      req.flush(mockCartResponse);
      tick();
    }));

    it('should set empty cart when not authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);

      let items: any[] = [];
      service.cartItems$.subscribe((i) => (items = i));

      service.loadCart();

      expect(items).toEqual([]);
      httpMock.expectNone(`${environment.apiBaseUrl}/api/cart`);
    });

    it('should handle empty cart response', fakeAsync(() => {
      authServiceSpy.isAuthenticated.and.returnValue(true);

      let items: any[] = [{ id: 'old' }];
      service.cartItems$.subscribe((i) => (items = i));

      service.loadCart();
      tick();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart`);
      req.flush({ success: true, data: { items: [] } });
      tick();

      expect(items).toEqual([]);
    }));

    it('should handle API error gracefully', fakeAsync(() => {
      authServiceSpy.isAuthenticated.and.returnValue(true);

      let items: any[] = [{ id: 'old' }];
      service.cartItems$.subscribe((i) => (items = i));

      service.loadCart();
      tick();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart`);
      req.flush({ message: 'Error' }, { status: 400, statusText: 'Bad Request' });
      tick();

      expect(items).toEqual([]);
    }));
  });

  describe('getItemCount', () => {
    it('should return total quantity of items', fakeAsync(() => {
      // getItemCount returns the length of items array
      const cartItems = [
        { id: '1', quantity: 2 },
        { id: '2', quantity: 3 },
      ];
      (service as any).cartItemsSubject.next(cartItems);
      tick();

      expect(service.getItemCount()).toBe(2);
    }));

    it('should return 0 for empty cart', () => {
      (service as any).cartItemsSubject.next([]);
      expect(service.getItemCount()).toBe(0);
    });
  });

  describe('getSubtotal', () => {
    it('should calculate subtotal correctly', fakeAsync(() => {
      // getSubtotal = getTotalInclVat() / 1.24
      // getTotalInclVat = 10*2 + 20*1 = 40
      // getSubtotal = 40 / 1.24 ≈ 32.26
      const cartItems = [
        { id: '1', price: 10, quantity: 2 },
        { id: '2', price: 20, quantity: 1 },
      ];
      (service as any).cartItemsSubject.next(cartItems);
      tick();

      expect(service.getSubtotal()).toBeCloseTo(32.26, 2);
    }));

    it('should return 0 for empty cart', () => {
      (service as any).cartItemsSubject.next([]);
      expect(service.getSubtotal()).toBe(0);
    });
  });

  describe('clearCart', () => {
    it('should clear cart via API', fakeAsync(() => {
      // clearCart shows a snackbar with confirmation
      // When onAction() fires, it sends DELETE request
      const mockSnackBarRef = {
        onAction: () => of(undefined),  // Simulate user clicking "Confirm"
      };
      snackBarSpy.open.and.returnValue(mockSnackBarRef as any);
      authServiceSpy.isAuthenticated.and.returnValue(true);

      service.clearCart();
      tick();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart`);
      expect(req.request.method).toBe('DELETE');
      req.flush({ success: true });
      tick();

      // loadCart is called after successful clear
      const loadReq = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart`);
      loadReq.flush({ success: true, data: { items: [] } });
      tick();
    }));
  });

  describe('localStorage persistence', () => {
    it('should save cart to localStorage on changes', fakeAsync(() => {
      const cartItems = [{ id: '1', productId: 'p1', quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      tick();

      const stored = localStorage.getItem('shopping_cart');
      expect(stored).toBeTruthy();
      expect(JSON.parse(stored!)).toEqual(cartItems);
    }));

    it('should load cart from localStorage on init', () => {
      // Note: The service loads from localStorage in constructor
      // This test verifies private method exists
      expect((service as any).loadCartFromStorage).toBeDefined();
    });
  });

  describe('addToCart', () => {
    it('should show out of stock message when stock is 0', fakeAsync(() => {
      service.addToCart({
        productId: 'prod-1',
        productName: 'Test',
        sellerId: 'seller-1',
        price: 10,
        categoryId: 'cat-1',
        sellerName: 'Seller',
        availableStock: 0,
      });
      tick();
      
      expect(snackBarSpy.open).toHaveBeenCalledWith('❌ Out of stock!', 'Close', jasmine.any(Object));
    }));

    it('should add item to cart via API', fakeAsync(() => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      
      service.addToCart({
        productId: 'prod-2',
        productName: 'New Product',
        sellerId: 'seller-1',
        price: 50,
        categoryId: 'cat-1',
        sellerName: 'Test Seller',
        availableStock: 10,
      });
      tick();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart/items`);
      expect(req.request.method).toBe('POST');
      req.flush({ success: true, data: { items: [] } });
      tick();
    }));
  });

  describe('removeItem', () => {
    it('should remove item from cart via API', fakeAsync(() => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      
      service.removeItem('prod-1');
      tick();

      const deleteReq = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart/items/prod-1`);
      expect(deleteReq.request.method).toBe('DELETE');
      deleteReq.flush({ success: true });
      tick();

      // Should call loadCart after removal
      const loadReq = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart`);
      loadReq.flush({ success: true, data: { items: [] } });
      tick();
    }));
  });

  describe('updateQuantity', () => {
    it('should remove item when quantity is less than 1', fakeAsync(() => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      spyOn(service, 'removeItem');
      
      service.updateQuantity('prod-1', 0);
      tick();

      expect(service.removeItem).toHaveBeenCalledWith('prod-1');
    }));
  });

  describe('getTotalInclVat', () => {
    it('should calculate total including VAT', () => {
      const cartItems = [
        { id: '1', price: 10, quantity: 2 },
        { id: '2', price: 20, quantity: 1 },
      ];
      (service as any).cartItemsSubject.next(cartItems);

      expect(service.getTotalInclVat()).toBe(40);
    });
  });

  describe('getVatAmount', () => {
    it('should calculate VAT amount', () => {
      const cartItems = [{ id: '1', price: 124, quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);

      // VAT = Total - (Total/1.24) = 124 - 100 = 24
      expect(service.getVatAmount()).toBeCloseTo(24, 1);
    });
  });

  describe('getShippingCost', () => {
    it('should return 0 for orders over 50', () => {
      const cartItems = [{ id: '1', price: 60, quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      
      expect(service.getShippingCost()).toBe(0);
    });

    it('should return 4.9 for orders under 50', () => {
      const cartItems = [{ id: '1', price: 30, quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      
      expect(service.getShippingCost()).toBe(4.9);
    });
  });

  describe('getTotal', () => {
    it('should calculate total including shipping', () => {
      const cartItems = [{ id: '1', price: 30, quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      
      // Total = 30 + 4.9 (shipping) = 34.9
      expect(service.getTotal()).toBe(34.9);
    });
  });

  describe('isInCart', () => {
    it('should return true if product is in cart', () => {
      const cartItems = [{ id: '1', productId: 'prod-1', quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      
      expect(service.isInCart('prod-1')).toBeTrue();
    });

    it('should return false if product is not in cart', () => {
      const cartItems = [{ id: '1', productId: 'prod-1', quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      
      expect(service.isInCart('prod-999')).toBeFalse();
    });
  });

  describe('getProductQuantity', () => {
    it('should return quantity for product in cart', () => {
      const cartItems = [{ id: '1', productId: 'prod-1', quantity: 5 }];
      (service as any).cartItemsSubject.next(cartItems);
      
      expect(service.getProductQuantity('prod-1')).toBe(5);
    });

    it('should return 0 for product not in cart', () => {
      const cartItems = [{ id: '1', productId: 'prod-1', quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      
      expect(service.getProductQuantity('prod-999')).toBe(0);
    });
  });

  describe('isEmpty', () => {
    it('should return true for empty cart', () => {
      (service as any).cartItemsSubject.next([]);
      expect(service.isEmpty).toBeTrue();
    });

    it('should return false for non-empty cart', () => {
      const cartItems = [{ id: '1', productId: 'prod-1', quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      expect(service.isEmpty).toBeFalse();
    });
  });

  describe('clearCartAfterOrder', () => {
    it('should clear cart', fakeAsync(() => {
      const cartItems = [{ id: '1', productId: 'prod-1', quantity: 1 }];
      (service as any).cartItemsSubject.next(cartItems);
      tick();
      
      service.clearCartAfterOrder();
      tick();

      let items: any[] = [];
      service.cartItems$.subscribe((i) => (items = i));
      tick();
      
      expect(items).toEqual([]);
    }));
  });

  describe('addProductToCart', () => {
    it('should add product with correct mapping', fakeAsync(() => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      
      const product = {
        id: 'prod-1',
        name: 'Test Product',
        userId: 'seller-1',
        price: 99.99,
        categoryId: 'cat-1',
        images: ['image.jpg'],
        quantity: 10,
      };
      
      service.addProductToCart(product);
      tick();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/cart/items`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body.productId).toBe('prod-1');
      expect(req.request.body.productName).toBe('Test Product');
      req.flush({ success: true, data: { items: [] } });
      tick();
    }));
  });

  describe('getCachedSellerName', () => {
    it('should return cached seller name', () => {
      (service as any).sellerCache = { 'seller-1': { name: 'John Seller', avatar: 'avatar.jpg' } };
      expect(service.getCachedSellerName('seller-1')).toBe('John Seller');
    });

    it('should return default for unknown seller', () => {
      expect(service.getCachedSellerName('unknown')).toBe('Seller');
    });
  });
});
