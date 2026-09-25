# Étape 1 : Build de l'application Angular
FROM node:22-slim AS builder
WORKDIR /app

# Optimisation du cache des dépendances npm
COPY package*.json ./
RUN npm ci

# Copie du code source et compilation pour la production
COPY . .
RUN npm run build

# Étape 2 : Image runtime Nginx alpine légère
FROM nginx:alpine

# Remplacement de la configuration Nginx par défaut
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copie de l'artefact compilé Angular
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
