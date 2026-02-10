import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { ProductResponse } from '../models/products/product-response.model';
import { CreateProductRequest } from '../models/products/createProductRequest.model';
import { UpdateProductRequest } from '../models/products/updateProductRequest.model';
import { ApiResponse } from '../models/api-response/api-response.model';
import { environment } from '../../environments/environment';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  const mockProduct: ProductResponse = {
    id: 'prod-1',
    name: 'Test Product',
    description: 'A test product',
    price: 99.99,
    quantity: 10,
    images: ['image1.jpg'],
    categoryId: 'cat-1',
    userId: 'seller-1',
  };

  const mockProducts: ProductResponse[] = [
    mockProduct,
    { ...mockProduct, id: 'prod-2', name: 'Second Product' },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService],
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getProducts', () => {
    it('should fetch all products', () => {
      const apiResponse: ApiResponse<ProductResponse[]> = {
        success: true,
        message: 'OK',
        data: mockProducts,
      };

      service.getProducts().subscribe((products) => {
        expect(products.length).toBe(2);
        expect(products[0].name).toBe('Test Product');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products`);
      expect(req.request.method).toBe('GET');
      req.flush(apiResponse);
    });

    it('should update products subject', () => {
      const apiResponse: ApiResponse<ProductResponse[]> = {
        success: true,
        message: 'OK',
        data: mockProducts,
      };

      let emittedProducts: ProductResponse[] = [];
      service.products$.subscribe((p) => (emittedProducts = p));

      service.getProducts().subscribe();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products`);
      req.flush(apiResponse);

      expect(emittedProducts.length).toBe(2);
    });

    it('should return empty array when no data', () => {
      const apiResponse: ApiResponse<ProductResponse[]> = {
        success: true,
        message: 'OK',
        data: undefined as any,
      };

      service.getProducts().subscribe((products) => {
        expect(products).toEqual([]);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products`);
      req.flush(apiResponse);
    });
  });

  describe('getProductById', () => {
    it('should fetch a single product', () => {
      const apiResponse: ApiResponse<ProductResponse> = {
        success: true,
        message: 'OK',
        data: mockProduct,
      };

      service.getProductById('prod-1').subscribe((product) => {
        expect(product.id).toBe('prod-1');
        expect(product.name).toBe('Test Product');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products/prod-1`);
      expect(req.request.method).toBe('GET');
      req.flush(apiResponse);
    });
  });

  describe('getProductsBySeller', () => {
    it('should fetch products by seller', () => {
      const apiResponse: ApiResponse<ProductResponse[]> = {
        success: true,
        message: 'OK',
        data: mockProducts,
      };

      service.getProductsBySeller('seller-1').subscribe((products) => {
        expect(products.length).toBe(2);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products?sellerId=seller-1`);
      expect(req.request.method).toBe('GET');
      req.flush(apiResponse);
    });
  });

  describe('getProductsByCategory', () => {
    it('should fetch products by category', () => {
      const apiResponse: ApiResponse<ProductResponse[]> = {
        success: true,
        message: 'OK',
        data: mockProducts,
      };

      service.getProductsByCategory('cat-1').subscribe((products) => {
        expect(products.length).toBe(2);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products?categoryId=cat-1`);
      expect(req.request.method).toBe('GET');
      req.flush(apiResponse);
    });
  });

  describe('addProduct', () => {
    it('should create a new product', () => {
      const createReq: CreateProductRequest = {
        name: 'New Product',
        description: 'New description',
        price: 49.99,
        quantity: 5,
        categoryId: 'cat-1',
        images: [],
      };

      const apiResponse: ApiResponse<ProductResponse> = {
        success: true,
        message: 'Created',
        data: { ...mockProduct, id: 'prod-new', name: 'New Product' },
      };

      service.addProduct(createReq, 'seller-1').subscribe((response) => {
        expect(response.data?.name).toBe('New Product');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products`);
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('X-USER-ID')).toBe('seller-1');
      expect(req.request.headers.get('X-USER-ROLE')).toBe('SELLER');
      req.flush(apiResponse);
    });

    it('should add product to subject', () => {
      const apiResponse: ApiResponse<ProductResponse> = {
        success: true,
        message: 'Created',
        data: { ...mockProduct, id: 'prod-new' },
      };

      let emittedProducts: ProductResponse[] = [];
      service.products$.subscribe((p) => (emittedProducts = p));

      service.addProduct({} as CreateProductRequest, 'seller-1').subscribe();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products`);
      req.flush(apiResponse);

      expect(emittedProducts.some((p) => p.id === 'prod-new')).toBeTrue();
    });
  });

  describe('updateProduct', () => {
    it('should update an existing product', () => {
      const updateReq: UpdateProductRequest = {
        name: 'Updated Product',
        description: 'Updated description',
        price: 79.99,
        quantity: 15,
        categoryId: 'cat-1',
        images: [],
      };

      const apiResponse: ApiResponse<ProductResponse> = {
        success: true,
        message: 'Updated',
        data: { ...mockProduct, name: 'Updated Product' },
      };

      service.updateProduct('prod-1', updateReq, 'seller-1').subscribe((response) => {
        expect(response.data?.name).toBe('Updated Product');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/products/prod-1`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.headers.get('X-USER-ID')).toBe('seller-1');
      req.flush(apiResponse);
    });
  });
});
