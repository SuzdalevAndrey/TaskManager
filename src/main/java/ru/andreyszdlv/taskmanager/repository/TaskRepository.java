package ru.andreyszdlv.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreyszdlv.taskmanager.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
