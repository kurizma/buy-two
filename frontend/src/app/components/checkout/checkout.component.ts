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
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';
import { RouterLink, Router } from '@angular/router';
import { OrderItem } from '../../models/order/order-item.model';
import { Order, OrderStatus, PaymentMethod, Address } from '../../models/order/order.model';

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

  public readonly authService: AuthService = inject(AuthService);
  public readonly cartService: CartService = inject(CartService);
  private readonly router = inject(Router);
  private readonly fb: FormBuilder = inject(FormBuilder);

  reviewForm: FormGroup;
  selectedPayment: PaymentMethod = PaymentMethod.PAY_ON_DELIVERY;
  reviewConfirmed = false;

  constructor() {
    this.checkoutForm = this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      street: ['', Validators.required],
      city: ['', Validators.required],
      zip: ['', Validators.required],
      country: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
    });
    this.reviewForm = this.fb.group({
      confirmed: [false, Validators.requiredTrue],
    });
  }

  ngOnInit() {
    this.cartItems = this.cartService.cartItems; // Load from service/localStorage
    this.total = this.cartService.getTotal();
  }

  confirmReview(stepper: MatStepper): void {
    this.reviewForm.patchValue({ confirmed: true });
    stepper.next();
  }

  placeOrder() {
    const mockOrderId = `ORD-${Date.now()}`;

    const orderData: Order = {
      id: mockOrderId,
      userId: this.authService.getUserId() || 'user123',
      orderNumber: mockOrderId,
      items: this.cartService.cartItems as OrderItem[],
      status: OrderStatus.CONFIRMED,
      paymentMethod: PaymentMethod.PAY_ON_DELIVERY,
      shippingAddress: this.makeAddress(), // Helper
      subtotal: this.cartService.getSubtotal(),
      tax: this.cartService.getVatAmount(),
      total: this.cartService.getTotalInclVat(),
      createdAt: new Date().toISOString(),
    };

    localStorage.setItem(`order_${orderData.id}`, JSON.stringify(orderData));
    this.cartService.clearCartAfterOrder();
    this.router.navigate(['/order-detail', orderData.id]);
  }

  private makeAddress() {
    const form = this.checkoutForm.value;
    return {
      firstname: form.firstname,
      lastname: form.lastname,
      street: form.street,
      city: form.city,
      zip: form.zip,
      country: form.country,
      phone: form.phone,
    } as Address;
  }
}
