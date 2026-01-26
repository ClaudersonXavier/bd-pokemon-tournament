-- CreateTable
CREATE TABLE "ataque" (
    "poder" INTEGER NOT NULL,
    "categoria" VARCHAR(255) NOT NULL,
    "nome" VARCHAR(255) NOT NULL,
    "tipo_nome" VARCHAR(255) NOT NULL,

    CONSTRAINT "ataque_pkey" PRIMARY KEY ("nome")
);

-- CreateTable
CREATE TABLE "batalha" (
    "rodada" INTEGER NOT NULL,
    "horario_fim" TIMESTAMP(6) NOT NULL,
    "horario_inicio" TIMESTAMP(6) NOT NULL,
    "id" BIGSERIAL NOT NULL,
    "time_vencedor_id" BIGINT,
    "torneio_id" BIGINT,

    CONSTRAINT "batalha_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "batalha_times_participantes" (
    "batalha_id" BIGINT NOT NULL,
    "times_participantes_id" BIGINT NOT NULL,

    CONSTRAINT "batalha_times_participantes_pkey" PRIMARY KEY ("batalha_id","times_participantes_id")
);

-- CreateTable
CREATE TABLE "especie" (
    "imagem_url" VARCHAR(255) NOT NULL,
    "nome" VARCHAR(255) NOT NULL,

    CONSTRAINT "especie_pkey" PRIMARY KEY ("nome")
);

-- CreateTable
CREATE TABLE "especie_tipos" (
    "especie_nome" VARCHAR(255) NOT NULL,
    "tipos_nome" VARCHAR(255) NOT NULL,

    CONSTRAINT "especie_tipos_pkey" PRIMARY KEY ("especie_nome","tipos_nome")
);

-- CreateTable
CREATE TABLE "pokemon" (
    "id" BIGSERIAL NOT NULL,
    "treinador_id" BIGINT,
    "apelido" VARCHAR(255) NOT NULL,
    "especie_nome" VARCHAR(255),

    CONSTRAINT "pokemon_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "pokemon_ataques" (
    "pokemon_id" BIGINT NOT NULL,
    "ataques_nome" VARCHAR(255) NOT NULL,

    CONSTRAINT "pokemon_ataques_pkey" PRIMARY KEY ("pokemon_id","ataques_nome")
);

-- CreateTable
CREATE TABLE "pokemon_times" (
    "pokemon_id" BIGINT NOT NULL,
    "times_id" BIGINT NOT NULL,

    CONSTRAINT "pokemon_times_pkey" PRIMARY KEY ("pokemon_id","times_id")
);

-- CreateTable
CREATE TABLE "time" (
    "id" BIGSERIAL NOT NULL,
    "treinador_id" BIGINT,
    "nome" VARCHAR(255) NOT NULL,

    CONSTRAINT "time_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "time_pokemons" (
    "pokemons_id" BIGINT NOT NULL,
    "time_id" BIGINT NOT NULL,

    CONSTRAINT "time_pokemons_pkey" PRIMARY KEY ("pokemons_id","time_id")
);

-- CreateTable
CREATE TABLE "time_torneios" (
    "time_id" BIGINT NOT NULL,
    "torneios_id" BIGINT NOT NULL,

    CONSTRAINT "time_torneios_pkey" PRIMARY KEY ("time_id","torneios_id")
);

-- CreateTable
CREATE TABLE "tipo" (
    "nome" VARCHAR(255) NOT NULL,

    CONSTRAINT "tipo_pkey" PRIMARY KEY ("nome")
);

-- CreateTable
CREATE TABLE "torneio" (
    "qtd_rodadas" INTEGER NOT NULL,
    "qtd_times" INTEGER NOT NULL,
    "data_fim" TIMESTAMP(6) NOT NULL,
    "data_inicio" TIMESTAMP(6) NOT NULL,
    "id" BIGSERIAL NOT NULL,
    "nome" VARCHAR(255) NOT NULL,

    CONSTRAINT "torneio_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "torneio_times" (
    "times_id" BIGINT NOT NULL,
    "torneio_id" BIGINT NOT NULL,

    CONSTRAINT "torneio_times_pkey" PRIMARY KEY ("times_id","torneio_id")
);

-- CreateTable
CREATE TABLE "treinador" (
    "id" BIGSERIAL NOT NULL,
    "nome" VARCHAR(255) NOT NULL,

    CONSTRAINT "treinador_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "batalha_time_vencedor_id_key" ON "batalha"("time_vencedor_id");

-- CreateIndex
CREATE UNIQUE INDEX "pokemon_apelido_key" ON "pokemon"("apelido");

-- AddForeignKey
ALTER TABLE "ataque" ADD CONSTRAINT "fkkxv7h9ko1dq7ol6d5wrue8wjh" FOREIGN KEY ("tipo_nome") REFERENCES "tipo"("nome") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "batalha" ADD CONSTRAINT "fk4n7p7fiytuh0lagyvxwtlsbmr" FOREIGN KEY ("torneio_id") REFERENCES "torneio"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "batalha" ADD CONSTRAINT "fkc65apkqov09lhd0kfo3kg8xme" FOREIGN KEY ("time_vencedor_id") REFERENCES "time"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "batalha_times_participantes" ADD CONSTRAINT "fk3ck9cpoc4fe4f5qb54xwisayb" FOREIGN KEY ("batalha_id") REFERENCES "batalha"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "batalha_times_participantes" ADD CONSTRAINT "fkhlhvxah8fo9ppa8n9qdxjbu6e" FOREIGN KEY ("times_participantes_id") REFERENCES "time"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "especie_tipos" ADD CONSTRAINT "fkge4wnnwrstw2iq57a55qix3pc" FOREIGN KEY ("tipos_nome") REFERENCES "tipo"("nome") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "especie_tipos" ADD CONSTRAINT "fkjjmt4t6i8sscy0bnkw5tto08m" FOREIGN KEY ("especie_nome") REFERENCES "especie"("nome") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "pokemon" ADD CONSTRAINT "fke1kfos2gpco9y0vce1cqh8yab" FOREIGN KEY ("especie_nome") REFERENCES "especie"("nome") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "pokemon" ADD CONSTRAINT "fkne6njqhdth9q1ulcpf7mervc7" FOREIGN KEY ("treinador_id") REFERENCES "treinador"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "pokemon_ataques" ADD CONSTRAINT "fkd8s3plgl2nydhftfrpic6t75l" FOREIGN KEY ("ataques_nome") REFERENCES "ataque"("nome") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "pokemon_ataques" ADD CONSTRAINT "fkkdg46mjunx6rvj29qxqxp97hq" FOREIGN KEY ("pokemon_id") REFERENCES "pokemon"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "pokemon_times" ADD CONSTRAINT "fk12rmy3b37fypaw2im4ukdbkst" FOREIGN KEY ("times_id") REFERENCES "time"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "pokemon_times" ADD CONSTRAINT "fkkfp5plqdoyog2jirh14fn1fgc" FOREIGN KEY ("pokemon_id") REFERENCES "pokemon"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "time" ADD CONSTRAINT "fkaqbcqb20rumv6cpphytr55nkf" FOREIGN KEY ("treinador_id") REFERENCES "treinador"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "time_pokemons" ADD CONSTRAINT "fkg5qgs9h44anrtnlpw12hxlp3t" FOREIGN KEY ("time_id") REFERENCES "time"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "time_pokemons" ADD CONSTRAINT "fkslmul2sa9m0w44idnvnmfmvdy" FOREIGN KEY ("pokemons_id") REFERENCES "pokemon"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "time_torneios" ADD CONSTRAINT "fk4hw6t7hlb74m7ke4ki3h99k6i" FOREIGN KEY ("torneios_id") REFERENCES "torneio"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "time_torneios" ADD CONSTRAINT "fknifd53q6kcfj35bsl56s620a5" FOREIGN KEY ("time_id") REFERENCES "time"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "torneio_times" ADD CONSTRAINT "fk7pgehlqf0dx0brs94fjtsxr8h" FOREIGN KEY ("torneio_id") REFERENCES "torneio"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE "torneio_times" ADD CONSTRAINT "fkl8eixqflwswdvtnjefw45sh4x" FOREIGN KEY ("times_id") REFERENCES "time"("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
