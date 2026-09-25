# SEPA26 - Système de transactions SEPA

Projet de gestion et de traitement de transactions SEPA (ISO 20022).



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



## Architecture des Services

Le projet s'exécute dans une architecture multi-conteneurs orchestrée par **Docker Compose** :

| Service | Répertoire source | Rôle | Port hôte |
| : | : | : | : |
| **`frontend`** | `frontend/` | Application Web Angular (SPA servie via Nginx) | [http://localhost:4200](http://localhost:4200) |
| **`backend`** | `backend/` | API REST Spring Boot 3 (Java 21) | [http://localhost:8100](http://localhost:8100) |
| **`db`** | - | Base de données MariaDB 11 | `3306` |

Le conteneur `frontend` intègre un reverse-proxy Nginx sur `/sepa26/` vers le `backend`, évitant ainsi toute contrainte CORS lors de l'utilisation dans le navigateur.



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

## Documentation

Le rapport complet du projet est disponible dans le dossier `doc/` :

- **Rapport PDF** : [`doc/rapport.pdf`](doc/rapport.pdf)
- **Source LaTeX** : [`doc/rapport.tex`](doc/rapport.tex)

## Fork

Ce projet est un fork du projet [SEPA26](https://github.com/M1-ROUEN-GIL/application-rest).