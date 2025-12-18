# Stage 1: build Angular app
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
# Use the docker environment config
RUN npm run build -- --configuration=docker

# Stage 2: serve with nginx
FROM nginx:1.27-alpine
COPY --from=build /app/dist/grit-tee/browser /usr/share/nginx/html

COPY ssl/server.crt /etc/nginx/ssl/server.crt
COPY ssl/server.key /etc/nginx/ssl/server.key

# Optional: custom nginx config if you need it later
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 443
CMD ["nginx", "-g", "daemon off;"]
