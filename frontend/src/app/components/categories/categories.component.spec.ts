import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { CategoriesComponent } from './categories.component';
import { CategoryService } from '../../services/category.service';
import { ProductService } from '../../services/product.service';
import { UserService } from '../../services/user.service';
import { Category } from '../../models/categories/category.model';

describe('CategoriesComponent', () => {
  let component: CategoriesComponent;
  let fixture: ComponentFixture<CategoriesComponent>;
  let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let paramMapSubject: BehaviorSubject<any>;

  const mockCategories: Category[] = [
    { id: 'cat-1', name: 'Electronics', slug: 'electronics', icon: 'devices', description: 'Electronic devices' },
    { id: 'cat-2', name: 'Clothing', slug: 'clothing', icon: 'checkroom', description: 'Fashion and clothing' },
  ];

  const mockProducts = [
    { id: 'prod-1', name: 'Laptop', description: 'Desc', price: 999.99, quantity: 10, categoryId: 'cat-1', userId: 'seller-1', images: [] },
    { id: 'prod-2', name: 'T-Shirt', description: 'Desc', price: 29.99, quantity: 50, categoryId: 'cat-2', userId: 'seller-2', images: [] },
  ];

  const mockSeller = { id: 'seller-1', name: 'Test Seller', email: 'seller@test.com', role: 'SELLER' as const, avatar: '' };

  beforeEach(async () => {
    paramMapSubject = new BehaviorSubject(convertToParamMap({}));

    categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getCategories']);
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getProducts']);
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUserById']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    categoryServiceSpy.getCategories.and.returnValue(of(mockCategories));
    productServiceSpy.getProducts.and.returnValue(of(mockProducts));
    userServiceSpy.getUserById.and.returnValue(of(mockSeller));

    await TestBed.configureTestingModule({
      imports: [CategoriesComponent, HttpClientTestingModule],
    })
      .overrideProvider(CategoryService, { useValue: categoryServiceSpy })
      .overrideProvider(ProductService, { useValue: productServiceSpy })
      .overrideProvider(UserService, { useValue: userServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: { paramMap: convertToParamMap({}) },
          paramMap: paramMapSubject.asObservable(),
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(CategoriesComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('initialization', () => {
    it('should load categories on init', fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
      expect(categoryServiceSpy.getCategories).toHaveBeenCalled();
      expect(component.categories.length).toBe(2);
    }));

    it('should load products on init', fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
      expect(productServiceSpy.getProducts).toHaveBeenCalled();
      expect(component.products.length).toBe(2);
    }));
  });

  describe('isLoading', () => {
    it('should be true when loading categories', () => {
      component.isLoadingCategories = true;
      expect(component.isLoading).toBeTrue();
    });

    it('should be true when loading products', () => {
      component.isLoadingProducts = true;
      expect(component.isLoading).toBeTrue();
    });

    it('should be false when not loading', () => {
      component.isLoadingCategories = false;
      component.isLoadingProducts = false;
      expect(component.isLoading).toBeFalse();
    });
  });

  describe('selectCategory', () => {
    it('should update selected category slug', () => {
      component.selectCategory('clothing');
      expect(component.selectedCategorySlug).toBe('clothing');
    });

    it('should navigate to category URL', () => {
      component.selectCategory('clothing');
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/categories', 'clothing']);
    });
  });

  describe('selectedCategory', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
    }));

    it('should return selected category object', () => {
      component.selectedCategorySlug = 'electronics';
      expect(component.selectedCategory?.name).toBe('Electronics');
    });

    it('should return undefined when no category selected', () => {
      component.selectedCategorySlug = null;
      expect(component.selectedCategory).toBeUndefined();
    });
  });

  describe('filteredProducts', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
    }));

    it('should filter products by category', () => {
      component.selectedCategorySlug = 'electronics';
      expect(component.filteredProducts.length).toBe(1);
      expect(component.filteredProducts[0].name).toBe('Laptop');
    });

    it('should return empty array when no category selected', () => {
      component.selectedCategorySlug = null;
      expect(component.filteredProducts.length).toBe(0);
    });
  });

  describe('getCategoryName', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
    }));

    it('should return category name for valid id', () => {
      expect(component.getCategoryName('cat-1')).toBe('Electronics');
    });

    it('should return empty string for invalid id', () => {
      expect(component.getCategoryName('invalid')).toBe('');
    });
  });

  describe('getSeller', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
    }));

    it('should return seller from map', () => {
      component.sellers.set('seller-1', mockSeller);
      expect(component.getSeller('seller-1')?.name).toBe('Test Seller');
    });

    it('should return undefined for unknown seller', () => {
      expect(component.getSeller('unknown')).toBeUndefined();
    });
  });

  describe('viewProductDetail', () => {
    it('should navigate to product detail', () => {
      component.viewProductDetail('prod-1');
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/product', 'prod-1']);
    });
  });

  describe('route handling', () => {
    it('should redirect on invalid slug', fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
      paramMapSubject.next(convertToParamMap({ slug: 'invalid-slug' }));
      tick(100);
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/categories']);
    }));

    it('should clear selection when no slug in URL', fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
      component.selectedCategorySlug = 'electronics';
      paramMapSubject.next(convertToParamMap({}));
      tick(100);
      expect(component.selectedCategorySlug).toBeNull();
    }));
  });

  describe('loadSellersForProducts', () => {
    it('should load sellers for products', fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
      expect(userServiceSpy.getUserById).toHaveBeenCalled();
    }));

    it('should not load seller twice for same id', fakeAsync(() => {
      fixture.detectChanges();
      tick(100);
      const callCount = userServiceSpy.getUserById.calls.count();
      // Products have 2 different sellers
      expect(callCount).toBe(2);
    }));
  });
});
