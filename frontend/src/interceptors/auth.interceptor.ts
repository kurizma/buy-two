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
    }
  }

  const authReq = Object.keys(headers).length ? req.clone({ setHeaders: headers }) : req;

  return next(authReq);
};

// import { HttpInterceptorFn } from '@angular/common/http';

// export const authInterceptor: HttpInterceptorFn = (req, next) => {
//   const token = localStorage.getItem('token');
//   if (token) {
//     const authReq = req.clone({
//       setHeaders: {
//         Authorization: `Bearer ${token}`,
//       },
//     });
//     return next(authReq);
//   }
//   return next(req);
// };
