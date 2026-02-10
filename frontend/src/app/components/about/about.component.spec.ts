import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AboutComponent } from './about.component';
import { AuthService } from '../../services/auth.service';

describe('AboutComponent', () => {
  let component: AboutComponent;
  let fixture: ComponentFixture<AboutComponent>;
  let routerSpy: jasmine.SpyObj<Router>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout'], {
      currentUserValue: { id: 'user-1', role: 'CLIENT' },
    });

    await TestBed.configureTestingModule({
      imports: [AboutComponent, HttpClientTestingModule],
    })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .compileComponents();

    fixture = TestBed.createComponent(AboutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('shopNow', () => {
    it('should navigate to product-listing', () => {
      component.shopNow();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/product-listing']);
    });
  });

  describe('browseCollections', () => {
    it('should navigate to categories', () => {
      component.browseCollections();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/categories']);
    });
  });

  describe('onSellWithUsClick', () => {
    it('should navigate to seller-dashboard for seller users', () => {
      (Object.getOwnPropertyDescriptor(authServiceSpy, 'currentUserValue')?.get as jasmine.Spy).and.returnValue({
        id: 'user-1',
        role: 'SELLER',
      });

      component.onSellWithUsClick();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/seller-dashboard']);
    });

    it('should logout and redirect to signup for non-seller users', () => {
      component.onSellWithUsClick();
      expect(authServiceSpy.logout).toHaveBeenCalled();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/signup']);
    });
  });

  describe('registerSeller', () => {
    it('should logout and navigate to signup', () => {
      component.registerSeller();
      expect(authServiceSpy.logout).toHaveBeenCalled();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/signup']);
    });
  });
});
