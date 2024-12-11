package ru.andreyszdlv.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @Column(name = "c_content", nullable = false, length = 1000)
    private String content;

    @Column(name = "c_created_at", nullable = false, columnDefinition = "TIMESTAMP(0)", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "c_task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "c_author_id", nullable = false)
    private User author;
}
