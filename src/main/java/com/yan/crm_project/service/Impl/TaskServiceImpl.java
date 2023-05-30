package com.yan.crm_project.service.Impl;

import java.util.*;

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
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    //Отримати всі завдання які є в базі даних
    @Override
    public List<Task> getTasks() {
        log.info("Отримання всіх завдань");
        return taskRepository.findAll();
    }

    @Override
    public Task getTask(int id) {
        log.info("Отримання завдання з ідентифікатором: {}", id);
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task saveTask(Task task) {

        log.info("Збереження завдання з назвою: {}", task.getName());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(int id) {
        log.info("Видалення завдання з id: {}", id);
        taskRepository.deleteById(id);
    }
}
