package com.service2.repository;

import com.service2.model.Citation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CitationRepository extends JpaRepository<Citation, Long> {
    @Query(value = "SELECT * FROM citation ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Citation findRandomCitation();
}
