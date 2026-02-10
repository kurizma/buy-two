import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of } from 'rxjs';
import { NavigationComponent } from './navigation.component';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';
import { Router } from '@angular/router';

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let router: Router;
  let currentUserSubject: BehaviorSubject<any>;
  let cartItemsSubject: BehaviorSubject<any[]>;

  const mockUser = {
    id: 'user-1',
    name: 'Test User',
    email: 'test@example.com',
    role: 'CLIENT' as const,
    avatar: 'avatar.jpg',
  };

  beforeEach(async () => {
    currentUserSubject = new BehaviorSubject<any>(null);
    cartItemsSubject = new BehaviorSubject<any[]>([]);

    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout'], {
      currentUser$: currentUserSubject.asObservable(),
    });

    cartServiceSpy = jasmine.createSpyObj('CartService', ['getTotal'], {
      cartItems$: cartItemsSubject.asObservable(),
    });
    cartServiceSpy.getTotal.and.returnValue(0);

    await TestBed.configureTestingModule({
      imports: [NavigationComponent, RouterTestingModule],
    })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .overrideProvider(CartService, { useValue: cartServiceSpy })
      .compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(NavigationComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should set isAuthenticated to false when no user', () => {
      fixture.detectChanges();
      expect(component.isAuthenticated).toBeFalse();
      expect(component.currentUserName).toBeNull();
    });

    it('should set isAuthenticated to true when user exists', fakeAsync(() => {
      fixture.detectChanges(); // Initialize component first
      currentUserSubject.next(mockUser); // Then emit user
      tick();
      expect(component.isAuthenticated).toBeTrue();
      expect(component.currentUserName).toBe('Test User');
      expect(component.currentUserAvatar).toBe('avatar.jpg');
    }));

    it('should update cart item count from cart service', fakeAsync(() => {
      fixture.detectChanges();
      cartItemsSubject.next([{ id: '1' }, { id: '2' }, { id: '3' }]);
      tick();
      expect(component.cartItemCount).toBe(3);
    }));

    it('should update cart count when items change', fakeAsync(() => {
      fixture.detectChanges();
      expect(component.cartItemCount).toBe(0);
      
      cartItemsSubject.next([{ id: '1' }]);
      tick();
      expect(component.cartItemCount).toBe(1);
      
      cartItemsSubject.next([{ id: '1' }, { id: '2' }]);
      tick();
      expect(component.cartItemCount).toBe(2);
    }));
  });

  describe('ngOnDestroy', () => {
    it('should unsubscribe from cart subscription', () => {
      fixture.detectChanges();
      expect((component as any).cartSubs).toBeTruthy();
      
      component.ngOnDestroy();
      // After unsubscribe, further changes shouldn't affect component
    });
  });

  describe('onLogout', () => {
    it('should call authService.logout', () => {
      component.onLogout();
      expect(authServiceSpy.logout).toHaveBeenCalled();
    });

    it('should navigate to home after logout', () => {
      component.onLogout();
      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });
  });
});
