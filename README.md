# Projeto Banquinho BD

Projeto simples usando a PokeAPI como fonte de dados.

## Docker (PostgreSQL)
O backend usa um banco PostgreSQL via Docker Compose.

### Como rodar
1. Na raiz do repo:
   - `cd backend`
   - `docker compose up -d`
2. Para parar:
   - `docker compose down`

### Credenciais de acesso
As credenciais estão definidas em `backend/docker-compose.yml` e usadas em `backend/src/main/resources/application.properties`:
- Host: `localhost`
- Porta: `5432`
- Database: `projeto_banquinho`
- Usuário: `dev`
- Senha: `dev`

## Links uteis
- API: https://pokeapi.co/
- Documentacao: https://pokeapi.co/docs/v2
