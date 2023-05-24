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
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;



    @Override
    public Iterable<Role> getRoles() {
        log.info("Отримання всіх ролей");
        return roleRepository.findAll();
    }

    @Override
    public Role getRole(int id) {
        log.info("Отримання ролі з ідентифікатором {}", id);
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role getRole(String name) {

        log.info("Отримання ролі з іменем: {}", name);
        return roleRepository.findByName(name);
    }

    @Override
    public Role saveRole(Role role) {

        log.info("Збереження ролі з іменем: {}", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(int id) {
        log.info("Видалення ролі з id: {}", id);
        roleRepository.deleteById(id);
    }
}
