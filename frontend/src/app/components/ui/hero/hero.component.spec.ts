import { ComponentFixture, TestBed, fakeAsync, tick, discardPeriodicTasks } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { HeroComponent } from './hero.component';
import { AuthService } from '../../../services/auth.service';

describe('HeroComponent', () => {
  let component: HeroComponent;
  let fixture: ComponentFixture<HeroComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  const mockSellerUser = {
    id: 'seller-1',
    name: 'Test Seller',
    email: 'seller@example.com',
    role: 'SELLER' as const,
  };

  const mockClientUser = {
    id: 'client-1',
    name: 'Test Client',
    email: 'client@example.com',
    role: 'CLIENT' as const,
  };

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout'], {
      currentUserValue: null,
    });

    await TestBed.configureTestingModule({
      imports: [HeroComponent, RouterTestingModule],
    })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(HeroComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    if (component.slideInterval) {
      clearInterval(component.slideInterval);
    }
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('initialization', () => {
    it('should have hero slides defined', () => {
      expect(component.heroSlides.length).toBeGreaterThan(0);
    });

    it('should start on first slide', () => {
      expect(component.currentSlide).toBe(0);
    });

    it('should have 4 hero slides', () => {
      expect(component.heroSlides.length).toBe(4);
    });
  });

  describe('ngOnInit', () => {
    it('should start slide interval', fakeAsync(() => {
      fixture.detectChanges();
      expect(component.slideInterval).toBeTruthy();
      discardPeriodicTasks();
    }));

    it('should auto-advance slides every 5 seconds', fakeAsync(() => {
      fixture.detectChanges();
      expect(component.currentSlide).toBe(0);
      
      tick(5000);
      expect(component.currentSlide).toBe(1);
      
      tick(5000);
      expect(component.currentSlide).toBe(2);
      
      discardPeriodicTasks();
    }));
  });

  describe('ngOnDestroy', () => {
    it('should clear slide interval', fakeAsync(() => {
      fixture.detectChanges();
      const intervalId = component.slideInterval;
      
      component.ngOnDestroy();
      
      // Interval should be cleared
      expect(component.slideInterval).toBeFalsy;
      discardPeriodicTasks();
    }));
  });

  describe('nextSlide', () => {
    it('should advance to next slide', () => {
      component.currentSlide = 0;
      component.nextSlide();
      expect(component.currentSlide).toBe(1);
    });

    it('should wrap around to first slide after last', () => {
      component.currentSlide = 3; // last slide (index 3)
      component.nextSlide();
      expect(component.currentSlide).toBe(0);
    });
  });

  describe('prevSlide', () => {
    it('should go to previous slide', () => {
      component.currentSlide = 2;
      component.prevSlide();
      expect(component.currentSlide).toBe(1);
    });

    it('should wrap around to last slide from first', () => {
      component.currentSlide = 0;
      component.prevSlide();
      expect(component.currentSlide).toBe(3); // last slide
    });
  });

  describe('setSlide', () => {
    it('should set slide to specific index', () => {
      component.setSlide(2);
      expect(component.currentSlide).toBe(2);
    });

    it('should set slide to 0', () => {
      component.currentSlide = 3;
      component.setSlide(0);
      expect(component.currentSlide).toBe(0);
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

  describe('onSellWithUsClick', () => {
    it('should navigate to seller-dashboard when user is seller', () => {
      Object.defineProperty(authServiceSpy, 'currentUserValue', { value: mockSellerUser, writable: true });
      component.onSellWithUsClick();
      expect(router.navigate).toHaveBeenCalledWith(['/seller-dashboard']);
    });

    it('should call registerSeller when user is not seller', () => {
      Object.defineProperty(authServiceSpy, 'currentUserValue', { value: mockClientUser, writable: true });
      component.onSellWithUsClick();
      expect(authServiceSpy.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/signup']);
    });

    it('should call registerSeller when no user', () => {
      Object.defineProperty(authServiceSpy, 'currentUserValue', { value: null, writable: true });
      component.onSellWithUsClick();
      expect(authServiceSpy.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/signup']);
    });
  });

  describe('registerSeller', () => {
    it('should logout and navigate to signup', () => {
      component.registerSeller();
      expect(authServiceSpy.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/signup']);
    });
  });
});
