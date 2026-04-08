package com.example.SosLocaliza.gateways;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.time.LocalDate;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProcedureRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public Map<String, Object> insertLocalizacao(String nomeLocal, String ruaLocal, 
                                                 Integer numeroLocal, String cepLocal) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(null)
                    .withProcedureName("PROC_INSERT_LOCALIZACAO")
                    .declareParameters(
                            new SqlParameter("p_nome_local", Types.VARCHAR),
                            new SqlParameter("p_rua_local", Types.VARCHAR),
                            new SqlParameter("p_numero_local", Types.NUMERIC),
                            new SqlParameter("p_cep_local", Types.VARCHAR),
                            new SqlOutParameter("p_id_local", Types.NUMERIC),
                            new SqlOutParameter("p_mensagem", Types.VARCHAR)
                    );
            
            Map<String, Object> result = call.execute(
                    nomeLocal,
                    ruaLocal,
                    numeroLocal,
                    cepLocal
            );
            
            log.info("Procedure PROC_INSERT_LOCALIZACAO executada. Resultado: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Erro ao executar PROC_INSERT_LOCALIZACAO", e);
            throw new RuntimeException("Erro ao inserir localização: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> updateLocalizacao(Long idLocal, String nomeLocal, 
                                                 String ruaLocal, Integer numeroLocal, String cepLocal) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(null)
                    .withProcedureName("PROC_UPDATE_LOCALIZACAO")
                    .declareParameters(
                            new SqlParameter("p_id_local", Types.NUMERIC),
                            new SqlParameter("p_nome_local", Types.VARCHAR),
                            new SqlParameter("p_rua_local", Types.VARCHAR),
                            new SqlParameter("p_numero_local", Types.NUMERIC),
                            new SqlParameter("p_cep_local", Types.VARCHAR),
                            new SqlOutParameter("p_mensagem", Types.VARCHAR)
                    );
            
            Map<String, Object> result = call.execute(
                    idLocal,
                    nomeLocal,
                    ruaLocal,
                    numeroLocal,
                    cepLocal
            );
            
            log.info("Procedure PROC_UPDATE_LOCALIZACAO executada. Resultado: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Erro ao executar PROC_UPDATE_LOCALIZACAO", e);
            throw new RuntimeException("Erro ao atualizar localização: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> deleteLocalizacao(Long idLocal) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(null)
                    .withProcedureName("PROC_DELETE_LOCALIZACAO")
                    .declareParameters(
                            new SqlParameter("p_id_local", Types.NUMERIC),
                            new SqlOutParameter("p_mensagem", Types.VARCHAR)
                    );
            
            Map<String, Object> result = call.execute(idLocal);
            
            log.info("Procedure PROC_DELETE_LOCALIZACAO executada. Resultado: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Erro ao executar PROC_DELETE_LOCALIZACAO", e);
            throw new RuntimeException("Erro ao excluir localização: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> insertUsuario(String nomeCompleto, String email, String senha,
                                             String cpf, LocalDate dataNascimento, Long idLocal, Integer ativo) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(null)
                    .withProcedureName("PROC_INSERT_USUARIO")
                    .declareParameters(
                            new SqlParameter("p_nome_completo", Types.VARCHAR),
                            new SqlParameter("p_email", Types.VARCHAR),
                            new SqlParameter("p_senha", Types.VARCHAR),
                            new SqlParameter("p_cpf", Types.VARCHAR),
                            new SqlParameter("p_data_nascimento", Types.DATE),
                            new SqlParameter("p_id_local", Types.NUMERIC),
                            new SqlParameter("p_ativo", Types.NUMERIC),
                            new SqlOutParameter("p_id_usuario", Types.NUMERIC),
                            new SqlOutParameter("p_mensagem", Types.VARCHAR)
                    );
            
            Map<String, Object> result = call.execute(
                    nomeCompleto,
                    email,
                    senha,
                    cpf,
                    java.sql.Date.valueOf(dataNascimento),
                    idLocal,
                    ativo
            );
            
            log.info("Procedure PROC_INSERT_USUARIO executada. Resultado: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Erro ao executar PROC_INSERT_USUARIO", e);
            throw new RuntimeException("Erro ao inserir usuário: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> updateUsuario(Long idUsuario, String nomeCompleto, String email,
                                             String senha, String cpf, LocalDate dataNascimento,
                                             Long idLocal, Integer ativo) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(null)
                    .withProcedureName("PROC_UPDATE_USUARIO")
                    .declareParameters(
                            new SqlParameter("p_id_usuario", Types.NUMERIC),
                            new SqlParameter("p_nome_completo", Types.VARCHAR),
                            new SqlParameter("p_email", Types.VARCHAR),
                            new SqlParameter("p_senha", Types.VARCHAR),
                            new SqlParameter("p_cpf", Types.VARCHAR),
                            new SqlParameter("p_data_nascimento", Types.DATE),
                            new SqlParameter("p_id_local", Types.NUMERIC),
                            new SqlParameter("p_ativo", Types.NUMERIC),
                            new SqlOutParameter("p_mensagem", Types.VARCHAR)
                    );
            
            Map<String, Object> result = call.execute(
                    idUsuario,
                    nomeCompleto,
                    email,
                    senha,
                    cpf != null ? cpf : null,
                    dataNascimento != null ? java.sql.Date.valueOf(dataNascimento) : null,
                    idLocal,
                    ativo
            );
            
            log.info("Procedure PROC_UPDATE_USUARIO executada. Resultado: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Erro ao executar PROC_UPDATE_USUARIO", e);
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> deleteUsuario(Long idUsuario) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(null)
                    .withProcedureName("PROC_DELETE_USUARIO")
                    .declareParameters(
                            new SqlParameter("p_id_usuario", Types.NUMERIC),
                            new SqlOutParameter("p_mensagem", Types.VARCHAR)
                    );
            
            Map<String, Object> result = call.execute(idUsuario);
            
            log.info("Procedure PROC_DELETE_USUARIO executada. Resultado: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Erro ao executar PROC_DELETE_USUARIO", e);
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }
}

