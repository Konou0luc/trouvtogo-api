package com.objetcol.collectobjet.repository;

import com.objetcol.collectobjet.model.LieuDepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LieuDepotRepository extends JpaRepository<LieuDepot, Long> {

    List<LieuDepot> findByActifTrueOrderByOrdreAffichageAscNomAsc();

    boolean existsByNomAndIdNot(String nom, Long id);

    boolean existsByNom(String nom);
}
