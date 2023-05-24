package com.yan.crm_project.service.Impl;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.repository.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.extern.slf4j.*;

@Service
@Transactional
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;



    @Override
    public Iterable<Project> getProjects() {
        log.info("Отримання всіх проектів");
        return projectRepository.findAll();
    }

    @Override
    public Project getProject(int id) {
        log.info("Отримання проекту з ідентифікатором: {}", id);
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public Project saveProject(Project project) {

        log.info("Збереження проекту з назвою: {}", project.getName());
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(int id) {
        log.info("Видалення проекту з id: {}", id);
        projectRepository.deleteById(id);
    }
}
