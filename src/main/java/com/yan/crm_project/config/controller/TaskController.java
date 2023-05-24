package com.yan.crm_project.config.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.ApplicationConstant.Role.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(TASK_VIEW)
public class TaskController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Завантажити список завдань
    @GetMapping("")
    public ModelAndView task() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(TASKS_PARAM, taskService.getTasks());
            return mav;
        }
    }

    // Завантажити форму введення нового завдання
    @GetMapping(ADD_VIEW)
    public ModelAndView taskAdd() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_ADD_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(ACCOUNT_PARAM, account);
            // перевірити адмін
            if (getCurrentAccountRole(account.getRoleId()).equals(ADMIN)) {
                mav.addObject(PROJECTS_PARAM, projectService.getProjects());
            } else {
                mav.addObject(PROJECTS_PARAM, account.getProjects());
            }
            return mav;
        }
    }

    // Додати нове завдання
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String taskAddSave(Task task) {
        // перевірити поточний рахунок ще дійсний
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            task.setStatusId(DEFAULT_STATUS);
            taskService.saveTask(task);
            //Побільше успішної роботи!
            return REDIRECT_PREFIX + TASK_VIEW;
        }
    }

    // Завантажити форму введення завдання редагування
    @GetMapping(EDIT_VIEW)
    public ModelAndView taskEdit(int id) {
        var account = authenticationUtil.getAccount();
        //перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var task = taskService.getTask(id);
            //перевірте, чи існує завдання
            if (task == null) {
                //Робота не знайдена!
                return new ModelAndView(REDIRECT_PREFIX + TASK_VIEW);
            } else {
                // перевірити дозвіл
                if (!isPermissionLeader(account, task.getProject().getOriginatorId())) {
                    return new ModelAndView(FORWARD_PREFIX + FORBIDDEN_VIEW);
                } else {
                    var mav = new ModelAndView(TASK_EDIT_TEMP);
                    mav.addObject(USERS_PARAM, userService.getUsers());
                    mav.addObject(ACCOUNT_PARAM, account);
                    mav.addObject(PROJECTS_PARAM, projectService.getProjects());
                    mav.addObject(TASK_PARAM, task);
                    return mav;
                }
            }
        }
    }

    // Редагувати завдання
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String taskEditSave(Task task, int originatorId) {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // перевірити дозвіл
            if (!isPermissionLeader(account, originatorId)) {
                return FORWARD_PREFIX + FORBIDDEN_VIEW;
            } else {
                taskService.saveTask(task);
                //Оновлення вакансії успішно!
                return REDIRECT_PREFIX + TASK_VIEW;
            }
        }
    }

    //Видалити завдання
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String taskDelete(int id) {
        var account = authenticationUtil.getAccount();
        //перевірити поточний рахунок ще дійсний
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var task = taskService.getTask(id);
            // перевірити наявність завдання
            if (task == null) {
                //Роботи не існує!
                return REDIRECT_PREFIX + TASK_VIEW;
            } else {
                // перевірити дозвіл
                if (!isPermissionLeader(account, task.getProject().getOriginatorId())) {
                    return FORWARD_PREFIX + FORBIDDEN_VIEW;
                } else {
                    taskService.deleteTask(id);
                    //Успішно видалено роботу!
                    return REDIRECT_PREFIX + TASK_VIEW;
                }
            }
        }
    }


    //Отримати роль поточного рахунку
    private String getCurrentAccountRole(int roleId) {
        return roleService.getRole(roleId).getName().toUpperCase();
    }

    // Перевірте дозвіл лідера на завдання
    private boolean isPermissionLeader(User user, int originatorId) {
        var currentAccountRole = getCurrentAccountRole(user.getRoleId());
        return currentAccountRole.equals(LEADER) && userService.getUser(originatorId).getId() == user.getId()
                || currentAccountRole.equals(ADMIN);
    }
}
