import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProductCardComponent } from './product-card.component';
import { ProductService } from '../../services/product.service';
import { CategoryService } from '../../services/category.service';
import { UserService } from '../../services/user.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { ProductResponse } from '../../models/products/product-response.model';

describe('ProductCardComponent', () => {
  let component: ProductCardComponent;
  let fixture: ComponentFixture<ProductCardComponent>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let router: Router;

  const mockProduct: ProductResponse = {
    id: 'prod-1',
    name: 'Cool T-Shirt',
    description: 'A great t-shirt',
    price: 19.99,
    images: ['tshirt.jpg'],
    categoryId: 'cat-1',
    userId: 'seller-1',
    quantity: 50,
  };

  const mockSeller = {
    id: 'seller-1',
    email: 'seller@test.com',
    name: 'Test Seller',
    role: 'SELLER' as const,
    avatar: 'seller.jpg',
  };

  const mockCategory = {
    id: 'cat-1',
    slug: 'clothing',
    name: 'Clothing',
    icon: 'shirt',
    description: 'Clothing items',
  };

  beforeEach(async () => {
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getProductById']);
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUserById']);
    categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getCategoryById']);
    cartServiceSpy = jasmine.createSpyObj('CartService', ['getProductQuantity', 'addProductToCart', 'isInCart']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isSeller']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    productServiceSpy.getProductById.and.returnValue(of(mockProduct));
    userServiceSpy.getUserById.and.returnValue(of(mockSeller));
    categoryServiceSpy.getCategoryById.and.returnValue(of(mockCategory));
    cartServiceSpy.getProductQuantity.and.returnValue(0);
    cartServiceSpy.isInCart.and.returnValue(false);
    authServiceSpy.isSeller.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [ProductCardComponent, HttpClientTestingModule, RouterTestingModule],
    })
      .overrideProvider(ProductService, { useValue: productServiceSpy })
      .overrideProvider(UserService, { useValue: userServiceSpy })
      .overrideProvider(CategoryService, { useValue: categoryServiceSpy })
      .overrideProvider(CartService, { useValue: cartServiceSpy })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .overrideProvider(MatSnackBar, { useValue: snackBarSpy })
      .overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: {
            paramMap: { get: jasmine.createSpy('get').and.returnValue('prod-1') },
          },
        },
      })
      .compileComponents();

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(ProductCardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load product on init', fakeAsync(() => {
      fixture.detectChanges();
      tick();
      
      expect(productServiceSpy.getProductById).toHaveBeenCalledWith('prod-1');
      expect(component.product).toEqual(mockProduct);
    }));

    it('should load seller after product loads', fakeAsync(() => {
      fixture.detectChanges();
      tick();
      
      expect(userServiceSpy.getUserById).toHaveBeenCalledWith('seller-1');
      expect(component.seller).toEqual(mockSeller);
    }));

    it('should load category after product loads', fakeAsync(() => {
      fixture.detectChanges();
      tick();
      
      expect(categoryServiceSpy.getCategoryById).toHaveBeenCalledWith('cat-1');
      expect(component.category).toEqual(mockCategory);
    }));

    it('should handle product load error', fakeAsync(() => {
      productServiceSpy.getProductById.and.returnValue(throwError(() => new Error('Not found')));
      fixture.detectChanges();
      tick();
      
      expect(component.errorMessage).toBeTruthy();
    }));
  });

  describe('addToCart', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should show out of stock message when quantity is 0', () => {
      const outOfStockProduct = { ...mockProduct, quantity: 0 };
      component.addToCart(outOfStockProduct);
      
      expect(snackBarSpy.open).toHaveBeenCalledWith('❌ Out of stock!', '', jasmine.any(Object));
      expect(cartServiceSpy.addProductToCart).not.toHaveBeenCalled();
    });

    it('should show stock limit message when exceeding available', () => {
      cartServiceSpy.getProductQuantity.and.returnValue(50);
      component.addToCart(mockProduct);
      
      expect(snackBarSpy.open).toHaveBeenCalledWith('⚠️ Only 50 left in stock!', '', jasmine.any(Object));
      expect(cartServiceSpy.addProductToCart).not.toHaveBeenCalled();
    });

    it('should add product to cart when stock available', () => {
      cartServiceSpy.getProductQuantity.and.returnValue(0);
      component.addToCart(mockProduct);
      
      expect(cartServiceSpy.addProductToCart).toHaveBeenCalled();
    });

    it('should include seller info in cart product', () => {
      component.seller = mockSeller;
      component.addToCart(mockProduct);
      
      const addedProduct = cartServiceSpy.addProductToCart.calls.mostRecent().args[0];
      expect(addedProduct.sellerName).toBe('Test Seller');
    });
  });

  describe('isSeller', () => {
    it('should return true when user is seller', () => {
      authServiceSpy.isSeller.and.returnValue(true);
      expect(component.isSeller()).toBeTrue();
    });

    it('should return false when user is not seller', () => {
      authServiceSpy.isSeller.and.returnValue(false);
      expect(component.isSeller()).toBeFalse();
    });
  });

  describe('isInCart', () => {
    it('should return true when product is in cart', () => {
      cartServiceSpy.isInCart.and.returnValue(true);
      expect(component.isInCart('prod-1')).toBeTrue();
    });

    it('should return false when product is not in cart', () => {
      cartServiceSpy.isInCart.and.returnValue(false);
      expect(component.isInCart('prod-1')).toBeFalse();
    });
  });

  describe('UI display', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should display product name', () => {
      const h2Element = fixture.nativeElement.querySelector('h2.card-title');
      expect(h2Element?.textContent).toContain('Cool T-Shirt');
    });

    it('should display product price', () => {
      const priceElement = fixture.nativeElement.querySelector('.price span.text-primary');
      expect(priceElement?.textContent).toContain('19.99');
    });

    it('should display product description', () => {
      expect(fixture.nativeElement.textContent).toContain('A great t-shirt');
    });
  });
});
