# SEPA26 - Service REST ISO 20022 & Frontend Angular

Projet de gestion et de traitement de transactions SEPA (ISO 20022).

---

## Structure du Projet

```text
sepahub/
├── backend/          # API REST Spring Boot 3 (Java 21, JPA, XSD, XSLT)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── frontend/         # Application SPA Angular 22 & Nginx
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src/
├── docker-compose.yml # Orchestration multi-conteneurs
├── .env.example       # Gabarit des variables d'environnement
├── doc/               # Rapport académique (rapport.pdf, rapport.tex)
└── README.md
```

---

## Architecture des Services

Le projet s'exécute dans une architecture multi-conteneurs orchestrée par **Docker Compose** :

| Service | Répertoire source | Rôle | Port hôte |
| :--- | :--- | :--- | :--- |
| **`frontend`** | `frontend/` | Application Web Angular (SPA servie via Nginx) | [http://localhost:4200](http://localhost:4200) |
| **`backend`** | `backend/` | API REST Spring Boot 3 (Java 21) | [http://localhost:8100](http://localhost:8100) |
| **`db`** | - | Base de données MariaDB 11 | `3306` |

Le conteneur `frontend` intègre un reverse-proxy Nginx sur `/sepa26/` vers le `backend`, évitant ainsi toute contrainte CORS lors de l'utilisation dans le navigateur.

---

## Lancement Rapide (Docker Compose)

1. *(Optionnel)* Personnaliser les identifiants de base de données à partir du gabarit :
```bash
cp .env.example .env
```

2. Compiler et démarrer l'ensemble des services en arrière-plan :
```bash
docker compose up --build -d
```

Pour arrêter les services :
```bash
docker compose down
```

Pour consulter les logs en temps réel :
```bash
docker compose logs -f
```

---

## Développement & Tests Locaux

- **Backend (Maven)** :
  ```bash
  cd backend
  mvn clean test
  ```
- **Frontend (Angular)** :
  ```bash
  cd frontend
  npm install
  npm test -- --watch=false
  npm run build
  ```

---

## Points d'Accès Principaux

- **Interface Frontend Angular :** [http://localhost:4200](http://localhost:4200)
  - Tableau de bord des transactions (résumés et détails complets)
  - Formulaire de transfert / validation / insertion XML SEPA
  - Recherche multicritère
  - Documentation interactive des endpoints (consommée dynamiquement)
- **API REST Spring Boot :** [http://localhost:8100/](http://localhost:8100/) (métadonnées & statut de santé en JSON)
- **Catalogue de Documentation REST :** [http://localhost:8100/help](http://localhost:8100/help) (flux JSON des routes)
- **Rapport de Conception complet :** [doc/rapport.pdf](doc/rapport.pdf)
