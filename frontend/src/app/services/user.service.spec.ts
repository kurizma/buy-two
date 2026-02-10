import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { UserResponse } from '../models/users/user-response.model';
import { UserUpdateRequest } from '../models/users/userUpdateRequest.model';
import { environment } from '../../environments/environment';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: UserResponse = {
    id: 'user-1',
    email: 'test@example.com',
    name: 'Test User',
    role: 'CLIENT',
    avatar: 'avatar.png',
  };

  const mockSellers: UserResponse[] = [
    { id: 'seller-1', email: 'seller1@test.com', name: 'Seller One', role: 'SELLER', avatar: 'avatar1.png' },
    { id: 'seller-2', email: 'seller2@test.com', name: 'Seller Two', role: 'SELLER', avatar: 'avatar2.png' },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService],
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCurrentUser', () => {
    it('should fetch current user', () => {
      service.getCurrentUser().subscribe((user) => {
        expect(user.id).toBe('user-1');
        expect(user.name).toBe('Test User');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/users/me`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });
  });

  describe('updateCurrentUser', () => {
    it('should update current user', () => {
      const updateReq: UserUpdateRequest = {
        id: 'user-1',
        name: 'Updated Name',
        avatar: 'new-avatar.png',
      };

      const updatedUser = { ...mockUser, name: 'Updated Name' };

      service.updateCurrentUser(updateReq).subscribe((user) => {
        expect(user.name).toBe('Updated Name');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/users/me`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateReq);
      req.flush(updatedUser);
    });
  });

  describe('getAllUsers', () => {
    it('should fetch all users', () => {
      const users = [mockUser, { ...mockUser, id: 'user-2' }];

      service.getAllUsers().subscribe((result) => {
        expect(result.length).toBe(2);
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/users`);
      expect(req.request.method).toBe('GET');
      req.flush(users);
    });
  });

  describe('getSellers', () => {
    it('should fetch only sellers', () => {
      service.getSellers().subscribe((sellers) => {
        expect(sellers.length).toBe(2);
        expect(sellers.every((s) => s.role === 'SELLER')).toBeTrue();
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/users/sellers`);
      expect(req.request.method).toBe('GET');
      req.flush(mockSellers);
    });
  });

  describe('getUserById', () => {
    it('should fetch user by id', () => {
      service.getUserById('user-1').subscribe((user) => {
        expect(user.id).toBe('user-1');
        expect(user.email).toBe('test@example.com');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/users/user-1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });
  });
});
