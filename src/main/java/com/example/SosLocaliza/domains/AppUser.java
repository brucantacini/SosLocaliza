package com.example.SosLocaliza.domains;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SOS_APP_USER")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USER")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(name = "NOME_EXIBICAO", length = 100)
    private String nomeExibicao;

    @Column(name = "DDD", length = 2)
    private String ddd;

    @Column(name = "NUMERO_LOCAL", length = 9)
    private String numeroLocal;

    @Column(name = "LOCALIZACAO", length = 200)
    private String localizacao;
}
