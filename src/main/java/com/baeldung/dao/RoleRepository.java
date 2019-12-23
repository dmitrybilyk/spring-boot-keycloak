package com.baeldung.dao;

import com.baeldung.keycloak.ScorecardRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<ScorecardRole, Integer> {
}
