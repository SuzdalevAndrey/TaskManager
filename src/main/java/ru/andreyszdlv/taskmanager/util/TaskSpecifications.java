package ru.andreyszdlv.taskmanager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.model.Task;

@Slf4j
public class TaskSpecifications {

    public static Specification<Task> hasAuthor(Long authorId) {
        log.info("Creating specification for author with id: {}", authorId);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Task> hasAssignee(Long assigneeId) {
        log.info("Creating specification for assignee with id: {}", assigneeId);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        log.info("Creating specification for status: {}", status);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        log.info("Creating specification for priority: {}", priority);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("priority"), priority);
    }
}
