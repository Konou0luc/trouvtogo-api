package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.response.CommunauteStatsResponse;
import com.objetcol.collectobjet.dto.response.TimeSeriesPointResponse;
import com.objetcol.collectobjet.model.StatutObjet;
import com.objetcol.collectobjet.repository.ObjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ObjetRepository objetRepository;
    private final JdbcTemplate jdbcTemplate;

    public CommunauteStatsResponse getCommunauteStats() {
        long n = objetRepository.countDistinctProprietairesByStatut(StatutObjet.ACTIF);
        return CommunauteStatsResponse.builder().personnesActives(n).build();
    }

    public List<TimeSeriesPointResponse> getTimeSeries(int months) {
        String sql = "WITH months AS ( " +
                "SELECT generate_series( " +
                "date_trunc('month', current_date) - (?::int - 1) * interval '1 month', " +
                "date_trunc('month', current_date), " +
                "interval '1 month' " +
                ") as month_start " +
                ") " +
                "SELECT to_char(m.month_start, 'YYYY-MM') as key, " +
                "to_char(m.month_start, 'Mon') as name, " +
                "COALESCE(l.lost, 0) as lost, " +
                "COALESCE(f.found, 0) as found, " +
                "COALESCE(r.resolved, 0) as resolved " +
                "FROM months m " +
                "LEFT JOIN ( " +
                "  SELECT date_trunc('month', created_at) as month, count(*) as lost FROM objets WHERE type = 'PERDU' GROUP BY month " +
                ") l ON date_trunc('month', m.month_start) = l.month " +
                "LEFT JOIN ( " +
                "  SELECT date_trunc('month', created_at) as month, count(*) as found FROM objets WHERE type = 'TROUVE' GROUP BY month " +
                ") f ON date_trunc('month', m.month_start) = f.month " +
                "LEFT JOIN ( " +
                "  SELECT date_trunc('month', created_at) as month, count(*) as resolved FROM objets WHERE statut = 'RESOLU' GROUP BY month " +
                ") r ON date_trunc('month', m.month_start) = r.month " +
                "ORDER BY m.month_start;";

        return jdbcTemplate.query(sql, new Object[]{months}, (rs, rowNum) ->
                TimeSeriesPointResponse.builder()
                        .key(rs.getString("key"))
                        .name(rs.getString("name"))
                        .lost(rs.getLong("lost"))
                        .found(rs.getLong("found"))
                        .resolved(rs.getLong("resolved"))
                        .build()
        );
    }
}
