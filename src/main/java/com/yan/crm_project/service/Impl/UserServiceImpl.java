package com.yan.crm_project.service.Impl;

import java.util.*;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.User;
import com.yan.crm_project.repository.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.extern.slf4j.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.util.Collections.*;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username);
        //перевірити існування користувача
        if (user == null) {
            log.error("Користувач не знайдений");
            throw new UsernameNotFoundException("Користувач не знайдений");
        } else {
            log.info("Користувача знайдено: {}", username);
            return new org.springframework.security.core.userdetails.User(user.getEmail(), passwordEncoder.encode(user.getPassword()),
                    singleton(new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole().getName().toUpperCase())));
        }
    }

    //Отримати всіх працівників
    @Override
    public Iterable<User> getUsers() {
        log.info("Отримання всіх користувачів");
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsers(String name) {
        log.info("Отримання всіх користувачів за іменами: {}", name);
        return userRepository.findAllByName(name);
    }

    @Override
    public User getUser(int id) {
        log.info("Отримання користувача з ідентифікатором {}", id);
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUser(String email) {

        log.info("\n" +
                "Отримання користувача електронною поштою: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {

        log.info("Збереження користувачів електронною поштою: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User saveUserWithoutPassword(User user) {
        user.setPassword(getUser(user.getId()).getPassword());

        log.info("Збереження користувачів електронною поштою: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(int id) {
        log.info("Видалення користувача з id: {}", id);
        userRepository.deleteById(id);
    }
}
