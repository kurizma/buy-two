import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, of } from 'rxjs';
import { CartComponent } from './cart.component';
import { CartService } from '../../services/cart.service';
import { CategoryService } from '../../services/category.service';
import { CartItem } from '../../models/cart/cart-item.model';

describe('CartComponent', () => {
  let component: CartComponent;
  let fixture: ComponentFixture<CartComponent>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let cartItemsSubject: BehaviorSubject<CartItem[]>;

  const mockCartItems: CartItem[] = [
    {
      id: 'item-1',
      productId: 'prod-1',
      productName: 'Test Product',
      sellerId: 'seller-1',
      sellerName: 'Test Seller',
      price: 99.99,
      quantity: 2,
      categoryId: 'cat-1',
      imageUrl: 'image.jpg',
    },
  ];

  beforeEach(async () => {
    cartItemsSubject = new BehaviorSubject<CartItem[]>(mockCartItems);
    
    cartServiceSpy = jasmine.createSpyObj('CartService', [
      'loadCart',
      'updateQuantity',
      'removeItem',
      'clearCart',
      'getSubtotal',
      'getItemCount',
    ], {
      cartItems$: cartItemsSubject.asObservable(),
    });

    categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['loadCategories', 'getCategorySlug']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    cartServiceSpy.getSubtotal.and.returnValue(199.98);
    cartServiceSpy.getItemCount.and.returnValue(2);
    categoryServiceSpy.getCategorySlug.and.returnValue('electronics');

    await TestBed.configureTestingModule({
      imports: [CartComponent, HttpClientTestingModule],
    })
      .overrideProvider(CartService, { useValue: cartServiceSpy })
      .overrideProvider(CategoryService, { useValue: categoryServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(MatSnackBar, { useValue: snackBarSpy })
      .overrideProvider(ActivatedRoute, { useValue: { snapshot: { queryParams: {} } } })
      .compileComponents();

    fixture = TestBed.createComponent(CartComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load cart on init', () => {
      fixture.detectChanges();
      expect(cartServiceSpy.loadCart).toHaveBeenCalled();
    });

    it('should load categories on init', () => {
      fixture.detectChanges();
      expect(categoryServiceSpy.loadCategories).toHaveBeenCalled();
    });

    it('should subscribe to cart items', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.cartItems.length).toBe(1);
      expect(component.cartItems[0].productName).toBe('Test Product');
    }));
  });

  describe('ngOnDestroy', () => {
    it('should unsubscribe on destroy', () => {
      fixture.detectChanges();
      const subscription = (component as any).subscription;
      spyOn(subscription, 'unsubscribe');

      component.ngOnDestroy();

      expect(subscription.unsubscribe).toHaveBeenCalled();
    });
  });

  describe('getCategorySlug', () => {
    it('should return category slug', () => {
      fixture.detectChanges();
      const slug = component.getCategorySlug('cat-1');
      expect(categoryServiceSpy.getCategorySlug).toHaveBeenCalledWith('cat-1');
      expect(slug).toBe('electronics');
    });
  });

  describe('updateQuantity', () => {
    it('should call cart service updateQuantity', () => {
      fixture.detectChanges();
      component.updateQuantity('prod-1', 5);
      expect(cartServiceSpy.updateQuantity).toHaveBeenCalledWith('prod-1', 5);
    });
  });

  describe('increaseQuantity', () => {
    it('should increase quantity by 1', () => {
      fixture.detectChanges();
      component.increaseQuantity('prod-1');
      expect(cartServiceSpy.updateQuantity).toHaveBeenCalledWith('prod-1', 3);
    });

    it('should do nothing for non-existent product', () => {
      fixture.detectChanges();
      component.increaseQuantity('non-existent');
      expect(cartServiceSpy.updateQuantity).not.toHaveBeenCalled();
    });
  });

  describe('decreaseQuantity', () => {
    it('should decrease quantity by 1', () => {
      fixture.detectChanges();
      component.decreaseQuantity('prod-1');
      expect(cartServiceSpy.updateQuantity).toHaveBeenCalledWith('prod-1', 1);
    });

    it('should do nothing for non-existent product', () => {
      fixture.detectChanges();
      component.decreaseQuantity('non-existent');
      expect(cartServiceSpy.updateQuantity).not.toHaveBeenCalled();
    });
  });

  describe('removeItem', () => {
    it('should call cart service removeItem', () => {
      fixture.detectChanges();
      component.removeItem('prod-1');
      expect(cartServiceSpy.removeItem).toHaveBeenCalledWith('prod-1');
    });
  });

  describe('clearCart', () => {
    it('should call cart service clearCart', () => {
      fixture.detectChanges();
      component.clearCart();
      expect(cartServiceSpy.clearCart).toHaveBeenCalled();
    });
  });

  describe('cart items updates', () => {
    it('should update items when cart changes', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      // Emit new items
      const newItems: CartItem[] = [
        ...mockCartItems,
        { ...mockCartItems[0], id: 'item-2', productId: 'prod-2', productName: 'New Product' },
      ];
      cartItemsSubject.next(newItems);
      tick();

      expect(component.cartItems.length).toBe(2);
    }));

    it('should handle empty cart', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      cartItemsSubject.next([]);
      tick();

      expect(component.cartItems.length).toBe(0);
    }));
  });
});
