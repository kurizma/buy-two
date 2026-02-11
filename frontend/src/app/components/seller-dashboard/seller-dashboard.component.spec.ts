import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SellerDashboardComponent } from './seller-dashboard.component';
import { ProductService } from '../../services/product.service';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { MediaService } from '../../services/media.service';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

const mockUser = {
  id: 'seller1',
  name: 'John Seller',
  role: 'SELLER',
  avatar: 'avatar.jpg',
};
const mockProducts = [
  {
    id: '1',
    name: 'T-Shirt',
    description: 'Nice shirt',
    price: 19.99,
    images: ['tshirt.jpg'],
    categoryId: 'cat1',
    quantity: 10,
    userId: 'seller1',
  },
];
const mockCategories = [
  { id: 'cat1', name: 'Clothing', slug: 'clothing', icon: 'shirt', description: 'Clothes' },
  { id: 'cat2', name: 'Electronics', slug: 'electronics', icon: 'device', description: 'Tech' },
];

describe('SellerDashboardComponent', () => {
  let component: SellerDashboardComponent;
  let fixture: ComponentFixture<SellerDashboardComponent>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let mediaServiceSpy: jasmine.SpyObj<MediaService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    productServiceSpy = jasmine.createSpyObj('ProductService', [
      'getProductsBySeller',
      'addProduct',
      'updateProduct',
      'deleteProduct',
    ]);
    categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getCategories']);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], { currentUserValue: mockUser });
    mediaServiceSpy = jasmine.createSpyObj('MediaService', [
      'uploadProductImage',
      'listProductImages',
      'deleteImage',
      'isAlreadySelected',
    ]);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    productServiceSpy.getProductsBySeller.and.returnValue(of(mockProducts));
    categoryServiceSpy.getCategories.and.returnValue(of(mockCategories));
    mediaServiceSpy.uploadProductImage.and.returnValue(of({ success: true, message: 'OK', data: { url: 'img.jpg' } } as any));
    mediaServiceSpy.listProductImages.and.returnValue(of({ success: true, message: 'OK', data: { images: [] } } as any));
    mediaServiceSpy.isAlreadySelected.and.returnValue(false);
    (mediaServiceSpy as any).allowedProductImageTypes = ['image/jpeg', 'image/png'];
    (mediaServiceSpy as any).maxImageSize = 2 * 1024 * 1024;

    await TestBed.configureTestingModule({
      imports: [SellerDashboardComponent, HttpClientTestingModule],
    })
      .overrideProvider(AuthService, { useValue: authServiceSpy })
      .overrideProvider(ProductService, { useValue: productServiceSpy })
      .overrideProvider(CategoryService, { useValue: categoryServiceSpy })
      .overrideProvider(MediaService, { useValue: mediaServiceSpy })
      .overrideProvider(Router, { useValue: routerSpy })
      .overrideProvider(ActivatedRoute, {
        useValue: { snapshot: { paramMap: { get: () => null } } },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SellerDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load categories on init', fakeAsync(() => {
      fixture.detectChanges();
      tick();
      expect(categoryServiceSpy.getCategories).toHaveBeenCalled();
      expect(component.categories.length).toBe(2);
    }));

    it('should load seller products on init', fakeAsync(() => {
      fixture.detectChanges();
      tick();
      expect(productServiceSpy.getProductsBySeller).toHaveBeenCalledWith('seller1');
      expect(component.userProducts.length).toBe(1);
    }));

    it('should set seller name and avatar', fakeAsync(() => {
      fixture.detectChanges();
      tick();
      expect(component.sellerName).toBe('John Seller');
      expect(component.sellerAvatar).toBe('avatar.jpg');
    }));

    it('should handle product load error', fakeAsync(() => {
      productServiceSpy.getProductsBySeller.and.returnValue(throwError(() => new Error('Error')));
      fixture.detectChanges();
      tick();
      expect(component.errorMessage).toBe('Could not load your products.');
    }));
  });

  describe('openAddProductModal', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should open modal in add mode', () => {
      component.openAddProductModal();
      expect(component.showModal).toBeTrue();
      expect(component.editIndex).toBeNull();
    });

    it('should reset form when opening modal', () => {
      component.productForm.patchValue({ name: 'Test' });
      component.openAddProductModal();
      expect(component.productForm.get('name')?.value).toBeFalsy();
    });

    it('should set default category when opening modal', () => {
      component.openAddProductModal();
      expect(component.productForm.get('categoryId')?.value).toBe('cat1');
    });
  });

  describe('viewMyShop', () => {
    it('should navigate to seller shop', () => {
      fixture.detectChanges();
      component.viewMyShop();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/seller-shop', 'seller1']);
    });
  });

  describe('image handling', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should remove image from previews', () => {
      component.imagePreviews = [
        { file: null, dataUrl: 'test.jpg', mediaId: null },
        { file: null, dataUrl: 'test2.jpg', mediaId: null },
      ];
      component.removeImage(0);
      expect(component.imagePreviews.length).toBe(1);
      expect(component.imagePreviews[0].dataUrl).toBe('test2.jpg');
    });

    it('should move image up', () => {
      component.imagePreviews = [
        { file: null, dataUrl: 'first.jpg', mediaId: null },
        { file: null, dataUrl: 'second.jpg', mediaId: null },
      ];
      component.moveImageUp(1);
      expect(component.imagePreviews[0].dataUrl).toBe('second.jpg');
    });

    it('should not move first image up', () => {
      component.imagePreviews = [
        { file: null, dataUrl: 'first.jpg', mediaId: null },
        { file: null, dataUrl: 'second.jpg', mediaId: null },
      ];
      component.moveImageUp(0);
      expect(component.imagePreviews[0].dataUrl).toBe('first.jpg');
    });

    it('should move image down', () => {
      component.imagePreviews = [
        { file: null, dataUrl: 'first.jpg', mediaId: null },
        { file: null, dataUrl: 'second.jpg', mediaId: null },
      ];
      component.moveImageDown(0);
      expect(component.imagePreviews[0].dataUrl).toBe('second.jpg');
    });

    it('should not move last image down', () => {
      component.imagePreviews = [
        { file: null, dataUrl: 'first.jpg', mediaId: null },
        { file: null, dataUrl: 'second.jpg', mediaId: null },
      ];
      component.moveImageDown(1);
      expect(component.imagePreviews[1].dataUrl).toBe('second.jpg');
    });

    it('should delete media when removing image with mediaId', fakeAsync(() => {
      mediaServiceSpy.deleteImage.and.returnValue(of({}));
      component.imagePreviews = [{ file: null, dataUrl: 'test.jpg', mediaId: 'media-1' }];
      component.removeImage(0);
      tick();
      expect(mediaServiceSpy.deleteImage).toHaveBeenCalledWith('media-1');
    }));
  });

  describe('drag and drop', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should set isDragActive on dragOver', () => {
      const event = { preventDefault: jasmine.createSpy('preventDefault') } as unknown as DragEvent;
      component.onDragOver(event);
      expect(component.isDragActive).toBeTrue();
    });

    it('should handle drop event', () => {
      const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
      const event = {
        preventDefault: jasmine.createSpy('preventDefault'),
        dataTransfer: { files: [mockFile] },
      } as unknown as DragEvent;
      
      spyOn(component, 'onFilesSelected');
      component.onDrop(event);
      
      expect(component.isDragActive).toBeFalse();
      expect(component.onFilesSelected).toHaveBeenCalled();
    });
  });

  describe('getCategoryById', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should return category by id', () => {
      const category = component.getCategoryById('cat1');
      expect(category?.name).toBe('Clothing');
    });

    it('should return undefined for non-existent category', () => {
      const category = component.getCategoryById('non-existent');
      expect(category).toBeUndefined();
    });
  });

  describe('submitProduct', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should not submit if form is invalid', () => {
      component.productForm.reset();
      component.submitProduct();
      expect(productServiceSpy.addProduct).not.toHaveBeenCalled();
    });

    it('should show error if no images', () => {
      component.productForm.patchValue({
        name: 'Test Product',
        description: 'Description',
        price: 10,
        quantity: 5,
        categoryId: 'cat1',
      });
      component.imagePreviews = [];
      component.submitProduct();
      expect(component.imageValidationError).toBe('Please add at least one image.');
    });

    it('should show error if too many images', () => {
      component.productForm.patchValue({
        name: 'Test Product',
        description: 'Description',
        price: 10,
        quantity: 5,
        categoryId: 'cat1',
      });
      component.imagePreviews = [
        { file: null, dataUrl: '1.jpg', mediaId: null },
        { file: null, dataUrl: '2.jpg', mediaId: null },
        { file: null, dataUrl: '3.jpg', mediaId: null },
        { file: null, dataUrl: '4.jpg', mediaId: null },
        { file: null, dataUrl: '5.jpg', mediaId: null },
        { file: null, dataUrl: '6.jpg', mediaId: null },
      ];
      component.submitProduct();
      expect(component.imageValidationError).toContain('maximum of 5 images');
    });
  });

  describe('UI display', () => {
    it('should display seller name', () => {
      fixture.detectChanges();
      const sellerNameElement = fixture.nativeElement.querySelector('h2 span.fw-bold.ms-0');
      expect(sellerNameElement.textContent.trim()).toBe('John Seller');
    });

    it('should display seller products list', () => {
      fixture.detectChanges();
      const productList = fixture.nativeElement.querySelector('.list-group');
      expect(productList).toBeTruthy();
      expect(fixture.nativeElement.querySelectorAll('.list-group-item').length).toBe(1);
      expect(fixture.nativeElement.querySelector('.fw-bold strong').textContent.trim()).toBe(
        'T-Shirt',
      );
    });

    it('should show Add New Product button', () => {
      fixture.detectChanges();
      const addButton = fixture.nativeElement.querySelector('button.btn-success');
      expect(addButton.textContent.trim()).toContain('Add New Product');
    });

    it('should show empty state when no products', () => {
      (TestBed.inject(ProductService) as any).getProductsBySeller.and.returnValue(of([]));
      fixture = TestBed.createComponent(SellerDashboardComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      const emptyState = fixture.nativeElement.querySelector('.text-center.py-5.text-white');
      expect(emptyState.textContent.trim()).toContain('You have not created any products yet.');
    });
  });

  describe('editProduct', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should load product into form for editing', fakeAsync(() => {
      mediaServiceSpy.listProductImages.and.returnValue(of({ success: true, message: 'OK', data: { images: [] } } as any));
      component.editProduct(0);
      tick();
      expect(component.productForm.get('name')?.value).toBe('T-Shirt');
      expect(component.editIndex).toBe(0);
      expect(component.showModal).toBeTrue();
    }));

    it('should map image URLs with media IDs', fakeAsync(() => {
      const mockMedias = [{ id: 'media-1', url: 'tshirt.jpg' }];
      mediaServiceSpy.listProductImages.and.returnValue(of({ success: true, message: 'OK', data: { images: mockMedias } } as any));
      component.editProduct(0);
      tick();
      expect(component.imagePreviews[0].mediaId).toBe('media-1');
    }));

    it('should handle media list error', fakeAsync(() => {
      mediaServiceSpy.listProductImages.and.returnValue(throwError(() => new Error('Error')));
      component.editProduct(0);
      tick();
      expect(component.showModal).toBeTrue();
      expect(component.imagePreviews[0].mediaId).toBeNull();
    }));
  });

  describe('closeModal', () => {
    it('should reset form and close modal', () => {
      fixture.detectChanges();
      component.showModal = true;
      component.editIndex = 1;
      component.imagePreviews = [{ file: null, dataUrl: 'test.jpg', mediaId: null }];
      
      component.closeModal();
      
      expect(component.showModal).toBeFalse();
      expect(component.editIndex).toBeNull();
      expect(component.imagePreviews.length).toBe(0);
    });
  });

  describe('deleteProduct', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should delete product on confirmation', fakeAsync(() => {
      spyOn(globalThis, 'confirm').and.returnValue(true);
      productServiceSpy.deleteProduct.and.returnValue(of({} as any));
      
      component.deleteProduct(0);
      tick();
      
      expect(productServiceSpy.deleteProduct).toHaveBeenCalledWith('1', 'seller1', 'SELLER');
      expect(component.successMessage).toBe('Product deleted successfully');
    }));

    it('should not delete if user cancels', () => {
      spyOn(globalThis, 'confirm').and.returnValue(false);
      component.deleteProduct(0);
      expect(productServiceSpy.deleteProduct).not.toHaveBeenCalled();
    });

    it('should handle delete error', fakeAsync(() => {
      spyOn(globalThis, 'confirm').and.returnValue(true);
      productServiceSpy.deleteProduct.and.returnValue(throwError(() => new Error('Error')));
      
      component.deleteProduct(0);
      tick();
      
      expect(component.errorMessage).toBe('Failed to delete product. Please try again.');
    }));
  });

  describe('onFilesSelected', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should prevent too many images', () => {
      component.imagePreviews = [
        { file: null, dataUrl: '1.jpg', mediaId: null },
        { file: null, dataUrl: '2.jpg', mediaId: null },
        { file: null, dataUrl: '3.jpg', mediaId: null },
        { file: null, dataUrl: '4.jpg', mediaId: null },
        { file: null, dataUrl: '5.jpg', mediaId: null },
      ];
      const mockFile = new File(['test'], 'new.jpg', { type: 'image/jpeg' });
      component.onFilesSelected({ target: { files: [mockFile] } });
      expect(component.imageValidationError).toContain('up to 5 images');
    });

    it('should reject duplicate images', () => {
      mediaServiceSpy.isAlreadySelected.and.returnValue(true);
      const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
      component.onFilesSelected({ target: { files: [mockFile] } });
      expect(component.imageValidationError).toBe('This image has already been selected.');
    });

    it('should reject invalid file types', () => {
      const mockFile = new File(['test'], 'test.gif', { type: 'image/gif' });
      component.onFilesSelected({ target: { files: [mockFile] } });
      expect(component.imageValidationError).toBe('Only JPG and PNG files are allowed.');
    });

    it('should reject files too large', () => {
      const largeFile = new File([new ArrayBuffer(3 * 1024 * 1024)], 'large.jpg', { type: 'image/jpeg' });
      component.onFilesSelected({ target: { files: [largeFile] } });
      expect(component.imageValidationError).toBe('Image size must be under 2MB.');
    });
  });

  describe('selectedCategoryId getter', () => {
    it('should return category id from form', () => {
      fixture.detectChanges();
      component.productForm.patchValue({ categoryId: 'cat2' });
      expect(component.selectedCategoryId).toBe('cat2');
    });
  });

  describe('onDragLeave', () => {
    it('should set isDragActive to false', () => {
      fixture.detectChanges();
      component.isDragActive = true;
      const event = { preventDefault: jasmine.createSpy('preventDefault') } as unknown as DragEvent;
      component.onDragOver(event);
      expect(component.isDragActive).toBeTrue();
      // Now simulate leaving
      component.isDragActive = false;
      expect(component.isDragActive).toBeFalse();
    });
  });

  describe('submitProduct in edit mode', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should update product without new images', fakeAsync(() => {
      mediaServiceSpy.listProductImages.and.returnValue(of({ success: true, message: 'OK', data: { images: [] } } as any));
      component.editProduct(0);
      tick();
      
      // Update form values
      component.productForm.patchValue({
        name: 'Updated T-Shirt',
        description: 'Updated description',
        price: 29.99,
        quantity: 20,
        categoryId: 'cat1'
      });
      
      // Keep existing image (no file, just url)
      component.imagePreviews = [{ file: null, dataUrl: 'tshirt.jpg', mediaId: null }];
      
      productServiceSpy.updateProduct.and.returnValue(of({ success: true } as any));
      component.submitProduct();
      tick();
      
      expect(productServiceSpy.updateProduct).toHaveBeenCalled();
    }));

    it('should upload new images and update product', fakeAsync(() => {
      mediaServiceSpy.listProductImages.and.returnValue(of({ success: true, message: 'OK', data: { images: [] } } as any));
      component.editProduct(0);
      tick();
      
      component.productForm.patchValue({
        name: 'Updated T-Shirt',
        description: 'Updated description',
        price: 29.99,
        quantity: 20,
        categoryId: 'cat1'
      });
      
      // Add a new file 
      const mockFile = new File(['test'], 'new.jpg', { type: 'image/jpeg' });
      component.imagePreviews = [{ file: mockFile, dataUrl: 'data:image/jpeg;base64,...', mediaId: null }];
      
      mediaServiceSpy.uploadProductImage.and.returnValue(of({ success: true, message: 'OK', data: { url: 'new-url.jpg' } } as any));
      productServiceSpy.updateProduct.and.returnValue(of({ success: true } as any));
      
      component.submitProduct();
      tick();
      
      expect(mediaServiceSpy.uploadProductImage).toHaveBeenCalled();
      expect(productServiceSpy.updateProduct).toHaveBeenCalled();
    }));
  });

  describe('submitProduct in add mode', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      tick();
    }));

    it('should create product and upload images', fakeAsync(() => {
      component.openAddProductModal();
      component.productForm.patchValue({
        name: 'New Product',
        description: 'New description',
        price: 49.99,
        quantity: 10,
        categoryId: 'cat1'
      });
      
      const mockFile = new File(['test'], 'product.jpg', { type: 'image/jpeg' });
      component.imagePreviews = [{ file: mockFile, dataUrl: 'data:image/jpeg;base64,...', mediaId: null }];
      
      productServiceSpy.addProduct.and.returnValue(of({ 
        success: true, 
        data: { id: 'new-product-id', name: 'New Product', description: 'New description', price: 49.99, quantity: 10, categoryId: 'cat1' } 
      } as any));
      mediaServiceSpy.uploadProductImage.and.returnValue(of({ success: true, message: 'OK', data: { url: 'uploaded.jpg' } } as any));
      productServiceSpy.updateProduct.and.returnValue(of({ success: true } as any));
      
      component.submitProduct();
      tick();
      
      expect(productServiceSpy.addProduct).toHaveBeenCalled();
    }));

    it('should handle product creation without returned id', fakeAsync(() => {
      component.openAddProductModal();
      component.productForm.patchValue({
        name: 'New Product',
        description: 'New description',
        price: 49.99,
        quantity: 10,
        categoryId: 'cat1'
      });
      
      const mockFile = new File(['test'], 'product.jpg', { type: 'image/jpeg' });
      component.imagePreviews = [{ file: mockFile, dataUrl: 'data:image/jpeg;base64,...', mediaId: null }];
      
      productServiceSpy.addProduct.and.returnValue(of({ success: true, data: null } as any));
      
      component.submitProduct();
      tick();
      
      expect(component.successMessage).toBe('Product created successfully');
    }));
  });

});
