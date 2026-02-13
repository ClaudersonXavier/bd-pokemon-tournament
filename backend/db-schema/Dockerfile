FROM node:22-slim

WORKDIR /app

# Install openssl (required by Prisma)
RUN apt-get update && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy Prisma schema and migrations
COPY prisma ./prisma

# Run migrations
CMD ["npx", "prisma", "migrate", "deploy"]
