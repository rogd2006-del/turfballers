package com.turfballers.backend.repository;

import com.turfballers.backend.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Member repository with search and filtering support.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    long countByStatus(Member.MemberStatus status);

    /** Search members by name/email/phone, optionally filtered by status */
    @Query("""
        SELECT m FROM Member m
        WHERE (:keyword IS NULL OR :keyword = '' OR
               LOWER(m.fullName)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(m.email)     LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               m.phone            LIKE CONCAT('%', :keyword, '%'))
          AND (:status IS NULL OR m.status = :status)
        """)
    Page<Member> searchMembers(
        @Param("keyword") String keyword,
        @Param("status")  Member.MemberStatus status,
        Pageable pageable
    );
}
