import { Component, OnInit, inject } from '@angular/core';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../models/users/user-response.model';
import { UserUpdateRequest } from '../../models/users/userUpdateRequest.model';
import { CommonModule } from '@angular/common';
import { MediaService } from '../../services/media.service';
import { AuthService } from '../../services/auth.service';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
} from '@angular/forms';
import { ProfileAnalyticsComponent } from '../profile-analytics/profile-analytics.component';
import { AnalyticsItem } from '../../models/profile/analytics-item';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, ProfileAnalyticsComponent],
})
export class ProfileComponent implements OnInit {
  currentUser: UserResponse | null = null;
  get userRole(): 'client' | 'seller' | null {
    if (!this.currentUser?.role) return null;
    return this.currentUser.role === 'CLIENT' ? 'client' : 'seller';
  }

  profileForm: FormGroup;
  passwordForm: FormGroup;
  avatar: string | null = null; // always URL or null
  avatarPreview: string | null = null; // for <img> only
  loaded = false; // flag to delay rendering the avatar until the user is loaded

  avatarError: string = '';
  avatarMediaId: string | null = null;
  successMessage = '';
  showSuccess = false;

  userService = inject(UserService);
  mediaService = inject(MediaService);
  authService = inject(AuthService);

  fb = inject(FormBuilder);

  // ************* Mock analytics data *****************
  getAnalyticsItems(role: 'client' | 'seller'): AnalyticsItem[] {
    if (!this.currentUser?.email) return [];

    const key = `${role}-${this.currentUser.email}`;

    switch (key) {
      case 'client-dada@dee.com':
        return [
          { name: 'Code Wizard Tee', categories: 'CAT-001', count: 2, amount: 58 },
          { name: 'Keep Coding Tee', categories: 'CAT-001', count: 1, amount: 28 },
          { name: 'Action Noir Tee', categories: 'CAT-006', count: 1, amount: 45 },
        ];

      case 'client-john@doe.com':
      case 'seller-angu@readme.md':
        return [];

      case 'seller-joon@kim.kr':
        return [
          { name: 'Code Wizard Tee', categories: 'CAT-001', count: 4, amount: 110 },
          { name: 'Pop Code Queen Tee', categories: 'CAT-003', count: 2, amount: 150 },
          { name: 'Classic Portrait Tee', categories: 'CAT-006', count: 10, amount: 450 },
        ];

      default:
        return [];
    }
  }

  getTotalAmount(role: 'client' | 'seller'): number {
    const items = this.getAnalyticsItems(role);
    return items.reduce((sum, item) => sum + item.amount, 0);
  }

  constructor() {
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
    });
    this.passwordForm = this.fb.group(
      {
        currentPassword: ['', Validators.required],
        newPassword: [
          '',
          [
            Validators.required,
            Validators.minLength(8),
            Validators.pattern(String.raw`^(?=.*[a-z])(?=.*\d).{8,}$`),
          ],
        ],
        confirmPassword: ['', Validators.required],
      },
      { validator: this.passwordsMatch },
    );
  }

  ngOnInit() {
    this.userService.getCurrentUser().subscribe((user) => {
      if (user) {
        this.currentUser = user;
        this.profileForm.patchValue({ name: user.name, email: user.email });
        this.profileForm.get('email')?.disable(); // Disable email field

        this.avatar = user.avatar || null;
        this.avatarPreview = this.avatar;
        this.avatarMediaId = this.extractMediaId(user.avatar);
      }
      this.loaded = true; // render avatar only after this
    });
  }

  onAvatarSelect(event: any): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.avatarError = '';

    if (file.size > this.mediaService.maxImageSize) {
      this.avatarPreview = this.avatar; // revert preview to current avatar
      this.avatarError = 'Avatar file size must be less than 2MB';
      return;
    }

    if (!this.mediaService.allowedAvatarTypes.includes(file.type)) {
      this.avatarPreview = this.avatar; // revert preview to current avatar
      this.avatarError = 'Invalid avatar file type';
      return;
    }

    // Local preview (base64) so user sees something instantly
    const reader = new FileReader();
    reader.onloadend = () => {
      this.avatarPreview = reader.result as string;
    };
    reader.readAsDataURL(file);

    // Upload via MediaService (stores in media-service + Cloudflare)
    this.mediaService.uploadAvatar(file).subscribe({
      next: (res) => {
        // res is ApiResponse<MediaResponse>
        const media = res.data; // MediaResponse
        this.avatar = media.url; // Use backend URL as final avatar
        this.avatarMediaId = media.id;
        this.avatarPreview = this.avatar; // ensure preview uses final URL
        // this.uploadProgress = 0;
      },
      error: (err) => {
        if (err instanceof Error) {
          // Error from media.service (type/size/user checks)
          this.avatarError = err.message;
        } else {
          this.avatarError = 'Failed to upload avatar.';
        }
      },
    });
  }

  handleRemoveAvatar() {
    if (!this.avatarMediaId) {
      this.avatar = null;
      this.avatarPreview = null;
      this.avatarMediaId = null;
      return;
    }

    this.mediaService.deleteImage(this.avatarMediaId).subscribe({
      next: () => {
        this.avatar = null;
        this.avatarPreview = null;
        this.avatarMediaId = null;
        console.log('✅ Avatar deleted from R2 + MongoDB');
      },
      error: (err) => {
        console.error('Delete failed', err);
        this.avatar = null; // Still clear UI
        this.avatarPreview = null;
        this.avatarMediaId = null;
      },
    });
  }

  private extractMediaId(url?: string): string | null {
    if (!url) return null;
    const match = /media\/([a-f0-9-]+)\./.exec(url);
    return match?.[1] || null;
  }

  saveProfile() {
    // Block saving if avatar upload failed
    if (this.avatarError) {
      this.showSuccess = false;
      this.successMessage = '';
      console.warn('Cannot save profile because avatar upload failed.');
      return;
    }

    if (this.profileForm.valid && this.currentUser) {
      this.successMessage = '';

      const dto: UserUpdateRequest = {
        id: this.currentUser.id,
        name: this.profileForm.value.name,
        avatar: this.avatar,
      };
      this.userService.updateCurrentUser(dto).subscribe({
        next: (updatedUser) => {
          this.currentUser = updatedUser;
          this.profileForm.patchValue({
            name: updatedUser.name,
          });
          // keep AuthService / navbar in sync
          this.authService.updateCurrentUserInStorage(updatedUser); // navbar updates
          this.successMessage = 'Profile updated successfully!';
          this.showSuccess = true;
          console.log('Profile updated successfully');
        },
        error: (err) => {
          console.error('Failed to update profile', err);
        },
      });
    }
  }

  changePassword() {
    if (this.passwordForm.valid && this.currentUser) {
      this.successMessage = '';

      const dto: UserUpdateRequest = {
        id: this.currentUser.id,
        name: this.currentUser.name, // keep unchanged
        avatar: this.avatar,
        password: this.passwordForm.value.newPassword,
      };
      this.userService.updateCurrentUser(dto).subscribe({
        next: (updatedUser) => {
          this.currentUser = updatedUser;
          this.passwordForm.reset();
          this.successMessage = 'Password changed successfully!';
          this.showSuccess = true;
          console.log('Password updated successfully');
        },
        error: (err) => {
          console.error('Failed to update password', err);
        },
      });
    }
  }

  private passwordsMatch(form: AbstractControl) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return newPassword === confirmPassword ? null : { mismatch: true };
  }
}
