-- CreateTable
CREATE TABLE "Treinador" (
    "_id" TEXT NOT NULL,
    "nome" TEXT NOT NULL,

    CONSTRAINT "Treinador_pkey" PRIMARY KEY ("_id")
);

-- CreateTable
CREATE TABLE "Time" (
    "id" TEXT NOT NULL,
    "nome" TEXT NOT NULL,
    "treinadorId" TEXT NOT NULL,

    CONSTRAINT "Time_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Pokemon" (
    "_id" TEXT NOT NULL,
    "apelido" TEXT,
    "treinadorId" TEXT NOT NULL,
    "especieNome" TEXT NOT NULL,

    CONSTRAINT "Pokemon_pkey" PRIMARY KEY ("_id")
);

-- CreateTable
CREATE TABLE "Especie" (
    "nome" TEXT NOT NULL,
    "imageUrl" TEXT NOT NULL,

    CONSTRAINT "Especie_pkey" PRIMARY KEY ("nome")
);

-- CreateTable
CREATE TABLE "Tipo" (
    "nome" TEXT NOT NULL,

    CONSTRAINT "Tipo_pkey" PRIMARY KEY ("nome")
);

-- CreateTable
CREATE TABLE "Ataque" (
    "nome" TEXT NOT NULL,
    "categoria" TEXT NOT NULL,
    "poder" INTEGER NOT NULL,
    "tipoNome" TEXT NOT NULL,

    CONSTRAINT "Ataque_pkey" PRIMARY KEY ("nome")
);

-- CreateTable
CREATE TABLE "Batalha" (
    "_id" TEXT NOT NULL,
    "horaInicio" TIMESTAMP(3) NOT NULL,
    "horaFim" TIMESTAMP(3) NOT NULL,
    "rodada" INTEGER NOT NULL,
    "vencedorId" TEXT,
    "torneioId" TEXT NOT NULL,

    CONSTRAINT "Batalha_pkey" PRIMARY KEY ("_id")
);

-- CreateTable
CREATE TABLE "Torneio" (
    "id" TEXT NOT NULL,
    "nome" TEXT NOT NULL,
    "dataInicio" TIMESTAMP(3) NOT NULL,
    "dataFim" TIMESTAMP(3) NOT NULL,
    "vencedor" TEXT,

    CONSTRAINT "Torneio_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "_TimeToTorneio" (
    "A" TEXT NOT NULL,
    "B" TEXT NOT NULL,

    CONSTRAINT "_TimeToTorneio_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateTable
CREATE TABLE "_PokemonToTime" (
    "A" TEXT NOT NULL,
    "B" TEXT NOT NULL,

    CONSTRAINT "_PokemonToTime_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateTable
CREATE TABLE "_EspecieToTipo" (
    "A" TEXT NOT NULL,
    "B" TEXT NOT NULL,

    CONSTRAINT "_EspecieToTipo_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateTable
CREATE TABLE "_AtaqueToPokemon" (
    "A" TEXT NOT NULL,
    "B" TEXT NOT NULL,

    CONSTRAINT "_AtaqueToPokemon_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateTable
CREATE TABLE "_ParticipaBatalha" (
    "A" TEXT NOT NULL,
    "B" TEXT NOT NULL,

    CONSTRAINT "_ParticipaBatalha_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateIndex
CREATE INDEX "_TimeToTorneio_B_index" ON "_TimeToTorneio"("B");

-- CreateIndex
CREATE INDEX "_PokemonToTime_B_index" ON "_PokemonToTime"("B");

-- CreateIndex
CREATE INDEX "_EspecieToTipo_B_index" ON "_EspecieToTipo"("B");

-- CreateIndex
CREATE INDEX "_AtaqueToPokemon_B_index" ON "_AtaqueToPokemon"("B");

-- CreateIndex
CREATE INDEX "_ParticipaBatalha_B_index" ON "_ParticipaBatalha"("B");

-- AddForeignKey
ALTER TABLE "Time" ADD CONSTRAINT "Time_treinadorId_fkey" FOREIGN KEY ("treinadorId") REFERENCES "Treinador"("_id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Pokemon" ADD CONSTRAINT "Pokemon_treinadorId_fkey" FOREIGN KEY ("treinadorId") REFERENCES "Treinador"("_id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Pokemon" ADD CONSTRAINT "Pokemon_especieNome_fkey" FOREIGN KEY ("especieNome") REFERENCES "Especie"("nome") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Ataque" ADD CONSTRAINT "Ataque_tipoNome_fkey" FOREIGN KEY ("tipoNome") REFERENCES "Tipo"("nome") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Batalha" ADD CONSTRAINT "Batalha_vencedorId_fkey" FOREIGN KEY ("vencedorId") REFERENCES "Time"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Batalha" ADD CONSTRAINT "Batalha_torneioId_fkey" FOREIGN KEY ("torneioId") REFERENCES "Torneio"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_TimeToTorneio" ADD CONSTRAINT "_TimeToTorneio_A_fkey" FOREIGN KEY ("A") REFERENCES "Time"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_TimeToTorneio" ADD CONSTRAINT "_TimeToTorneio_B_fkey" FOREIGN KEY ("B") REFERENCES "Torneio"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_PokemonToTime" ADD CONSTRAINT "_PokemonToTime_A_fkey" FOREIGN KEY ("A") REFERENCES "Pokemon"("_id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_PokemonToTime" ADD CONSTRAINT "_PokemonToTime_B_fkey" FOREIGN KEY ("B") REFERENCES "Time"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_EspecieToTipo" ADD CONSTRAINT "_EspecieToTipo_A_fkey" FOREIGN KEY ("A") REFERENCES "Especie"("nome") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_EspecieToTipo" ADD CONSTRAINT "_EspecieToTipo_B_fkey" FOREIGN KEY ("B") REFERENCES "Tipo"("nome") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_AtaqueToPokemon" ADD CONSTRAINT "_AtaqueToPokemon_A_fkey" FOREIGN KEY ("A") REFERENCES "Ataque"("nome") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_AtaqueToPokemon" ADD CONSTRAINT "_AtaqueToPokemon_B_fkey" FOREIGN KEY ("B") REFERENCES "Pokemon"("_id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_ParticipaBatalha" ADD CONSTRAINT "_ParticipaBatalha_A_fkey" FOREIGN KEY ("A") REFERENCES "Batalha"("_id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_ParticipaBatalha" ADD CONSTRAINT "_ParticipaBatalha_B_fkey" FOREIGN KEY ("B") REFERENCES "Time"("id") ON DELETE CASCADE ON UPDATE CASCADE;
