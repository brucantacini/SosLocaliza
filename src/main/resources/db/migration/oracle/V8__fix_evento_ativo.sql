-- Garante que eventos cadastrados apareçam em /api/eventos/ativos (JPA: findByAtivoTrue).
UPDATE T_SOS_EVENTO SET ATIVO = 1 WHERE ATIVO IS NULL OR ATIVO = 0;
