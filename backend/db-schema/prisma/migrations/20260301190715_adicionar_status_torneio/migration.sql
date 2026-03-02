CREATE TYPE "status_torneio_enum" AS ENUM ('ABERTO', 'EM_ANDAMENTO', 'ENCERRADO');

ALTER TABLE "torneio" ADD COLUMN "status" "status_torneio_enum";

UPDATE "torneio" SET "status" = 'ABERTO' WHERE "status" IS NULL;

ALTER TABLE "torneio" ALTER COLUMN "status" SET NOT NULL;
