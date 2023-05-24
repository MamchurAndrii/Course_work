package com.yan.crm_project.config.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(ROLE_VIEW)
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Завантажити список ролей
    @GetMapping("")
    public ModelAndView role() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            return mav;
        }
    }

    // Завантажити форму введення нової ролі
    @GetMapping(ADD_VIEW)
    public ModelAndView roleAdd() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    // Додайте нові ролі
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String roleAddSave(Role role) {
        // Перевірте поточний рахунок ще дійсний
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            //ім'я перевірки вже існує
            if (roleService.getRole(role.getName()) != null) {
               //Це ім'я вже існує
                return REDIRECT_PREFIX + ROLE_VIEW + ADD_VIEW;
            } else {
                roleService.saveRole(role);
                //Додати успіх
                return REDIRECT_PREFIX + ROLE_VIEW;
            }
        }
    }

    // Завантажити форму редагування ролі
    @GetMapping(EDIT_VIEW)
    public ModelAndView roleEdit(int id) {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var role = roleService.getRole(id);
            // перевірте, чи існує роль
            if (role == null) {
                //Дозвіл не існує
                return new ModelAndView(REDIRECT_PREFIX + ROLE_VIEW);
            } else {
                var mav = new ModelAndView(ROLE_EDIT_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(ROLE_PARAM, role);
                return mav;
            }
        }
    }

    // Редагувати ролі
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String roleEditSave(Role role) {
        // перевірити поточний рахунок ще дійсний
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            roleService.saveRole(role);
            //Оновлення дозволу успішне
            return REDIRECT_PREFIX + ROLE_VIEW;
        }
    }

    //Видалити роль
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String roleDelete(int id) {
        // перевірити поточний рахунок ще дійсний
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var role = roleService.getRole(id);
            // перевірте наявність ролі
            if (role == null) {
                //Дозвіл не існує
            } else {
                // перевірте відключення ролі
                if (role.getUsers().size() > 0) {
                    //Цей дозвіл використовується, його неможливо видалити
                } else {
                    roleService.deleteRole(id);
                    //Вилучено дозвіл
                }
            }
            return REDIRECT_PREFIX + ROLE_VIEW;
        }
    }
}
