package com.example.SosLocaliza.domains;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//  Entidade que representa uma mensagem SMS enviada
//  Mapeada para a tabela T_SOS_SMS no banco de dados

@With
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "T_SOS_SMS")
public class SmsMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_SMS")
    private String idSms;

    @Column(name = "REMETENTE", nullable = false, length = 100)
    private String remetente;

    @Column(name = "NUMERO_TELEFONE", nullable = false, length = 20)
    private String numeroTelefone;

    @Column(name = "MENSAGEM", nullable = false, length = 1000)
    private String mensagem;

    @Column(name = "DATA_ENVIO")
    private LocalDateTime dataEnvio;

    @Column(name = "ENVIADO_COM_SUCESSO")
    @Builder.Default
    private Boolean enviadoComSucesso = false;

    @Column(name = "ERRO", length = 500)
    private String erro;

    @Column(name = "DDD", length = 3)
    private String ddd;

    @Column(name = "NUMERO", length = 10)
    private String numero;

    // Relacionamento com o evento relacionado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EVENTO")
    private Evento evento;

    @PrePersist
    protected void onCreate() {
        if (dataEnvio == null) {
            dataEnvio = LocalDateTime.now();
        }
    }
}
