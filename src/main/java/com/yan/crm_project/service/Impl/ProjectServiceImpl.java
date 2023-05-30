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

    //Отримати всі можливі проекти в базі даних
    @Override
    public Iterable<Project> getProjects() {
        log.info("Отримання всіх проектів");
        return projectRepository.findAll();
    }


}
