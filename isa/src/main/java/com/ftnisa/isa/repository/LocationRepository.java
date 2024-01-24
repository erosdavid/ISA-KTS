package com.ftnisa.isa.repository;

import com.ftnisa.isa.model.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findById(Integer id);

}
