package ru.andreyszdlv.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "t_tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @Column(name = "c_title", nullable = false)
    private String title;

    @Column(name = "c_description", length = 1000)
    private String description;

    @Column(name = "c_status", nullable = false, columnDefinition = "VARCHAR(20) CHECK (c_status IN ('WAITING', 'IN_PROGRESS', 'COMPLETED'))")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "c_priority", nullable = false, columnDefinition = "VARCHAR(20) CHECK (c_priority IN ('LOW', 'MEDIUM', 'HIGH'))")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Column(name = "c_created_at", nullable = false, columnDefinition = "TIMESTAMP(0)", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "c_author_id", nullable = false, updatable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "c_assignee_id")
    private User assignee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
