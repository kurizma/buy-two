import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { SellerShopComponent } from './seller-shop.component';
import { ProductService } from '../../services/product.service';
import { UserService } from '../../services/user.service';
import { CategoryService } from '../../services/category.service';
import { BehaviorSubject } from 'rxjs';

describe('SellerShopComponent', () => {
  let component: SellerShopComponent;
  let fixture: ComponentFixture<SellerShopComponent>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockSeller = {
    id: 'seller-1',
    name: 'Test Seller',
    email: 'seller@test.com',
    role: 'SELLER',
  };

  const mockProducts = [
    {
      id: 'prod-1',
      name: 'Test Product',
      description: 'Test description',
      price: 99.99,
      quantity: 10,
      categoryId: 'cat-1',
      userId: 'seller-1',
      images: [],
    },
  ];

  const mockCategories = [
    { id: 'cat-1', name: 'Electronics', slug: 'electronics', icon: 'devices', description: 'Electronics' },
  ];

  function createComponent(sellerId: string | null) {
    return TestBed.configureTestingModule({
      imports: [SellerShopComponent, HttpClientTestingModule],
    })
      .overrideProvider(ProductService, { useValue: productServiceSpy })
      .overrideProvider(UserService, { useValue: userServiceSpy })
      .overrideProvider(CategoryService, { useValue: categoryServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: {
            paramMap: convertToParamMap({ id: sellerId }),
          },
        },
      })
      .compileComponents();
  }

  beforeEach(() => {
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getProductsBySeller', 'getProducts'], {
      products$: new BehaviorSubject(mockProducts).asObservable(),
    });
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUserById']);
    categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getCategories']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    userServiceSpy.getUserById.and.returnValue(of(mockSeller as any));
    categoryServiceSpy.getCategories.and.returnValue(of(mockCategories as any));
    productServiceSpy.getProducts.and.returnValue(of(mockProducts));
    productServiceSpy.getProductsBySeller.and.returnValue(of(mockProducts));
  });

  describe('with valid seller id', () => {
    beforeEach(async () => {
      await createComponent('seller-1');
      fixture = TestBed.createComponent(SellerShopComponent);
      component = fixture.componentInstance;
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should load seller on init', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(userServiceSpy.getUserById).toHaveBeenCalledWith('seller-1');
      expect(component.seller).toEqual(mockSeller as any);
    }));

    it('should handle seller load error', fakeAsync(() => {
      userServiceSpy.getUserById.and.returnValue(throwError(() => new Error('Load failed')));
      fixture.detectChanges();
      tick();

      expect(component.seller).toBeUndefined();
      expect(component.isLoadingSeller).toBeFalse();
    }));
  });

  describe('without seller id', () => {
    beforeEach(async () => {
      await createComponent(null);
      fixture = TestBed.createComponent(SellerShopComponent);
      component = fixture.componentInstance;
    });

    it('should show error message when no seller id', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.errorMessage).toBe('No Seller specified.');
      expect(userServiceSpy.getUserById).not.toHaveBeenCalled();
    }));
  });
});
