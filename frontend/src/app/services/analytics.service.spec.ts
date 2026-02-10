import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AnalyticsService } from './analytics.service';
import { environment } from '../../environments/environment';

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  let httpMock: HttpTestingController;

  const mockClientAnalyticsResponse = {
    success: true,
    message: 'OK',
    data: {
      totalSpent: 500,
      mostBoughtProducts: [
        { name: 'Product A', totalQty: 5, totalAmount: 250, category: 'Electronics', productId: 'p1' },
        { name: 'Product B', totalQty: 3, totalAmount: 150, category: 'Clothing', productId: 'p2' },
      ],
      topCategories: [
        { category: 'Electronics', totalSpent: 300 },
        { category: 'Clothing', totalSpent: 200 },
      ],
    },
  };

  const mockSellerAnalyticsResponse = {
    success: true,
    message: 'OK',
    data: {
      totalRevenue: 1000,
      bestSellingProducts: [
        { name: 'Best Seller', unitsSold: 50, revenue: 500, category: 'Electronics' },
      ],
      topCategories: [
        { category: 'Electronics', totalRevenue: 1000 },
      ],
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AnalyticsService],
    });

    service = TestBed.inject(AnalyticsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getClientAnalytics', () => {
    it('should fetch and transform client analytics', () => {
      service.getClientAnalytics('user-1').subscribe((analytics) => {
        expect(analytics.totalAmount).toBe(500);
        expect(analytics.items.length).toBe(2);
        expect(analytics.items[0].name).toBe('Product A');
        expect(analytics.items[0].count).toBe(5);
        expect(analytics.categories?.length).toBe(2);
        expect(analytics.categories?.[0]).toBe('Electronics');
        expect(analytics.categoryAmounts?.[0]).toBe(300);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/analytics/client/user-1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockClientAnalyticsResponse);
    });

    it('should handle unsuccessful response', () => {
      service.getClientAnalytics('user-1').subscribe({
        error: (err) => {
          expect(err.message).toContain('analytics failed');
        },
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/analytics/client/user-1`);
      req.flush({ success: false, message: 'Client analytics failed' });
    });

    it('should handle HTTP error', () => {
      service.getClientAnalytics('user-1').subscribe({
        error: (err) => {
          expect(err).toBeTruthy();
        },
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/analytics/client/user-1`);
      req.flush({ message: 'Server Error' }, { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getSellerAnalytics', () => {
    it('should fetch and transform seller analytics', () => {
      service.getSellerAnalytics('seller-1').subscribe((analytics) => {
        expect(analytics.totalAmount).toBe(1000);
        expect(analytics.items.length).toBe(1);
        expect(analytics.items[0].name).toBe('Best Seller');
        expect(analytics.items[0].count).toBe(50);
        expect(analytics.items[0].amount).toBe(500);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/analytics/seller/seller-1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockSellerAnalyticsResponse);
    });

    it('should handle unsuccessful response', () => {
      service.getSellerAnalytics('seller-1').subscribe({
        error: (err) => {
          expect(err.message).toContain('analytics failed');
        },
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/analytics/seller/seller-1`);
      req.flush({ success: false, message: 'Seller analytics failed' });
    });

    it('should handle missing bestSellingProducts', () => {
      const response = {
        success: true,
        data: {
          totalRevenue: 0,
          bestSellingProducts: undefined,
          topCategories: [],
        },
      };

      service.getSellerAnalytics('seller-1').subscribe((analytics) => {
        expect(analytics.totalAmount).toBe(0);
        expect(analytics.items).toEqual([]);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/analytics/seller/seller-1`);
      req.flush(response);
    });
  });
});
