package com.objetcol.collectobjet.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "objets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Objet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150, columnDefinition = "varchar(150)")
    private String titre;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeObjet type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutObjet statut = StatutObjet.ACTIF;

    @Column(length = 255)
    private String localisation;

    /** Latitude WGS84 ; renseignée avec {@link #longitude} pour un point précis (ex. annonce « trouvé »). */
    private Double latitude;

    /** Longitude WGS84 */
    private Double longitude;

    private LocalDateTime dateEvenement;

    /** Renseigné uniquement pour les annonces {@link TypeObjet#TROUVE} */
    @Enumerated(EnumType.STRING)
    @Column(name = "conservation_trouve")
    private ConservationTrouvaille conservationTrouve;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lieu_depot_id")
    private LieuDepot lieuDepot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private User proprietaire;

    @OneToMany(mappedBy = "objet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}