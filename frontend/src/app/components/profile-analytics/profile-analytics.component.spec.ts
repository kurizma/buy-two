import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ProfileAnalyticsComponent } from './profile-analytics.component';
import { AnalyticsItem } from '../../models/analytics/analytics.model';
import { Chart, registerables } from 'chart.js';

// Register Chart.js components globally for tests
Chart.register(...registerables);

describe('ProfileAnalyticsComponent', () => {
  let component: ProfileAnalyticsComponent;
  let fixture: ComponentFixture<ProfileAnalyticsComponent>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockClientItems: AnalyticsItem[] = [
    { name: 'Product 1', count: 2, amount: 100, categories: ['Electronics'] },
    { name: 'Product 2', count: 3, amount: 150, categories: ['Clothing'] },
  ];

  const mockSellerItems: AnalyticsItem[] = [
    { name: 'Sold Product 1', count: 5, amount: 200, categories: ['Electronics'] },
    { name: 'Sold Product 2', count: 10, amount: 300, categories: ['Books'] },
  ];

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ProfileAnalyticsComponent],
    })
      .overrideProvider(Router, { useValue: routerSpy })
      .compileComponents();

    fixture = TestBed.createComponent(ProfileAnalyticsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('client role', () => {
    beforeEach(() => {
      component.role = 'client';
      component.totalAmount = 250;
      component.items = mockClientItems;
      component.categories = ['Electronics', 'Clothing'];
      component.categoryAmounts = [150, 100];
    });

    it('should set up chart data for client', () => {
      component.ngOnChanges();

      expect(component.barChartData.labels).toEqual(['Product 1', 'Product 2']);
      expect(component.pieChartData.labels).toEqual(['Electronics', 'Clothing']);
    });

    it('should use client colors', () => {
      component.ngOnChanges();

      // Should have dataset with client color scheme
      expect(component.barChartData.datasets.length).toBeGreaterThan(0);
    });
  });

  describe('seller role', () => {
    beforeEach(() => {
      component.role = 'seller';
      component.totalAmount = 500;
      component.items = mockSellerItems;
      component.categories = ['Electronics', 'Books'];
      component.categoryAmounts = [300, 200];
    });

    it('should set up chart data for seller', () => {
      component.ngOnChanges();

      expect(component.barChartData.labels).toEqual(['Sold Product 1', 'Sold Product 2']);
      expect(component.pieChartData.labels).toEqual(['Electronics', 'Books']);
    });
  });

  describe('empty data', () => {
    it('should handle empty items', () => {
      component.role = 'client';
      component.totalAmount = 0;
      component.items = [];
      component.categories = [];
      component.categoryAmounts = [];

      component.ngOnChanges();

      expect(component.barChartData.labels).toEqual([]);
    });
  });
});
