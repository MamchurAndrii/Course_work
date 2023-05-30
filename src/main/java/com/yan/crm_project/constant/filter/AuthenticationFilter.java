package com.yan.crm_project.constant.filter;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;

import com.fasterxml.jackson.databind.*;

import lombok.*;
import lombok.extern.slf4j.*;

import static com.auth0.jwt.JWT.*;
import static com.auth0.jwt.algorithms.Algorithm.*;
import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.lang.System.*;
import static java.util.stream.Collectors.*;
import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    // поле
    private final AuthenticationManager authenticationManager;

}
