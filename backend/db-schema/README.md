# Como rodar o Prisma

## Pré-requisitos

- Node.js instalado
- Docker rodando o PostgreSQL (veja o `docker-compose.yml`)
- Variáveis de ambiente configuradas no `.env`

## Passos

1. **Instale as dependências:**
   ```bash
   npm install
   ```

2. **Suba o banco de dados com Docker:**
   ```bash
   docker-compose up -d
   ```

3. **Configure a variável de ambiente no `.env`:**
   ```
   DATABASE_URL="postgres://user:senha@localhost:porta/database-name?schema=public"
   ```

4. **Rode as migrações do Prisma:**
   ```bash
   npx prisma migrate dev
   ```

5. **Caso não funcione tente:**
   ```bash
    npx prisma migrate dev --name init --url "postgres://user:senha@localhost:porta/database-name?schema=public"
   ```


## Comandos úteis

- Visualizar o banco com Prisma Studio:
  ```bash
  npx prisma studio
  ```

- Verificar o status das migrações:
  ```bash
  npx prisma migrate status
  ```

## Observações

- Certifique-se que o banco está rodando antes de executar comandos do Prisma.
- As variáveis do `.env` são carregadas automaticamente se você usar `import "dotenv/config"` no seu projeto.