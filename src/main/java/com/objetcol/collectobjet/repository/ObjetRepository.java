package com.objetcol.collectobjet.repository;

import com.objetcol.collectobjet.model.Objet;
import com.objetcol.collectobjet.model.StatutObjet;
import com.objetcol.collectobjet.model.TypeObjet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjetRepository extends JpaRepository<Objet, Long> {

    Page<Objet> findByStatut(StatutObjet statut, Pageable pageable);

    Page<Objet> findByType(TypeObjet type, Pageable pageable);

    Page<Objet> findByTypeAndStatut(TypeObjet type, StatutObjet statut, Pageable pageable);

    List<Objet> findByProprietaireId(Long proprietaireId);

    Page<Objet> findByCategorieId(Long categorieId, Pageable pageable);

    /**
     * Liste filtrée sans recherche texte — évite tout {@code lower(titre)} sur des colonnes encore BYTEA
     * (PostgreSQL n’accepte pas {@code lower(bytea)} ; l’accueil appelle souvent sans mot-clé).
     */
    @Query("SELECT o FROM Objet o WHERE " +
            "(:type IS NULL OR o.type = :type) " +
            "AND (:statut IS NULL OR o.statut = :statut) " +
            "AND (:categorieId IS NULL OR o.categorie.id = :categorieId)")
    Page<Objet> rechercherSansMotCle(
            @Param("type") TypeObjet type,
            @Param("statut") StatutObjet statut,
            @Param("categorieId") Long categorieId,
            Pageable pageable
    );

    /**
     * Recherche plein texte : colonnes {@code titre}/{@code description} encore stockées en BYTEA côté PG
     * → {@code convert_from(..., 'UTF8')} + {@code ILIKE}. Après migration varchar/text, préférer une JPQL classique.
     */
    @Query(
            value = """
                    SELECT o.* FROM objets o WHERE
                    (
                      convert_from(o.titre, 'UTF8') ILIKE CONCAT('%', btrim(CAST(:keyword AS text)), '%')
                      OR (
                        o.description IS NOT NULL
                        AND convert_from(o.description, 'UTF8') ILIKE CONCAT('%', btrim(CAST(:keyword AS text)), '%')
                      )
                    )
                    AND (CAST(:typeFilter AS text) IS NULL OR o.type = CAST(:typeFilter AS text))
                    AND (CAST(:statutFilter AS text) IS NULL OR o.statut = CAST(:statutFilter AS text))
                    AND (:categorieId IS NULL OR o.categorie_id = :categorieId)
                    """,
            countQuery = """
                    SELECT count(o.id) FROM objets o WHERE
                    (
                      convert_from(o.titre, 'UTF8') ILIKE CONCAT('%', btrim(CAST(:keyword AS text)), '%')
                      OR (
                        o.description IS NOT NULL
                        AND convert_from(o.description, 'UTF8') ILIKE CONCAT('%', btrim(CAST(:keyword AS text)), '%')
                      )
                    )
                    AND (CAST(:typeFilter AS text) IS NULL OR o.type = CAST(:typeFilter AS text))
                    AND (CAST(:statutFilter AS text) IS NULL OR o.statut = CAST(:statutFilter AS text))
                    AND (:categorieId IS NULL OR o.categorie_id = :categorieId)
                    """,
            nativeQuery = true)
    Page<Objet> rechercherAvecMotCle(
            @Param("keyword") String keyword,
            @Param("typeFilter") TypeObjet type,
            @Param("statutFilter") StatutObjet statut,
            @Param("categorieId") Long categorieId,
            Pageable pageable
    );

    @Query("SELECT COUNT(DISTINCT o.proprietaire.id) FROM Objet o WHERE o.statut = :statut")
    long countDistinctProprietairesByStatut(@Param("statut") StatutObjet statut);
}