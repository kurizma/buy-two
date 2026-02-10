import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { CategoriesComponent } from './categories.component';
import { CategoryService } from '../../services/category.service';
import { ProductService } from '../../services/product.service';
import { UserService } from '../../services/user.service';
import { Category } from '../../models/categories/category.model';
import { BehaviorSubject } from 'rxjs';

describe('CategoriesComponent', () => {
  let component: CategoriesComponent;
  let fixture: ComponentFixture<CategoriesComponent>;
  let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let productsSubject: BehaviorSubject<any[]>;

  const mockCategories: Category[] = [
    { id: 'cat-1', name: 'Electronics', slug: 'electronics', icon: 'devices', description: 'Electronic devices' },
    { id: 'cat-2', name: 'Clothing', slug: 'clothing', icon: 'checkroom', description: 'Fashion and clothing' },
  ];

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

  beforeEach(async () => {
    productsSubject = new BehaviorSubject(mockProducts);

    categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getCategories']);
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getProducts'], {
      products$: productsSubject.asObservable(),
    });
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUserById']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    categoryServiceSpy.getCategories.and.returnValue(of(mockCategories));
    productServiceSpy.getProducts.and.returnValue(of(mockProducts));

    await TestBed.configureTestingModule({
      imports: [CategoriesComponent, HttpClientTestingModule],
    })
      .overrideProvider(CategoryService, { useValue: categoryServiceSpy })
      .overrideProvider(ProductService, { useValue: productServiceSpy })
      .overrideProvider(UserService, { useValue: userServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: {
            paramMap: convertToParamMap({}),
          },
          paramMap: of(convertToParamMap({})),
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(CategoriesComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load categories on init', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    expect(categoryServiceSpy.getCategories).toHaveBeenCalled();
    expect(component.categories.length).toBe(2);
  }));

  it('should show loading state', () => {
    component.isLoadingCategories = true;
    expect(component.isLoading).toBeTrue();

    component.isLoadingCategories = false;
    component.isLoadingProducts = true;
    expect(component.isLoading).toBeTrue();

    component.isLoadingProducts = false;
    expect(component.isLoading).toBeFalse();
  });
});
