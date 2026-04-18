package com.objetcol.collectobjet.repository;

import com.objetcol.collectobjet.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndLueFalse(Long userId);

    List<Notification> findByUserIdAndLueFalse(Long userId);
}