-- Add columns to treinador
ALTER TABLE "treinador" ADD COLUMN "email" VARCHAR(255);
ALTER TABLE "treinador" ADD COLUMN "senha" VARCHAR(255);

-- Enforce constraints
ALTER TABLE "treinador" ALTER COLUMN "email" SET NOT NULL;
ALTER TABLE "treinador" ALTER COLUMN "senha" SET NOT NULL;

-- CreateIndex
CREATE UNIQUE INDEX "treinador_email_key" ON "treinador"("email");
