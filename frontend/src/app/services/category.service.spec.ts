import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CategoryService } from './category.service';
import { Category } from '../models/categories/category.model';
import { environment } from '../../environments/environment';

describe('CategoryService', () => {
  let service: CategoryService;
  let httpMock: HttpTestingController;

  const mockCategories: Category[] = [
    { id: 'cat-1', slug: 'electronics', name: 'Electronics', icon: 'laptop', description: 'Electronic items' },
    { id: 'cat-2', slug: 'clothing', name: 'Clothing', icon: 'shirt', description: 'Clothing items' },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CategoryService],
    });

    service = TestBed.inject(CategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCategories', () => {
    it('should fetch all categories', () => {
      service.getCategories().subscribe((categories) => {
        expect(categories.length).toBe(2);
        expect(categories[0].name).toBe('Electronics');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/categories`);
      expect(req.request.method).toBe('GET');
      req.flush(mockCategories);
    });

    it('should update categories subject', () => {
      let emittedCategories: Category[] = [];
      service.categories$.subscribe((c) => (emittedCategories = c));

      service.getCategories().subscribe();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/categories`);
      req.flush(mockCategories);

      expect(emittedCategories.length).toBe(2);
    });

    it('should populate categoryMap', () => {
      service.getCategories().subscribe();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/categories`);
      req.flush(mockCategories);

      expect(service.categoryMap.get('cat-1')).toBe('electronics');
      expect(service.categoryMap.get('cat-2')).toBe('clothing');
    });
  });

  describe('getCategoryById', () => {
    it('should fetch a single category', () => {
      service.getCategoryById('cat-1').subscribe((category) => {
        expect(category.id).toBe('cat-1');
        expect(category.slug).toBe('electronics');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/categories/cat-1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockCategories[0]);
    });
  });

  describe('getCategorySlug', () => {
    it('should return slug from map', () => {
      service.categoryMap.set('cat-1', 'electronics');
      expect(service.getCategorySlug('cat-1')).toBe('electronics');
    });

    it('should fallback to subject value', () => {
      // Setup categories in subject
      service.getCategories().subscribe();
      const req = httpMock.expectOne(`${environment.apiBaseUrl}/categories`);
      req.flush(mockCategories);

      // Clear map to test fallback
      service.categoryMap.clear();

      expect(service.getCategorySlug('cat-1')).toBe('electronics');
    });

    it('should fallback to id when not found', () => {
      expect(service.getCategorySlug('unknown-id')).toBe('unknown-id');
    });
  });

  describe('loadCategories', () => {
    it('should load categories when map is empty', () => {
      service.loadCategories();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/categories`);
      req.flush(mockCategories);

      expect(service.categoryMap.size).toBe(2);
    });

    it('should not reload when categories already loaded', () => {
      service.categoryMap.set('cat-1', 'electronics');

      service.loadCategories();

      httpMock.expectNone(`${environment.apiBaseUrl}/categories`);
    });
  });
});
