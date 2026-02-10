import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MediaService } from './media.service';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('MediaService', () => {
  let service: MediaService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockUser = {
    id: 'user-1',
    email: 'test@example.com',
    name: 'Test User',
    role: 'SELLER',
    avatar: 'avatar.png',
    createdAt: new Date().toISOString(),
  };

  const mockMediaResponse = {
    success: true,
    message: 'Uploaded',
    data: {
      id: 'media-1',
      url: 'https://cdn.example.com/image.jpg',
      ownerId: 'user-1',
      ownerType: 'USER',
    },
  };

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUserValue: mockUser,
    });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        MediaService,
        { provide: AuthService, useValue: authServiceSpy },
      ],
    });

    service = TestBed.inject(MediaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('isAlreadySelected', () => {
    it('should return true if file already in list', () => {
      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
      const previewList = [{ file, dataUrl: 'data:image/jpeg;base64,...' }];

      expect(service.isAlreadySelected(file, previewList)).toBeTrue();
    });

    it('should return false if file not in list', () => {
      const file1 = new File(['test1'], 'test1.jpg', { type: 'image/jpeg' });
      const file2 = new File(['test2'], 'test2.jpg', { type: 'image/jpeg' });
      const previewList = [{ file: file1, dataUrl: 'data:image/jpeg;base64,...' }];

      expect(service.isAlreadySelected(file2, previewList)).toBeFalse();
    });

    it('should handle null files in list', () => {
      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
      const previewList = [{ file: null, dataUrl: 'https://example.com/image.jpg' }];

      expect(service.isAlreadySelected(file, previewList)).toBeFalse();
    });
  });

  describe('uploadAvatar', () => {
    it('should upload avatar successfully', () => {
      const file = new File(['avatar'], 'avatar.jpg', { type: 'image/jpeg' });

      service.uploadAvatar(file).subscribe((response) => {
        expect(response.success).toBeTrue();
        expect(response.data?.url).toBe('https://cdn.example.com/image.jpg');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/media/images`);
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('X-USER-ID')).toBe('user-1');
      expect(req.request.headers.get('X-USER-ROLE')).toBe('SELLER');
      req.flush(mockMediaResponse);
    });

    it('should reject invalid file type', () => {
      const file = new File(['test'], 'test.pdf', { type: 'application/pdf' });

      service.uploadAvatar(file).subscribe({
        error: (err) => {
          expect(err.message).toBe('Invalid avatar file type');
        },
      });

      httpMock.expectNone(`${environment.apiBaseUrl}/media/images`);
    });

    it('should reject file too large', () => {
      // Create a file larger than 2MB
      const largeContent = new Array(3 * 1024 * 1024).fill('x').join('');
      const file = new File([largeContent], 'large.jpg', { type: 'image/jpeg' });

      service.uploadAvatar(file).subscribe({
        error: (err) => {
          expect(err.message).toBe('Avatar file size must be less than 2MB');
        },
      });

      httpMock.expectNone(`${environment.apiBaseUrl}/media/images`);
    });

    it('should reject when user not logged in', () => {
      // Override auth service to return null user
      (Object.getOwnPropertyDescriptor(authServiceSpy, 'currentUserValue')?.get as jasmine.Spy).and.returnValue(null);

      const file = new File(['avatar'], 'avatar.jpg', { type: 'image/jpeg' });

      service.uploadAvatar(file).subscribe({
        error: (err) => {
          expect(err.message).toBe('User not logged in');
        },
      });

      httpMock.expectNone(`${environment.apiBaseUrl}/media/images`);
    });
  });

  describe('constants', () => {
    it('should have correct max image size', () => {
      expect(service.maxImageSize).toBe(2 * 1024 * 1024);
    });

    it('should have correct allowed product image types', () => {
      expect(service.allowedProductImageTypes).toContain('image/jpeg');
      expect(service.allowedProductImageTypes).toContain('image/png');
    });

    it('should have correct allowed avatar types', () => {
      expect(service.allowedAvatarTypes).toContain('image/jpeg');
      expect(service.allowedAvatarTypes).toContain('image/png');
      expect(service.allowedAvatarTypes).toContain('image/webp');
      expect(service.allowedAvatarTypes).toContain('image/gif');
    });
  });

  describe('uploadProductImage', () => {
    it('should upload product image successfully', () => {
      const file = new File(['product'], 'product.jpg', { type: 'image/jpeg' });

      service.uploadProductImage('prod-1', file).subscribe((response) => {
        expect(response.success).toBeTrue();
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/media/images`);
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('X-USER-ID')).toBe('user-1');
      req.flush(mockMediaResponse);
    });

    it('should reject invalid product image type', () => {
      const file = new File(['test'], 'test.pdf', { type: 'application/pdf' });

      service.uploadProductImage('prod-1', file).subscribe({
        error: (err) => {
          expect(err.message).toBe('Invalid product image file type');
        },
      });

      httpMock.expectNone(`${environment.apiBaseUrl}/media/images`);
    });

    it('should reject large product image', () => {
      const largeContent = new Array(3 * 1024 * 1024).fill('x').join('');
      const file = new File([largeContent], 'large.jpg', { type: 'image/jpeg' });

      service.uploadProductImage('prod-1', file).subscribe({
        error: (err) => {
          expect(err.message).toBe('Product image file size must be less than 2MB');
        },
      });

      httpMock.expectNone(`${environment.apiBaseUrl}/media/images`);
    });

    it('should reject when user not logged in', () => {
      (Object.getOwnPropertyDescriptor(authServiceSpy, 'currentUserValue')?.get as jasmine.Spy).and.returnValue(null);
      const file = new File(['image'], 'image.jpg', { type: 'image/jpeg' });

      service.uploadProductImage('prod-1', file).subscribe({
        error: (err) => {
          expect(err.message).toBe('User not logged in');
        },
      });

      httpMock.expectNone(`${environment.apiBaseUrl}/media/images`);
    });
  });

  describe('listProductImages', () => {
    it('should list product images', () => {
      const mockListResponse = {
        success: true,
        message: 'OK',
        data: { images: [{ id: 'img-1', url: 'https://example.com/img1.jpg' }] },
      };

      service.listProductImages('prod-1').subscribe((response) => {
        expect(response.success).toBeTrue();
        expect(response.data?.images.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/media/images/product/prod-1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockListResponse);
    });
  });

  describe('deleteImage', () => {
    it('should delete image successfully', () => {
      service.deleteImage('media-1').subscribe();

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/media/images/media-1`);
      expect(req.request.method).toBe('DELETE');
      expect(req.request.headers.get('X-USER-ID')).toBe('user-1');
      req.flush({ success: true });
    });

    it('should reject when user not logged in', () => {
      (Object.getOwnPropertyDescriptor(authServiceSpy, 'currentUserValue')?.get as jasmine.Spy).and.returnValue(null);

      service.deleteImage('media-1').subscribe({
        error: (err) => {
          expect(err.message).toBe('User not logged in');
        },
      });

      httpMock.expectNone(`${environment.apiBaseUrl}/media/images/media-1`);
    });
  });
});
