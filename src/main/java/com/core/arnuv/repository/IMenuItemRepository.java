package com.core.arnuv.repository;

import com.core.arnuv.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface IMenuItemRepository extends JpaRepository<MenuItem, Long> {
    @Query("SELECT m FROM MenuItem m JOIN m.roles r WHERE r.nombre IN ?1 ORDER BY m.orden ASC")
    Set<MenuItem> findAllByRoles(Set<String> roleNames);
}
