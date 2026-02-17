FROM node:22-slim

WORKDIR /app

# Instalar dependências necessárias para Prisma
RUN apt-get update \
  && apt-get install -y openssl \
  && rm -rf /var/lib/apt/lists/*

# Copiar arquivos de dependência
COPY package*.json ./

# Instalar dependências
RUN npm install

# Copiar schema e migrations
COPY prisma ./prisma

# Gerar Prisma Client
RUN npx prisma generate

# Executar migrations ao subir o container
CMD ["npx", "prisma", "migrate", "deploy"]