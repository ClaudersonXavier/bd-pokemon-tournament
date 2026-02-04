# Db Schema

## Como adicionar novas migracoes

1. Rode `npx prisma db pull`.
2. Rode `npx prisma migrate reset dev` e confirme com `y`.
3. Rode `npx prisma migrate dev` e informe o nome da migracao quando solicitado.
