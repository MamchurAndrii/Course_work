package com.yan.crm_project.config.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.*;

import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.util.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(PROFILE_VIEW)
public class ProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Завантажити сторінку профілю
    @GetMapping("")
    public ModelAndView profile() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_TEMP);
            var tasks = account.getTasks();
            var tasksCount = tasks.size();
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, NOT_STARTED).size() * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS).size() * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, COMPLETED).size() * 100 / tasksCount);
            return mav;
        }
    }

    // Завантажте новий аватар
    @PostMapping(EDIT_VIEW + AVATAR_VIEW)
    public String profileAvatarEdit(MultipartFile avatar) {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            if (avatar.isEmpty()) {
                //Невідомий файл!
            } else {
                fileUploadService.init();
                var file = fileUploadService.upload(avatar);
                // перевірте файл зображення
                if (file == null) {
                    //Недійсний файл!
                } else {
                    var defaultAvatarName = account.getId() + ".jpg";
                    // спробуйте змінити аватар
                    if (!imageService.resizeImage(file, defaultAvatarName)) {
                        //Не вдалося оновити аватар!
                    } else {
                        account.setImage(defaultAvatarName);
                        userService.saveUserWithoutPassword(account);
                        fileUploadService.remove(file.getName());
                       //Оновлення фотографії профілю виконано!
                    }
                }
            }
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }


    //Завантажити форму введення редагованої інформації
    @GetMapping(EDIT_VIEW + INFO_VIEW)
    public ModelAndView profileInfoEdit() {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_INFO_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    // Редагувати користувача
    @RequestMapping(value = EDIT_VIEW + INFO_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String profileInfoEditSave(User user) {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // перевірити новий пароль
            if (hasText(user.getPassword())) {
                userService.saveUser(user);
            } else {
                userService.saveUserWithoutPassword(user);
            }
            //Успішно оновлено!
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }


    //Завантажити форму введення завдання редагування
    @GetMapping(EDIT_VIEW + TASK_VIEW)
    public ModelAndView profileTaskEdit(int id) {
        var account = authenticationUtil.getAccount();
        // перевірити поточний рахунок ще дійсний
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var task = taskService.getTask(id);
            // перевірте, чи існує завдання
            if (task == null) {
                //Роботи не існує!
                return new ModelAndView(REDIRECT_PREFIX + PROFILE_VIEW);
            } else {
                var mav = new ModelAndView(PROFILE_TASK_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(TASK_PARAM, task);
                mav.addObject(TASKSTATUSES_PARAM, taskStatusService.getTaskStatuses());
                return mav;
            }
        }
    }

    // Редагувати завдання
    @RequestMapping(value = EDIT_VIEW + TASK_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String profileTaskEditSave(Task task) {
        // перевірити поточний рахунок ще дійсний
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            taskService.saveTask(task);
            //Статус вакансії оновлено!
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }
}
