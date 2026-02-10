import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductGridCardComponent } from './products-grid-card.component';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { ProductResponse } from '../../models/products/product-response.model';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ProductGridCardComponent', () => {
  let component: ProductGridCardComponent;
  let fixture: ComponentFixture<ProductGridCardComponent>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  const mockProduct: ProductResponse = {
    id: 'prod-1',
    name: 'Test Product',
    description: 'Test description',
    price: 99.99,
    quantity: 10,
    categoryId: 'cat-1',
    userId: 'seller-1',
    images: [],
  };

  beforeEach(async () => {
    cartServiceSpy = jasmine.createSpyObj('CartService', ['getProductQuantity', 'addProductToCart', 'isInCart']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'isSeller']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    cartServiceSpy.getProductQuantity.and.returnValue(0);
    cartServiceSpy.isInCart.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [ProductGridCardComponent, NoopAnimationsModule, HttpClientTestingModule],
    })
      .overrideProvider(CartService, { useValue: cartServiceSpy })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(MatSnackBar, { useValue: snackBarSpy })
      .compileComponents();

    fixture = TestBed.createComponent(ProductGridCardComponent);
    component = fixture.componentInstance;
    component.product = mockProduct;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onView', () => {
    it('should emit view event with product id', () => {
      spyOn(component.view, 'emit');

      component.onView();

      expect(component.view.emit).toHaveBeenCalledWith('prod-1');
    });
  });

  describe('addToCart', () => {
    it('should show out of stock message when quantity is 0', () => {
      const outOfStockProduct = { ...mockProduct, quantity: 0 };

      component.addToCart(outOfStockProduct);

      expect(snackBarSpy.open).toHaveBeenCalledWith('❌ Out of stock!', '', jasmine.any(Object));
    });

    it('should show stock limit message when exceeding available stock', () => {
      cartServiceSpy.getProductQuantity.and.returnValue(10);
      const product = { ...mockProduct, quantity: 10 };

      component.addToCart(product);

      expect(snackBarSpy.open).toHaveBeenCalledWith('⚠️ Only 10 left in stock!', '', jasmine.any(Object));
    });
  });

  describe('input properties', () => {
    it('should accept product input', () => {
      expect(component.product).toEqual(mockProduct);
    });

    it('should have default showSeller as true', () => {
      expect(component.showSeller).toBeTrue();
    });

    it('should have default showCategory as true', () => {
      expect(component.showCategory).toBeTrue();
    });

    it('should accept seller input', () => {
      const mockSeller = { id: 'seller-1', name: 'Test Seller' };
      component.seller = mockSeller as any;
      expect(component.seller).toEqual(mockSeller as any);
    });

    it('should accept categoryName input', () => {
      component.categoryName = 'Electronics';
      expect(component.categoryName).toBe('Electronics');
    });
  });
});
