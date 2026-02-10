import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { InfiniteSliderComponent } from './infinite-slider.component';

describe('InfiniteSliderComponent', () => {
  let component: InfiniteSliderComponent;
  let fixture: ComponentFixture<InfiniteSliderComponent>;
  let router: Router;

  const mockSellers = [
    { id: 'seller-1', avatar: 'avatar1.jpg', name: 'Seller 1' },
    { id: 'seller-2', avatar: 'avatar2.jpg', name: 'Seller 2' },
  ];

  const mockProducts = [
    { id: 'prod-1', image: 'product1.jpg' },
    { id: 'prod-2', image: 'product2.jpg' },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InfiniteSliderComponent, RouterTestingModule],
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(InfiniteSliderComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('inputs', () => {
    it('should accept sellers input', () => {
      component.sellers = mockSellers;
      fixture.detectChanges();
      expect(component.sellers.length).toBe(2);
    });

    it('should accept products input', () => {
      component.products = mockProducts;
      fixture.detectChanges();
      expect(component.products.length).toBe(2);
    });

    it('should have empty sellers by default', () => {
      expect(component.sellers).toEqual([]);
    });

    it('should have empty products by default', () => {
      expect(component.products).toEqual([]);
    });
  });

  describe('extendedSellers', () => {
    it('should return sellers repeated 4 times', () => {
      component.sellers = mockSellers;
      expect(component.extendedSellers.length).toBe(8); // 2 sellers * 4
    });

    it('should return empty array when no sellers', () => {
      component.sellers = [];
      expect(component.extendedSellers.length).toBe(0);
    });

    it('should contain same seller objects repeated', () => {
      component.sellers = mockSellers;
      const extended = component.extendedSellers;
      expect(extended[0]).toEqual(mockSellers[0]);
      expect(extended[2]).toEqual(mockSellers[0]);
      expect(extended[4]).toEqual(mockSellers[0]);
    });
  });

  describe('extendedProducts', () => {
    it('should return products repeated 4 times', () => {
      component.products = mockProducts;
      expect(component.extendedProducts.length).toBe(8); // 2 products * 4
    });

    it('should return empty array when no products', () => {
      component.products = [];
      expect(component.extendedProducts.length).toBe(0);
    });
  });

  describe('viewSellerShop', () => {
    it('should navigate to seller shop with seller id', () => {
      component.viewSellerShop('seller-123');
      expect(router.navigate).toHaveBeenCalledWith(['/seller-shop', 'seller-123']);
    });
  });

  describe('viewProductDetail', () => {
    it('should navigate to product detail with product id', () => {
      component.viewProductDetail('prod-456');
      expect(router.navigate).toHaveBeenCalledWith(['/product', 'prod-456']);
    });
  });

  describe('shopNow', () => {
    it('should navigate to product-listing', () => {
      component.shopNow();
      expect(router.navigate).toHaveBeenCalledWith(['/product-listing']);
    });
  });

  describe('browseCollections', () => {
    it('should navigate to categories', () => {
      component.browseCollections();
      expect(router.navigate).toHaveBeenCalledWith(['/categories']);
    });
  });
});
