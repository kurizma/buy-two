export interface CartItem {
  productId: string;
  productName: string;
  sellerId: string;
  price: number;
  quantity: number;
  categoryId: string;
  imageUrl?: string;
}

// ⭐ MOCK DATA - Use anywhere!
export const MOCK_CART_ITEMS: CartItem[] = [
  {
    productId: 'prod-001',
    productName: 'Code Wizard Tee',
    sellerId: 'seller-001',
    price: 37.5, // Excl VAT
    quantity: 2,
    categoryId: 'CAT-001',
    imageUrl: 'assets/images/CodeWizardTee(1).png',
  },
  {
    productId: 'prod-002',
    productName: 'Pop Code Queen Tee',
    sellerId: 'seller-002',
    price: 45,
    quantity: 1,
    categoryId: 'CAT-003',
    imageUrl: 'assets/images/PopCodeQueenTee(1).png',
  },
  {
    productId: 'prod-003',
    productName: 'Action Noir Tee',
    sellerId: 'seller-001',
    price: 28,
    quantity: 3,
    categoryId: 'CAT-006',
    imageUrl: 'assets/images/OlegActionNoirTee(3).png',
  },
];
