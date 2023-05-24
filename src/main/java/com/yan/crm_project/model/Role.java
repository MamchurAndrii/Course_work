package com.yan.crm_project.model;

import java.util.*;

import javax.persistence.*;

import lombok.*;

import static javax.persistence.GenerationType.*;
@Entity(name = "tupes_of_professions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "name_ang")
    private String name;

    @Column(name = "name")
    private String description;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}