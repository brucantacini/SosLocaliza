package com.example.SosLocaliza.services;

import com.example.SosLocaliza.gateways.ProcedureRepository;
import com.example.SosLocaliza.gateways.dtos.request.LocalizacaoProcedureRequestDto;
import com.example.SosLocaliza.gateways.dtos.request.UsuarioProcedureRequestDto;
import com.example.SosLocaliza.gateways.dtos.response.ProcedureResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProcedureService {
    
    private final ProcedureRepository procedureRepository;
    
    // ============================================================
    // SERVIÇOS DE LOCALIZACAO
    // ============================================================
    
    public ProcedureResponseDto insertLocalizacao(LocalizacaoProcedureRequestDto dto) {
        try {
            Map<String, Object> result = procedureRepository.insertLocalizacao(
                    dto.getNomeLocal(),
                    dto.getRuaLocal(),
                    dto.getNumeroLocal(),
                    dto.getCepLocal()
            );
            
            String mensagem = (String) result.get("p_mensagem");
            BigDecimal idBigDecimal = (BigDecimal) result.get("p_id_local");
            Long id = idBigDecimal != null ? idBigDecimal.longValue() : null;
            
            if (mensagem != null && mensagem.contains("Erro")) {
                return ProcedureResponseDto.erro(mensagem);
            }
            
            return ProcedureResponseDto.sucesso(id, mensagem);
        } catch (Exception e) {
            log.error("Erro ao inserir localização via procedure", e);
            return ProcedureResponseDto.erro("Erro ao inserir localização: " + e.getMessage());
        }
    }
    
    public ProcedureResponseDto updateLocalizacao(Long idLocal, LocalizacaoProcedureRequestDto dto) {
        try {
            Map<String, Object> result = procedureRepository.updateLocalizacao(
                    idLocal,
                    dto.getNomeLocal(),
                    dto.getRuaLocal(),
                    dto.getNumeroLocal(),
                    dto.getCepLocal()
            );
            
            String mensagem = (String) result.get("p_mensagem");
            
            if (mensagem != null && mensagem.contains("Erro")) {
                return ProcedureResponseDto.erro(mensagem);
            }
            
            return ProcedureResponseDto.sucesso(idLocal, mensagem);
        } catch (Exception e) {
            log.error("Erro ao atualizar localização via procedure", e);
            return ProcedureResponseDto.erro("Erro ao atualizar localização: " + e.getMessage());
        }
    }
    
    public ProcedureResponseDto deleteLocalizacao(Long idLocal) {
        try {
            Map<String, Object> result = procedureRepository.deleteLocalizacao(idLocal);
            
            String mensagem = (String) result.get("p_mensagem");
            
            if (mensagem != null && mensagem.contains("Erro")) {
                return ProcedureResponseDto.erro(mensagem);
            }
            
            return ProcedureResponseDto.sucesso(idLocal, mensagem);
        } catch (Exception e) {
            log.error("Erro ao excluir localização via procedure", e);
            return ProcedureResponseDto.erro("Erro ao excluir localização: " + e.getMessage());
        }
    }
    
    // ============================================================
    // SERVIÇOS DE USUARIO
    // ============================================================
    
    public ProcedureResponseDto insertUsuario(UsuarioProcedureRequestDto dto) {
        try {
            Map<String, Object> result = procedureRepository.insertUsuario(
                    dto.getNomeCompleto(),
                    dto.getEmail(),
                    dto.getSenha(),
                    dto.getCpf(),
                    dto.getDataNascimento(),
                    dto.getIdLocal(),
                    dto.getAtivo()
            );
            
            String mensagem = (String) result.get("p_mensagem");
            BigDecimal idBigDecimal = (BigDecimal) result.get("p_id_usuario");
            Long id = idBigDecimal != null ? idBigDecimal.longValue() : null;
            
            if (mensagem != null && mensagem.contains("Erro")) {
                return ProcedureResponseDto.erro(mensagem);
            }
            
            return ProcedureResponseDto.sucesso(id, mensagem);
        } catch (Exception e) {
            log.error("Erro ao inserir usuário via procedure", e);
            return ProcedureResponseDto.erro("Erro ao inserir usuário: " + e.getMessage());
        }
    }
    
    public ProcedureResponseDto updateUsuario(Long idUsuario, UsuarioProcedureRequestDto dto) {
        try {
            Map<String, Object> result = procedureRepository.updateUsuario(
                    idUsuario,
                    dto.getNomeCompleto(),
                    dto.getEmail(),
                    dto.getSenha(),
                    dto.getCpf(),
                    dto.getDataNascimento(),
                    dto.getIdLocal(),
                    dto.getAtivo()
            );
            
            String mensagem = (String) result.get("p_mensagem");
            
            if (mensagem != null && mensagem.contains("Erro")) {
                return ProcedureResponseDto.erro(mensagem);
            }
            
            return ProcedureResponseDto.sucesso(idUsuario, mensagem);
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário via procedure", e);
            return ProcedureResponseDto.erro("Erro ao atualizar usuário: " + e.getMessage());
        }
    }
    
    public ProcedureResponseDto deleteUsuario(Long idUsuario) {
        try {
            Map<String, Object> result = procedureRepository.deleteUsuario(idUsuario);
            
            String mensagem = (String) result.get("p_mensagem");
            
            if (mensagem != null && mensagem.contains("Erro")) {
                return ProcedureResponseDto.erro(mensagem);
            }
            
            return ProcedureResponseDto.sucesso(idUsuario, mensagem);
        } catch (Exception e) {
            log.error("Erro ao excluir usuário via procedure", e);
            return ProcedureResponseDto.erro("Erro ao excluir usuário: " + e.getMessage());
        }
    }
}

