package com.yan.crm_project.util;

import java.util.*;

import org.springframework.stereotype.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.util.stream.Collectors.*;

@Component
public class ApplicationUtil {
    // Отримати всі завдання зі статусом
    public List<Task> splitTasksByStatus(List<Task> tasks, int statusId) {
        return tasks.stream().filter(task -> task.getStatus().getId() == statusId).collect(toList());
    }
}
