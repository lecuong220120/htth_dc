package com.example.demo.Repository;

import com.example.demo.NsoObj.PlayerNSO;
import com.example.demo.Obj.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerNsoRepository extends JpaRepository<PlayerNSO, Long> {
    Optional<PlayerNSO> findByName(String name);
    Optional<PlayerNSO> findByNameAndTimeAndSelection(String name, String time, Integer selection);
}
