package api.kun.uz.repository;

import api.kun.uz.dto.FilterResultDTO;
import api.kun.uz.dto.ProfileFilterDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class CustomFilterRepository {
    @Autowired
    private EntityManager entityManager;


    public FilterResultDTO<Object[]> filter(ProfileFilterDTO profileFilterDTO, Integer page, Integer size) {
        StringBuilder dataQueryBuilder = new StringBuilder(
                "SELECT p.name, p.surname, p.username, p.status, p.createdDate, " +
                        "(SELECT string_agg(pr.roles, ',') FROM ProfileRoleEntity pr WHERE p.id = pr.profileId) AS profileRole " +
                        "FROM ProfileEntity p WHERE p.visible = true"
        );
        StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(p) FROM ProfileEntity p WHERE p.visible = true");
        HashMap<String, Object> params = new HashMap<>();

        if (profileFilterDTO.getName() != null && !profileFilterDTO.getName().isBlank()) {
            dataQueryBuilder.append(" AND LOWER(p.name) LIKE :name");
            countQueryBuilder.append(" AND LOWER(p.name) LIKE :name");
            params.put("name", "%" + profileFilterDTO.getName().toLowerCase() + "%");
        }
        if (profileFilterDTO.getSurname() != null && !profileFilterDTO.getSurname().isBlank()) {
            dataQueryBuilder.append(" AND LOWER(p.surname) LIKE :surname");
            countQueryBuilder.append(" AND LOWER(p.surname) LIKE :surname");
            params.put("surname", "%" + profileFilterDTO.getSurname().toLowerCase() + "%");
        }
        if (profileFilterDTO.getUsername() != null && !profileFilterDTO.getUsername().isBlank()) {
            dataQueryBuilder.append(" AND LOWER(p.username) LIKE :username");
            countQueryBuilder.append(" AND LOWER(p.username) LIKE :username");
            params.put("username", "%" + profileFilterDTO.getUsername().toLowerCase() + "%");
        }
        if (profileFilterDTO.getCreatedDateFrom() != null) {
            dataQueryBuilder.append(" AND p.createdDate >= :createdDateFrom");
            countQueryBuilder.append(" AND p.createdDate >= :createdDateFrom");
            params.put("createdDateFrom", profileFilterDTO.getCreatedDateFrom());
        }
        if (profileFilterDTO.getCreatedDateTo() != null) {
            dataQueryBuilder.append(" AND p.createdDate <= :createdDateTo");
            countQueryBuilder.append(" AND p.createdDate <= :createdDateTo");
            params.put("createdDateTo", profileFilterDTO.getCreatedDateTo());
        }

        dataQueryBuilder.append(" ORDER BY p.createdDate DESC");

        // Build queries
        Query dataQuery = entityManager.createQuery(dataQueryBuilder.toString());
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());

        // Bind parameters
        params.forEach((key, value) -> {
            dataQuery.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        //Pagination
        dataQuery.setFirstResult(page * size);
        dataQuery.setMaxResults(size);

        List<Object[]> results = dataQuery.getResultList();
        Long total = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<>(results, total);

    }
}
