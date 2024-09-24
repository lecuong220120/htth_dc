package com.example.demo.Repository;

import com.example.demo.Obj.Player;
import com.example.demo.Obj.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDb, Long> {
    Optional<UserDb> findByName(String name);
}
