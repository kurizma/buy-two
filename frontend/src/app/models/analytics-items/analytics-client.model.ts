// ************* Mock analytics data *****************

export const analyticsClientItems = [
  { name: 'Code Wizard Tee', categories: 'CAT-001', count: 2, amount: 58 },
  { name: 'Keep Coding Tee', categories: 'CAT-001', count: 1, amount: 28 },
  { name: 'Action Noir Tee', categories: 'CAT-006', count: 1, amount: 45 },
];

/*
I want to test with 4 users ( 1 active seller, 1 active client, a new seller, and a new client:

1. Active seller: 
joon@kin.kr
analyticsSellerItems = [
  { name: 'Code Wizard Tee', categories: 'CAT-001', count: 4, amount: 110 },
  { name: 'Pop Code Queen Tee', categories: 'CAT-003', count: 2, amount: 150 },
  { name: 'Classic Portrait Tee', categories: 'CAT-006', count: 10, amount: 450 },
];

2. Active client:
dada@dee.com
analyticsClientItems = [
  { name: 'Code Wizard Tee', categories: 'CAT-001', count: 2, amount: 58 },
  { name: 'Keep Coding Tee', categories: 'CAT-001', count: 1, amount: 28 },
  { name: 'Action Noir Tee', categories: 'CAT-006', count: 1, amount: 45 },
];
3. New seller:
angu@readme.md
analyticsSellerItems = [];

4. New client:
john@doe.com
analyticsClientItems = [];
*/
