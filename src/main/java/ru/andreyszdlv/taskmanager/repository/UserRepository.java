package ru.andreyszdlv.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreyszdlv.taskmanager.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
