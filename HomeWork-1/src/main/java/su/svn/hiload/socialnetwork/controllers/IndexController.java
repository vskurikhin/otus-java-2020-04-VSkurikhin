package su.svn.hiload.socialnetwork.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.exceptions.NotFound;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.model.security.UserProfile;
import su.svn.hiload.socialnetwork.view.ApplicationForm;
import su.svn.hiload.socialnetwork.view.RegistrationForm;

import java.net.URI;

@Controller
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private BCryptPasswordEncoder encoder;

    private final su.svn.hiload.socialnetwork.dao.jdbc.UserInfoDao userInfoJdbcDao;

    private final su.svn.hiload.socialnetwork.dao.r2dbc.UserInfoDao userInfoR2dbcDao;

    private final su.svn.hiload.socialnetwork.dao.jdbc.UserProfileDao userProfileJdbcDao;

    private final su.svn.hiload.socialnetwork.dao.r2dbc.UserProfileDao userProfileR2dbcDao;

    public IndexController(
            @Value("${application.security.strength}") int strength,
            @Qualifier("userInfoJdbcDao") su.svn.hiload.socialnetwork.dao.jdbc.UserInfoDao userInfoJdbcDao,
            @Qualifier("userInfoR2dbcDao") su.svn.hiload.socialnetwork.dao.r2dbc.UserInfoDao userInfoR2dbcDao,
            @Qualifier("userProfileJdbcDao") su.svn.hiload.socialnetwork.dao.jdbc.UserProfileDao userProfileJdbcDao,
            @Qualifier("userProfileR2dbcDao") su.svn.hiload.socialnetwork.dao.r2dbc.UserProfileDao userProfileR2dbcDao) {
        this.encoder = new BCryptPasswordEncoder(strength);
        this.userInfoJdbcDao = userInfoJdbcDao;
        this.userInfoR2dbcDao = userInfoR2dbcDao;
        this.userProfileJdbcDao = userProfileJdbcDao;
        this.userProfileR2dbcDao = userProfileR2dbcDao;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Mono<Void> userRegistration(RegistrationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
             response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
             response.getHeaders().setLocation(URI.create("/error?code=10"));

             return response.setComplete();
        }

        String hash = encoder.encode(form.getPassword());
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(form.getUsername());
        userProfile.setHash(hash);
        userProfile.setLocked(false);
        userProfile.setExpired(false);

        int count = userProfileJdbcDao.create(userProfile);
        if (count > 0) {
            response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
            response.getHeaders().setLocation(URI.create("/user/application"));

             return response.setComplete();
        }

        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(URI.create("/error?code=20"));

        return response.setComplete();
    }

    @RequestMapping("/user")
    public Mono<Void> userIndex(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/user/application"));

        return response.setComplete();
    }

    @GetMapping("/user/application")
    public String userIndexApplication(@AuthenticationPrincipal UserDetails user, final Model model) {
        final ApplicationForm form = new ApplicationForm();
        form.setUsername(user.getUsername());
        userProfileJdbcDao.readIdByLogin(user.getUsername())
                .ifPresentOrElse(id -> fillApplicationForm(form, id), NotFound::is);
        model.addAttribute("form", form);
        model.addAttribute("username", user.getUsername());

        return "user/application";
    }

    private void fillApplicationForm(ApplicationForm form, long id) {
        userInfoJdbcDao.readById(id).ifPresentOrElse(userInfo -> {
            form.setFirstName(userInfo.getFirstName());
            form.setSurName(userInfo.getSurName());
            form.setAge(userInfo.getAge());
            form.setSex(userInfo.getSex());
            form.setCity(userInfo.getCity());
        }, NotFound::is);
    }

    @PostMapping("/user/application")
    public Mono<Void> userApplication(ApplicationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
            response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
            response.getHeaders().setLocation(URI.create("/error?code=30"));

            return response.setComplete();
        }
        userProfileJdbcDao.readLogin(form.getUsername())
                .ifPresentOrElse(userProfile -> userApplication(form, userProfile), NotFound::is);
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/user/friends"));

        return response.setComplete();
    }

    private void userApplication(ApplicationForm form, UserProfile userProfile) {

        UserInfo userInfo = new UserInfo();
        userInfo.setId(userProfile.getId());
        userInfo.setFirstName(form.getFirstName());
        userInfo.setSurName(form.getSurName());
        userInfo.setAge(form.getAge());
        userInfo.setSex(form.getSex());
        userInfo.setCity(form.getCity());

        if (userInfoJdbcDao.existsById(userProfile.getId())) {
            userInfoJdbcDao.update(userInfo);
        } else {
            userInfoJdbcDao.create(userInfo);
        }
    }

    @RequestMapping("/user/friends")
    public String userIndexFriends(final Model model) {
        model.addAttribute("users", getUsers());

        return "user/friends/index";
    }

    private IReactiveDataDriverContextVariable getUsers() {
        return new ReactiveDataDriverContextVariable(userProfileR2dbcDao.readAll(), 1);
    }

    @GetMapping("/error")
    public String error(@RequestParam("code") int code, final Model model) {
        String errorMessage = "Error message by code: " + code;
        model.addAttribute("errorMessage", errorMessage);

        return "error";
    }
}
