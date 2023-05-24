package com.yan.crm_project.model;

import java.util.*;

import javax.persistence.*;

import lombok.*;

import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static java.util.stream.Collectors.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
@Entity(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "email")
    private String email;

    @NonNull
    @Column(name = "password")
    private String password;

    @NonNull
    @Column(name = "pib_employee")
    private String name;

    @NonNull
    @Column(name = "photograghs")
    private String image;

    @Column(name = "plase_of_residence")
    private String address;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "id_tupe_of_profession")
    private int roleId;

    @JoinColumn(name = "id_tupe_of_profession", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY)
    private Role role;

    @OneToMany(mappedBy = "originator")
    private List<Project> projects;

    @OneToMany(mappedBy = "doer")
    private List<Task> tasks;

    // Отримати всі завдання зі статусом "Не розпочато".
    public List<Task> getTasksNotStarted() {
        return tasks.stream().filter(task -> task.getStatus().getId() == NOT_STARTED).collect(toList());
    }

    // Отримайте всі завдання зі статусом виконання
    public List<Task> getTasksInProgress() {
        return tasks.stream().filter(task -> task.getStatus().getId() == IN_PROGRESS).collect(toList());
    }

    // Отримати всі завдання зі статусом виконано
    public List<Task> getTasksCompleted() {
        return tasks.stream().filter(task -> task.getStatus().getId() == COMPLETED).collect(toList());
    }
}