import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HomeComponent } from './home.component';
import { CategoryService } from '../../services/category.service';
import { ProductService } from '../../services/product.service';
import { UserService } from '../../services/user.service';
import { Category } from '../../models/categories/category.model';
import { ProductResponse } from '../../models/products/product-response.model';
import { UserResponse } from '../../models/users/user-response.model';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockCategories: Category[] = [
    { id: 'cat-1', slug: 'electronics', name: 'Electronics', icon: 'laptop', description: 'Electronics' },
    { id: 'cat-2', slug: 'clothing', name: 'Clothing', icon: 'shirt', description: 'Clothing' },
  ];

  const mockProducts: ProductResponse[] = [
    { id: 'prod-1', name: 'Product 1', description: 'Desc 1', price: 99, quantity: 10, images: ['img1.jpg'], categoryId: 'cat-1', userId: 'seller-1' },
    { id: 'prod-2', name: 'Product 2', description: 'Desc 2', price: 199, quantity: 5, images: ['img2.jpg'], categoryId: 'cat-2', userId: 'seller-2' },
  ];

  const mockSellers: UserResponse[] = [
    { id: 'seller-1', email: 'seller1@test.com', name: 'Seller 1', role: 'SELLER', avatar: 'avatar1.jpg' },
    { id: 'seller-2', email: 'seller2@test.com', name: 'Seller 2', role: 'SELLER', avatar: 'avatar2.jpg' },
  ];

  beforeEach(async () => {
    categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getCategories']);
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getProducts']);
    userServiceSpy = jasmine.createSpyObj('UserService', ['getSellers']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    categoryServiceSpy.getCategories.and.returnValue(of(mockCategories));
    productServiceSpy.getProducts.and.returnValue(of(mockProducts));
    userServiceSpy.getSellers.and.returnValue(of(mockSellers));

    await TestBed.configureTestingModule({
      imports: [HomeComponent, HttpClientTestingModule],
    })
      .overrideProvider(CategoryService, { useValue: categoryServiceSpy })
      .overrideProvider(ProductService, { useValue: productServiceSpy })
      .overrideProvider(UserService, { useValue: userServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load categories on init', () => {
      fixture.detectChanges();
      expect(categoryServiceSpy.getCategories).toHaveBeenCalled();
      expect(component.categories.length).toBe(2);
    });

    it('should load sellers on init', () => {
      fixture.detectChanges();
      expect(userServiceSpy.getSellers).toHaveBeenCalled();
      expect(component.sliderSellers.length).toBe(2);
      expect(component.sliderSellers[0].name).toBe('Seller 1');
    });

    it('should load products on init', () => {
      fixture.detectChanges();
      expect(productServiceSpy.getProducts).toHaveBeenCalled();
      expect(component.sliderProducts.length).toBe(2);
      expect(component.sliderProducts[0].image).toBe('img1.jpg');
    });
  });

  describe('error handling', () => {
    it('should handle category load error', () => {
      categoryServiceSpy.getCategories.and.returnValue(throwError(() => new Error('Load failed')));
      spyOn(console, 'error');

      fixture.detectChanges();

      expect(console.error).toHaveBeenCalled();
      expect(component.categories).toEqual([]);
    });

    it('should handle sellers load error', () => {
      userServiceSpy.getSellers.and.returnValue(throwError(() => new Error('Load failed')));
      spyOn(console, 'error');

      fixture.detectChanges();

      expect(console.error).toHaveBeenCalled();
    });

    it('should handle products load error', () => {
      productServiceSpy.getProducts.and.returnValue(throwError(() => new Error('Load failed')));
      spyOn(console, 'error');

      fixture.detectChanges();

      expect(console.error).toHaveBeenCalled();
    });
  });

  describe('onCategoryClick', () => {
    it('should navigate to category page', () => {
      component.onCategoryClick('electronics');

      expect(routerSpy.navigate).toHaveBeenCalledWith(['/categories', 'electronics']);
    });
  });

  describe('seller avatar fallback', () => {
    it('should use default avatar when seller has none', () => {
      const sellersWithoutAvatar: UserResponse[] = [
        { id: 'seller-1', email: 'seller@test.com', name: 'Seller', role: 'SELLER' },
      ];
      userServiceSpy.getSellers.and.returnValue(of(sellersWithoutAvatar));

      fixture.detectChanges();

      expect(component.sliderSellers[0].avatar).toBe('assets/avatars/user-default.png');
    });
  });
});
