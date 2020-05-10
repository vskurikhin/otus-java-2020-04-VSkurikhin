package su.svn.hiload.socialnetwork.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.services.InterestsCollectorToForm;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.model.security.UserProfile;
import su.svn.hiload.socialnetwork.services.OldSchoolBlockingService;
import su.svn.hiload.socialnetwork.services.ReactiveService;
import su.svn.hiload.socialnetwork.view.ApplicationForm;
import su.svn.hiload.socialnetwork.view.RegistrationForm;

import java.net.URI;

@Controller
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private final ReactiveService reactiveService;

    // private final OldSchoolBlockingService blockingService;

    public IndexController(ReactiveService reactiveService, OldSchoolBlockingService blockingService) {
        // this.blockingService = blockingService;
        this.reactiveService = reactiveService;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Mono<Void> userRegistration(RegistrationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
            return createTemporaryRedirect(response, "/error?code=10");
        }
        return reactiveService.createUserProfile(form).flatMap(count -> {
            if (count > 0) {
                response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
                response.getHeaders().setLocation(URI.create("/user/application"));

                return response.setComplete();
            }
            return createTemporaryRedirect(response, "/error?code=15");
        }).switchIfEmpty(createTemporaryRedirect(response, "/error?code=20"));
    }

    private Mono<Void> createTemporaryRedirect(ServerHttpResponse response, String path) {
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(URI.create(path));

        return response.setComplete();
    }

    @RequestMapping("/user")
    public Mono<Void> userIndex(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/user/application"));

        return response.setComplete();
    }

    @GetMapping("/user/application")
    public Mono<String> userIndexApplication(@AuthenticationPrincipal UserDetails user, final Model model) {
        return reactiveService.readByLogin(user.getUsername())
                .flatMap(userProfile -> getUserApplication(userProfile, model))
                .switchIfEmpty(Mono.just("error"));
    }

    public Mono<String> getUserApplication(final UserProfile user, final Model model) {
        final ApplicationForm form = new ApplicationForm();
        form.setUsername(user.getLogin());
        model.addAttribute("username", user.getLogin());

        return reactiveService.readInfoById(user.getId())
                .flatMap(userInfo -> fillApplicationForm(form, userInfo, model))
                .switchIfEmpty(emptyApplicationForm(form, model));
    }

    private Mono<String> fillApplicationForm(final ApplicationForm form, UserInfo userInfo, final Model model) {
        form.setFirstName(userInfo.getFirstName());
        form.setSurName(userInfo.getSurName());
        form.setAge(userInfo.getAge());
        form.setSex(userInfo.getSex());
        form.setCity(userInfo.getCity());

        return reactiveService.readAllByUserInfoId(userInfo.getId())
                .collect(new InterestsCollectorToForm(form, model, "user/application"))
                .switchIfEmpty(Mono.just("error"));
    }

    private Mono<String> emptyApplicationForm(final ApplicationForm form, final Model model) {
        model.addAttribute("form", form);
        return Mono.just("user/application");
    }

    @PostMapping("/user/application")
    public Mono<Void> userApplication(ApplicationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
            response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
            response.getHeaders().setLocation(URI.create("/error?code=30"));

            return response.setComplete();
        }
        return reactiveService.postUserApplication(form).flatMap(count -> {
            System.err.println("count0 = " + count);
            if (count > 0) {
                response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
                response.getHeaders().setLocation(URI.create("/user/users"));

                return response.setComplete();
            }
            return createTemporaryRedirect(response, "/error?code=15");
        }).switchIfEmpty(createTemporaryRedirect(response, "/error?code=20"));
    }

    @RequestMapping("/user/friends")
    public Mono<String> userIndexFriends(@AuthenticationPrincipal UserDetails user, final Model model) {
        return reactiveService.readByLogin(user.getUsername())
                .flatMap(userProfile -> {
                    friendsIndexListOnSuccess(userProfile, model);
                    return Mono.just("user/friends/index");
                }).switchIfEmpty(Mono.just("error"));
    }

    private void friendsIndexListOnSuccess(UserProfile userProfile, final Model model) {
        model.addAttribute("friends", reactiveService.getAllFriends(userProfile.getId()));
        model.addAttribute("id", userProfile.getId());
    }

    @RequestMapping("/user/users")
    public Mono<String> userIndexList(@AuthenticationPrincipal UserDetails user, final Model model) {
        return reactiveService.readByLogin(user.getUsername())
                .flatMap(userProfile -> {
                    usersIndexListOnSuccess(userProfile, model);
                    return Mono.just("user/users/index");
                }).switchIfEmpty(Mono.just("error"));
    }

    private void usersIndexListOnSuccess(UserProfile userProfile, final Model model) {
        model.addAttribute("users", reactiveService.getAllUsers(userProfile.getId()));
        model.addAttribute("id", userProfile.getId());
    }

    private void userIndexListOnError(Throwable throwable) {
        LOG.error("userIndexList: ", throwable);
    }

    @RequestMapping("/user/users/profile")
    public String userIndexProfile(@AuthenticationPrincipal UserDetails user, final Model model, @RequestParam("id") long id) {
        model.addAttribute("user", reactiveService.readById(id));

        return "user/users/profile";
    }

    @GetMapping("/error")
    public String error(@RequestParam("code") int code, final Model model) {
        String errorMessage = "Error message by code: " + code;
        model.addAttribute("errorMessage", errorMessage);

        return "error";
    }
}
