package com.objetcol.collectobjet.repository;

import com.objetcol.collectobjet.model.Signalement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignalementRepository extends JpaRepository<Signalement, Long> {
    List<Signalement> findByObjetId(Long objetId);
    List<Signalement> findByResolvedFalse();
}
