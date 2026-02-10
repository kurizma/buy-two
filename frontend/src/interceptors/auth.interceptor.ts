import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('user');

  let headers: { [key: string]: string } = {};

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  if (userId) {
    try {
      const user = JSON.parse(userId);
      if (user.id) {
        headers['X-USER-ID'] = user.id; // ex. "6944162944b627014e77a2f5"
      }
      if (user.role) {
        headers['X-USER-ROLE'] = user.role; // ex. "CLIENT"
      }
    } catch (e) {
      console.warn('Invalid user data in localStorage');
      localStorage.removeItem('user'); // Clean up invalid data
    }
  }

  const authReq = Object.keys(headers).length ? req.clone({ setHeaders: headers }) : req;

  return next(authReq);
};
