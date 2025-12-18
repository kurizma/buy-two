const papaAvatar = 'assets/avatars/papa.jpg';
const emilyAvatar = 'assets/avatars/emily.jpg'; //002
const samAvatar = 'assets/avatars/koaladrinkscoffee.webp'; //004
const sarahAvatar = 'assets/avatars/panda.webp'; //006
const mamaAvatar = 'assets/avatars/mama.jpg'; //008
const lindaAvatar = 'assets/avatars/linda.jpg';

export const MOCK_USERS: User[] = [
  {
    id: 'USER-001',
    name: 'Papa Jones',
    email: 'papa@jones.com',
    password: 'Papa123!',
    role: 'client',
    avatar: papaAvatar,
  },
  {
    id: 'USER-002',
    name: 'Emily Apple',
    email: 'emily@apple.com',
    password: 'Emily123!',
    role: 'seller',
    avatar: emilyAvatar,
  },
  {
    id: 'USER-004',
    name: 'Sam Smith',
    email: 'Sam@smith.com',
    password: 'Sams123!',
    role: 'seller',
    avatar: samAvatar,
  },
  {
    id: 'USER-006',
    name: 'Sarah Lee',
    email: 'sarah@lee.com',
    password: 'Sarah123!',
    role: 'seller',
    avatar: sarahAvatar,
  },
  {
    id: 'USER-008',
    name: 'Mama Blues',
    email: 'mama@blues.com',
    password: 'Mama123!',
    role: 'seller',
    avatar: mamaAvatar,
  },
  {
    id: 'USER-010',
    name: 'Linda Long',
    email: 'linda@long.com',
    password: 'Linda!',
    role: 'seller',
    avatar: lindaAvatar,
  },
];


//helper functions to simulate user operations
export function authenticateUser(email: string, password: string): User | null {
  return MOCK_USERS.find((user) => user.email === email && user.password === password) || null;
}

export function getUserByEmail(email: string): User | null {
  return MOCK_USERS.find((user) => user.email === email) || null;
}

export function updateUser(dto: Partial<User>): User | null {
  const user = MOCK_USERS.find((u) => u.id === dto.id);
  if (!user) return null;
  if (dto.name !== undefined) user.name = dto.name;
  if (dto.email !== undefined) user.email = dto.email;
  if (dto.avatar !== undefined) user.avatar = dto.avatar;
  if (dto.password !== undefined) user.password = dto.password;
  if (dto.role !== undefined) user.role = dto.role;
  return user;
}