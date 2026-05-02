package com.objetcol.collectobjet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lieux_depot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LieuDepot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TypeLieuDepot typeLieu = TypeLieuDepot.COMMISSARIAT;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 500)
    private String adresse;

    @Column(length = 120)
    private String ville;

    @Column(length = 40)
    private String telephone;

    /** Indications pratiques (horaires, vestiaire, etc.) */
    @Column(length = 1000)
    private String indication;

    @Column(nullable = false)
    @Builder.Default
    private boolean actif = true;

    /** Ordre d’affichage dans les listes */
    @Column(nullable = false)
    @Builder.Default
    private Integer ordreAffichage = 0;
}
