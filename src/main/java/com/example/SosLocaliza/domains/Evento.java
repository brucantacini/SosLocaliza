package com.example.SosLocaliza.domains;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


 //  Entidade que representa um evento climático de emergência
 //  Mapeada para a tabela T_SOS_EVENTO no banco de dados
 
@With
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "T_SOS_EVENTO")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO")
    private Long idEvento;

    @Column(name = "NOME_EVENTO", nullable = false, length = 100)
    private String nomeEvento;

    @Column(name = "DESCRICAO_EVENTO", length = 500)
    private String descricaoEvento;

    @Column(name = "CAUSAS", length = 300)
    private String causas;

    @Column(name = "ALERTAS", length = 300)
    private String alertas;

    @Column(name = "ACOES_ANTES", length = 500)
    private String acoesAntes;

    @Column(name = "ACOES_DURANTE", length = 500)
    private String acoesDurante;

    @Column(name = "ACOES_DEPOIS", length = 500)
    private String acoesDepois;

    @Column(name = "DATA_CRIACAO")
    private LocalDateTime dataCriacao;

    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    @Column(name = "ATIVO")
    @Builder.Default
    private Boolean ativo = true;

    // Relacionamento com SMS enviados para este evento
    @OneToMany(
            mappedBy = "evento",
            fetch = FetchType.LAZY
    )
    private List<SmsMessage> smsEnviados;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
