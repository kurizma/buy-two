export interface CartItem {
  id: string;
  productId: string;
  productName: string;
  productDescription?: string;
  price: number;
  quantity: number;
  imageUrl: string;
  sellerId: string;
  sellerName: string;
  categoryId: string;
}

export const mockCartItems: CartItem[] = [
  {
    id: 'cart-item-1',
    productId: '6942c0fe286f80150d8801e0',
    productName: 'Code Wizard Tee',
    productDescription: 'For devs who love stylish, clever code jokes.',
    price: 29,
    quantity: 2,
    imageUrl:
      'https://pub-0695601915114c88a20b5aa2ba5091cd.r2.dev/media/6942c1004244c6023e6f81bf.png', // Use actual image path from your images array
    sellerId: '691f5fca370d294f8db7a3d6',
    sellerName: 'Joon Kim', // You'll need to fetch seller name from user service
    categoryId: 'CAT-001', // CAT-001
  },
  {
    id: 'cart-item-2',
    productId: '6974f3f55053e779fb320b95',
    productName: 'Why Dark Mode Tee',
    productDescription:
      'Programmers choose dark mode because light attracts bugs! Wear your dev humor.',
    price: 35,
    quantity: 1,
    imageUrl:
      'https://pub-0695601915114c88a20b5aa2ba5091cd.r2.dev/media/6974f3f680f76174d9c4c005.png',
    sellerId: '691f5fca370d294f8db7a3d6',
    sellerName: 'Joon Kim',
    categoryId: 'CAT-005', // CAT-005
  },
  {
    id: 'cart-item-3',
    productId: '6970fcb5c8a5341b443e0291',
    productName: 'Fortress Strider Tee',
    productDescription:
      'Epic lone wanderer storms the ancient cobble fortress—sunglasses on, ready for adventure.',
    price: 45,
    quantity: 1,
    imageUrl:
      'https://pub-0695601915114c88a20b5aa2ba5091cd.r2.dev/media/6970fcb54a08ce69b25b626d.png',
    sellerId: '693f98995940a77e1d19246a',
    sellerName: 'Jackie Chan',
    categoryId: 'CAT-006', // CAT-006
  },
  {
    id: 'cart-item-4',
    productId: '6942c289286f80150d8801e1',
    productName: 'Pop Code Queen Tee',
    productDescription:
      'Stand out with bold code and bold color! This wearable artwork is for queens of the keyboard.',
    price: 30,
    quantity: 3,
    imageUrl:
      'https://pub-0695601915114c88a20b5aa2ba5091cd.r2.dev/media/6942c28a4244c6023e6f81c0.png',
    sellerId: '691f5fca370d294f8db7a3d6',
    sellerName: 'Joon Kim',
    categoryId: 'CAT-003', // CAT-003
  },
];
