import { Component, OnInit, inject } from '@angular/core';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  FormsModule,
} from '@angular/forms';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CartService } from '../../services/cart.service';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  imports: [
    MatStepper,
    MatStepperModule,
    ReactiveFormsModule, // for FormGroup and FormBuilder
    MatFormFieldModule, // for mat-form-field
    MatInputModule, // for matInput
    MatButtonModule, // for mat-button
    MatIcon, // for mat-icon
    CommonModule, // for ngIf and ngFor
    RouterLink, // for routerLink
    MatRadioModule, // for mat-radio-button
    FormsModule, // for ngModel
  ],
})
export class CheckoutComponent implements OnInit {
  checkoutForm: FormGroup;
  cartItems: any[] = [];
  total = 0;
  formSubmitted = false;

  public cartService: CartService = inject(CartService);
  private readonly router = inject(Router);
  private fb: FormBuilder = inject(FormBuilder);
  reviewForm: FormGroup = this.fb.group({}); // Dummy form for step control
  selectedPayment: string = 'PAY_ON_DELIVERY';

  constructor() {
    this.checkoutForm = this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      zip: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.cartItems = this.cartService.cartItems; // Load from service/localStorage
    this.total = this.cartService.getTotal();
  }

  goToNext(stepper: MatStepper) {
    this.formSubmitted = true;
    // Mark form as touched  to show errors
    this.checkoutForm.markAllAsTouched();

    if (this.checkoutForm.valid) {
      stepper.next();
    }
  }

  placeOrder() {
    // Mock order creation (no service needed)
    const mockOrderId = 'order-' + Date.now();
    const orderData = {
      id: mockOrderId,
      address: this.checkoutForm.value,
      items: this.cartService.cartItems,
      total: this.cartService.getTotal(),
      paymentMethod: this.selectedPayment,
    };

    console.log('✅ Order created:', orderData);

    // Clear cart
    this.cartService.clearCart();

    // Navigate to mock orders page
    this.router.navigate(['/orders', mockOrderId]);
  }
}
