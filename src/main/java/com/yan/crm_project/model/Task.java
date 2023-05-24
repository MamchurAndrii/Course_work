package com.yan.crm_project.model;

import java.util.*;

import javax.persistence.*;

import org.springframework.format.annotation.*;

import lombok.*;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
@Entity(name = "work")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "name_project")
    private String name;

    @Column(name = "description")
    private String description;

    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_data_work")
    private Date startDate;

    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_data_work")
    private Date endDate;

    @Column(name = "id_employee_who_will_perform")
    private int doerId;

    @Column(name = "id_project")
    private int projectId;

    @Column(name = "id_profession_employee")
    private int statusId;

    @JoinColumn(name = "id_employee_who_will_perform", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY)
    private User doer;

    @JoinColumn(name = "id_project", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY)
    private Project project;

    @JoinColumn(name = "id_profession_employee", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY)
    private TaskStatus status;
}