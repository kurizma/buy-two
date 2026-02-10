import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { UserService } from '../../services/user.service';
import { MediaService } from '../../services/media.service';
import { AuthService } from '../../services/auth.service';
import { AnalyticsService } from '../../services/analytics.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { UserResponse, Role } from '../../models/users/user-response.model';
import { AnalyticsResponse } from '../../models/analytics/analytics-response.model';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Chart, registerables } from 'chart.js';

// Register Chart.js components globally for tests
Chart.register(...registerables);

const mockUser: UserResponse = {
  id: 'user1',
  name: 'John Doe',
  email: 'john@example.com',
  role: 'CLIENT' as Role,
  avatar: 'https://example.com/media/abc123.jpg',
};

const mockSellerUser: UserResponse = {
  id: 'seller1',
  name: 'Seller Joe',
  email: 'seller@example.com',
  role: 'SELLER' as Role,
  avatar: undefined,
};

const mockAnalytics: AnalyticsResponse = {
  items: [{ name: 'Orders', count: 5, categories: [] }],
  totalAmount: 500,
};

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let mediaServiceSpy: jasmine.SpyObj<MediaService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let analyticsServiceSpy: jasmine.SpyObj<AnalyticsService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getCurrentUser', 'updateCurrentUser']);
    mediaServiceSpy = jasmine.createSpyObj('MediaService', ['uploadAvatar', 'deleteImage']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['updateCurrentUserInStorage']);
    analyticsServiceSpy = jasmine.createSpyObj('AnalyticsService', ['getClientAnalytics', 'getSellerAnalytics']);

    userServiceSpy.getCurrentUser.and.returnValue(of(mockUser));
    userServiceSpy.updateCurrentUser.and.returnValue(of(mockUser));
    mediaServiceSpy.uploadAvatar.and.returnValue(of({ success: true, message: 'OK', data: { url: 'new-avatar.jpg', id: 'media1' } } as any));
    mediaServiceSpy.deleteImage.and.returnValue(of({}));
    (mediaServiceSpy as any).maxImageSize = 2 * 1024 * 1024;
    (mediaServiceSpy as any).allowedAvatarTypes = ['image/jpeg', 'image/png'];
    analyticsServiceSpy.getClientAnalytics.and.returnValue(of(mockAnalytics));
    analyticsServiceSpy.getSellerAnalytics.and.returnValue(of(mockAnalytics));

    await TestBed.configureTestingModule({
      imports: [ProfileComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: MediaService, useValue: mediaServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: AnalyticsService, useValue: analyticsServiceSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA], // Ignore child component errors like chart.js
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load current user on init', fakeAsync(() => {
    tick(100);
    expect(userServiceSpy.getCurrentUser).toHaveBeenCalled();
  }));

  it('should load analytics', fakeAsync(() => {
    tick(100);
    expect(analyticsServiceSpy.getClientAnalytics).toHaveBeenCalled();
  }));

  it('should return null userRole when no user', () => {
    component.currentUser = null;
    expect(component.userRole).toBeNull();
  });

  it('should return client userRole for CLIENT', () => {
    component.currentUser = mockUser;
    expect(component.userRole).toBe('client');
  });

  it('should return seller userRole for SELLER', () => {
    component.currentUser = mockSellerUser;
    expect(component.userRole).toBe('seller');
  });

  describe('avatar handling', () => {
    it('should handle no file selected', () => {
      component.onAvatarSelect({ target: { files: null } });
      expect(component.avatarError).toBe('');
    });

    it('should show error for oversized avatar', () => {
      const largeFile = new File(['x'.repeat(100)], 'large.jpg', { type: 'image/jpeg' });
      Object.defineProperty(largeFile, 'size', { value: 3 * 1024 * 1024 });
      
      component.onAvatarSelect({ target: { files: [largeFile] } });
      
      expect(component.avatarError).toBe('Avatar file size must be less than 2MB');
    });

    it('should show error for invalid avatar type', () => {
      const invalidFile = new File(['test'], 'test.gif', { type: 'image/gif' });
      
      component.onAvatarSelect({ target: { files: [invalidFile] } });
      
      expect(component.avatarError).toBe('Invalid avatar file type');
    });

    it('should remove avatar without media id', () => {
      component.avatarMediaId = null;
      component.handleRemoveAvatar();
      
      expect(component.avatar).toBeNull();
      expect(component.avatarPreview).toBeNull();
      expect(mediaServiceSpy.deleteImage).not.toHaveBeenCalled();
    });

    it('should delete avatar with media id', fakeAsync(() => {
      component.avatarMediaId = 'media-123';
      component.handleRemoveAvatar();
      tick(100);
      
      expect(mediaServiceSpy.deleteImage).toHaveBeenCalledWith('media-123');
    }));
  });

  describe('saveProfile', () => {
    it('should show success message when profile saved', fakeAsync(() => {
      component.profileForm.patchValue({ name: 'Jane Doe' });
      component.saveProfile();
      tick(100);
      expect(component.successMessage).toContain('Profile updated successfully');
      expect(component.showSuccess).toBeTrue();
    }));

    it('should not save if avatar error exists', () => {
      component.avatarError = 'Avatar upload failed';
      component.saveProfile();
      expect(userServiceSpy.updateCurrentUser).not.toHaveBeenCalled();
    });

    it('should update auth storage on successful save', fakeAsync(() => {
      component.saveProfile();
      tick(100);
      expect(authServiceSpy.updateCurrentUserInStorage).toHaveBeenCalled();
    }));
  });

  describe('changePassword', () => {
    it('should validate password match', () => {
      component.passwordForm.patchValue({
        currentPassword: 'oldpass123',
        newPassword: 'newpass123',
        confirmPassword: 'differentpass',
      });
      expect(component.passwordForm.errors?.['mismatch']).toBeTrue();
    });

    it('should update password when form valid', fakeAsync(() => {
      component.passwordForm.patchValue({
        currentPassword: 'oldpass123',
        newPassword: 'newpass12',
        confirmPassword: 'newpass12',
      });
      component.changePassword();
      tick(100);
      expect(component.successMessage).toContain('Password changed successfully');
    }));
  });

  describe('transformedAnalyticsItems', () => {
    it('should return analytics items', () => {
      component.analyticsData = mockAnalytics;
      expect(component.transformedAnalyticsItems.length).toBe(1);
    });

    it('should return empty array when no data', () => {
      component.analyticsData = null;
      expect(component.transformedAnalyticsItems).toEqual([]);
    });
  });
});
