package dev.morgadorafa.certification_nlw.modules.students.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.morgadorafa.certification_nlw.modules.students.entities.CertificationStudentEntity;

public interface CertificationStudentRepository extends JpaRepository<CertificationStudentEntity, UUID>{
    
    @Query("SELECT c FROM certifications c INNER JOIN c.studentEntity std WHERE std.email = :email and c.technology = :technology")
    List<CertificationStudentEntity> findByStudentEmailAndTechnology(String email, String technology);

    @Query("SELECT c FROM certifications c ORDER BY c.grate DESC LIMIT 10")
    List<CertificationStudentEntity> findTop10ByOrderByGrateDesc();
}
