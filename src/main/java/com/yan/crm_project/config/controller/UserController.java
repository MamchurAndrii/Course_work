package com.yan.crm_project.config.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.util.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(USER_VIEW)
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;



    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Завантажити список користувачів
    @GetMapping("")
    public ModelAndView user() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    // Завантажити нову форму введення нового користувача
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(ADD_VIEW)
    public ModelAndView userAdd() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            mav.addObject(DEFAULT_ROLE_ID_PARAM, DEFAULT_ROLE);
            return mav;
        }
    }

    // Додати нового користувача
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String userAddSave(User user) {
        // Перевірте поточний рахунок ще дійсний
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // перевірка електронної пошти вже існує
            if (userService.getUser(user.getEmail()) != null) {
                //Цей обліковий запис електронної пошти вже зареєстровано!
                return REDIRECT_PREFIX + USER_VIEW + ADD_VIEW;
            } else {
                user.setImage(DEFAULT_AVATAR);
                userService.saveUser(user);
                //Обліковий запис успішно створено!
                return REDIRECT_PREFIX + USER_VIEW;
            }
        }
    }

    // Завантажити форму для редагування користувача
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(EDIT_VIEW)
    public ModelAndView userEdit(int id) {
        var account = authenticationUtil.getAccount();

        //перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var user = userService.getUser(id);
            // перевірити існування користувача
            if (user == null) {
                //Акаунт не існує!
                return new ModelAndView(REDIRECT_PREFIX + USER_VIEW);
            } else {
                var mav = new ModelAndView(USER_EDIT_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(USER_PARAM, user);
                mav.addObject(ROLES_PARAM, roleService.getRoles());
                return mav;
            }
        }
    }

    // Зберігти редагованого користувача
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(EDIT_VIEW + SAVE_VIEW)
    public String userEditSave(User user) {
        // перевірити поточний рахунок ще дійсний
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            //перевірити новий пароль
            if (hasText(user.getPassword())) {
                userService.saveUser(user);
            } else {
                userService.saveUserWithoutPassword(user);
            }
            //Обліковий запис успішно оновлено!
            return REDIRECT_PREFIX + USER_VIEW;
        }
    }

    // Видалити користувача
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String userDelete(int id) {
        // перевірити поточний рахунок ще дійсний
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var user = userService.getUser(id);
            // перевірити, чи існує користувач
            if (user == null) {
                //Акаунт не існує!
            } else {

                //перевірити відключення користувача
                if (user.getProjects().size() > 0) {
                    //У цьому обліковому записі є проект, його неможливо видалити!
                } else if (user.getTasks().size() > 0) {
                    //У цьому обліковому записі є робота, не можна видалити!
                } else {
                    userService.deleteUser(id);

                }
            }
            return REDIRECT_PREFIX + USER_VIEW;
        }
    }

    //Завантажити сторінку з інформацією про користувача
    @GetMapping(DETAILS_VIEW)
    public ModelAndView userDetails(int id) {
        var account = authenticationUtil.getAccount();
        //перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var user = userService.getUser(id);
            // перевірити існування користувача
            if (user == null) {
                //Акаунт не існує!
                return new ModelAndView(REDIRECT_PREFIX + USER_VIEW);
            } else {
                var mav = new ModelAndView(USER_DETAILS_TEMP);
                var tasks = user.getTasks();
                var tasksNotStarted = applicationUtil.splitTasksByStatus(tasks, NOT_STARTED);
                var tasksInProgress = applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS);
                var tasksCompleted = applicationUtil.splitTasksByStatus(tasks, COMPLETED);
                var tasksCount = tasks.size();
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(USER_PARAM, user);
                mav.addObject(TASKS_NOT_STARTED_PARAM, tasksNotStarted);
                mav.addObject(TASKS_IN_PROGRESS_PARAM, tasksInProgress);
                mav.addObject(TASKS_COMPLETED_PARAM, tasksCompleted);
                mav.addObject(NOT_STARTED_PERCENT_PARAM,
                        tasksCount == 0 ? 0 : tasksNotStarted.size() * 100 / tasksCount);
                mav.addObject(IN_PROGRESS_PERCENT_PARAM,
                        tasksCount == 0 ? 0 : tasksInProgress.size() * 100 / tasksCount);
                mav.addObject(COMPLETED_PERCENT_PARAM,
                        tasksCount == 0 ? 0 : tasksCompleted.size() * 100 / tasksCount);
                return mav;
            }
        }
    }
}
