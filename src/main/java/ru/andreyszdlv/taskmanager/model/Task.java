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

    @Column(name = "c_description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_status", nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_priority", nullable = false)
    private TaskPriority priority;

    @Column(name = "c_created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "c_author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "c_assignee_id")
    private User assignee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
