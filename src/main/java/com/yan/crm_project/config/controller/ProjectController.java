package com.yan.crm_project.config.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.ApplicationConstant.Role.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(PROJECT_VIEW)
public class ProjectController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Завантажити список проектів
    @GetMapping("")
    public ModelAndView project() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(PROJECTS_PARAM, projectService.getProjects());
            return mav;
        }
    }


    //Завантажити нову форму введення проекту
    @GetMapping(ADD_VIEW)
    public ModelAndView projectAdd() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    // Додати новий проект
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String projectAddSave(Project project) {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            project.setOriginatorId(account.getId());
            projectService.saveProject(project);
            //Побільше успішних проектів!
            return REDIRECT_PREFIX + PROJECT_VIEW;
        }
    }

    // Завантажити форму редагування проекту
    @GetMapping(EDIT_VIEW)
    public ModelAndView projectEdit(int id) {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var project = projectService.getProject(id);
            // перевірте, чи існує проект
            if (project == null) {
                //Проект не існує!
                return new ModelAndView(REDIRECT_PREFIX + PROJECT_VIEW);
            } else {
                // перевірити дозвіл
                if (!isPermissionLeader(account, project.getOriginatorId())) {
                    return new ModelAndView(FORWARD_PREFIX + FORBIDDEN_VIEW);
                } else {
                    var mav = new ModelAndView(PROJECT_EDIT_TEMP);
                    mav.addObject(ACCOUNT_PARAM, account);
                    mav.addObject(PROJECT_PARAM, project);
                    return mav;
                }
            }
        }
    }

    // Редагувати проект
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String projectEditSave(Project project) {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // перевірити дозвіл
            if (!isPermissionLeader(account, project.getOriginatorId())) {
                return FORWARD_PREFIX + FORBIDDEN_VIEW;
            } else {
                projectService.saveProject(project);
               //Оновлення вакансії успішно!
                return REDIRECT_PREFIX + PROJECT_VIEW;
            }
        }
    }

    // Видалити проект
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String projectDelete(int id) {
        var account = authenticationUtil.getAccount();

        //перевірити поточний рахунок ще дійсний
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var project = projectService.getProject(id);
            // перевірте, чи існує проект
            if (project == null) {
                //Проект не існує!
                return REDIRECT_PREFIX + PROJECT_VIEW;
            } else {

                //перевірити відключення проекту
                if (project.getTasks().size() > 0) {
                    //Проект має роботу, не можна видалити!
                    return REDIRECT_PREFIX + PROJECT_VIEW;
                } else {
                    // перевірити дозвіл
                    if (!isPermissionLeader(account, project.getOriginatorId())) {
                        return FORWARD_PREFIX + FORBIDDEN_VIEW;
                    } else {
                        projectService.deleteProject(id);
                        //Проект успішно видалено!
                        return REDIRECT_PREFIX + PROJECT_VIEW;
                    }
                }
            }
        }
    }

    // Отримати роль поточного рахунку
    private String getCurrentAccountRole(int roleId) {
        return roleService.getRole(roleId).getName().toUpperCase();
    }

    //Перевірте лідера дозволів для проекту
    private boolean isPermissionLeader(User user, int originatorId) {
        var currentAccountRole = getCurrentAccountRole(user.getRoleId());
        return currentAccountRole.equals(LEADER) && userService.getUser(originatorId).getId() == user.getId()
                || currentAccountRole.equals(ADMIN);
    }
}
