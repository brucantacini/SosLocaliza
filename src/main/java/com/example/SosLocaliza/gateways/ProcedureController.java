package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.gateways.dtos.request.LocalizacaoProcedureRequestDto;
import com.example.SosLocaliza.gateways.dtos.request.UsuarioProcedureRequestDto;
import com.example.SosLocaliza.gateways.dtos.response.ProcedureResponseDto;
import com.example.SosLocaliza.services.ProcedureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/procedures")
@RequiredArgsConstructor
@Slf4j
public class ProcedureController {
    
    private final ProcedureService procedureService;
    
    // ============================================================
    // ENDPOINTS DE LOCALIZACAO
    // ============================================================
    
    /**
     * POST /procedures/localizacao
     * Insere uma nova localização usando a procedure PROC_INSERT_LOCALIZACAO
     */
    @PostMapping("/localizacao")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProcedureResponseDto> insertLocalizacao(
            @RequestBody @Valid LocalizacaoProcedureRequestDto dto) {
        log.info("Chamando procedure PROC_INSERT_LOCALIZACAO com dados: {}", dto);
        ProcedureResponseDto response = procedureService.insertLocalizacao(dto);
        
        if (response.getSucesso()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * PUT /procedures/localizacao/{id}
     * Atualiza uma localização usando a procedure PROC_UPDATE_LOCALIZACAO
     */
    @PutMapping("/localizacao/{id}")
    public ResponseEntity<ProcedureResponseDto> updateLocalizacao(
            @PathVariable Long id,
            @RequestBody @Valid LocalizacaoProcedureRequestDto dto) {
        log.info("Chamando procedure PROC_UPDATE_LOCALIZACAO para ID: {}", id);
        ProcedureResponseDto response = procedureService.updateLocalizacao(id, dto);
        
        if (response.getSucesso()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * DELETE /procedures/localizacao/{id}
     * Exclui uma localização usando a procedure PROC_DELETE_LOCALIZACAO
     */
    @DeleteMapping("/localizacao/{id}")
    public ResponseEntity<ProcedureResponseDto> deleteLocalizacao(@PathVariable Long id) {
        log.info("Chamando procedure PROC_DELETE_LOCALIZACAO para ID: {}", id);
        ProcedureResponseDto response = procedureService.deleteLocalizacao(id);
        
        if (response.getSucesso()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // ============================================================
    // ENDPOINTS DE USUARIO
    // ============================================================
    
    /**
     * POST /procedures/usuario
     * Insere um novo usuário usando a procedure PROC_INSERT_USUARIO
     */
    @PostMapping("/usuario")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProcedureResponseDto> insertUsuario(
            @RequestBody @Valid UsuarioProcedureRequestDto dto) {
        log.info("Chamando procedure PROC_INSERT_USUARIO com dados: {}", dto);
        ProcedureResponseDto response = procedureService.insertUsuario(dto);
        
        if (response.getSucesso()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * PUT /procedures/usuario/{id}
     * Atualiza um usuário usando a procedure PROC_UPDATE_USUARIO
     */
    @PutMapping("/usuario/{id}")
    public ResponseEntity<ProcedureResponseDto> updateUsuario(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioProcedureRequestDto dto) {
        log.info("Chamando procedure PROC_UPDATE_USUARIO para ID: {}", id);
        ProcedureResponseDto response = procedureService.updateUsuario(id, dto);
        
        if (response.getSucesso()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * DELETE /procedures/usuario/{id}
     * Exclui um usuário usando a procedure PROC_DELETE_USUARIO
     */
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<ProcedureResponseDto> deleteUsuario(@PathVariable Long id) {
        log.info("Chamando procedure PROC_DELETE_USUARIO para ID: {}", id);
        ProcedureResponseDto response = procedureService.deleteUsuario(id);
        
        if (response.getSucesso()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}

