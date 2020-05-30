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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.services.ReactiveService;
import su.svn.hiload.socialnetwork.utils.ErrorEnum;
import su.svn.hiload.socialnetwork.view.ApplicationForm;
import su.svn.hiload.socialnetwork.view.RegistrationForm;

import java.net.URI;
import java.util.ArrayList;

@Controller
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private final ReactiveService reactiveService;

    public IndexController(ReactiveService reactiveService) {
        this.reactiveService = reactiveService;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Mono<Void> userRegistration(RegistrationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
            return createTemporaryRedirect(response, "/error?code=" + ErrorEnum.E11.getCode());
        }
        return reactiveService.createUserProfile(form)
                .flatMap(errorEnum -> switchRedirect(response, errorEnum, "/user/application"));
    }

    @RequestMapping("/user")
    public Mono<Void> userIndex(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/user/application"));

        return response.setComplete();
    }

    @ModelAttribute("form")
    public ApplicationForm getForm() {
        ApplicationForm form = new ApplicationForm();
        form.setInterests(new ArrayList<>());

        return form;
    }

    @GetMapping("/user/application")
    public Mono<String> userIndexApplication(@AuthenticationPrincipal UserDetails user, final Model model) {
        return reactiveService.getUserApplication(user.getUsername())
                .flatMap(form -> viewApplication(user, model, form))
                .switchIfEmpty(Mono.just("error"));
    }

    private Mono<String> viewApplication(@AuthenticationPrincipal UserDetails user, Model model, ApplicationForm form) {
        model.addAttribute("form", form);
        model.addAttribute("username", user.getUsername());

        return Mono.just("user/application");
    }

    @PostMapping("/user/application")
    public Mono<Void> userApplication(ApplicationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
            return createTemporaryRedirect(response, "/error?code=" + ErrorEnum.E11.getCode());
        }
        return reactiveService.postUserApplication(form)
                .flatMap(errorEnum -> switchRedirect(response, errorEnum, "/user/search-users"));
    }

    private Mono<Void> switchRedirect(ServerHttpResponse response, ErrorEnum errorEnum, String path) {
        switch (errorEnum) {
            case OK: return createPermanentRedirect(response, path);
            case E1: return createPermanentRedirect(response, "/");
            default: return createTemporaryRedirect(response, "/error?code=" + errorEnum.getCode());
        }
    }

    private Mono<Void> createPermanentRedirect(ServerHttpResponse response, String path) {
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(URI.create(path));

        return response.setComplete();
    }

    private Mono<Void> createTemporaryRedirect(ServerHttpResponse response, String path) {
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(URI.create(path));

        return response.setComplete();
    }

    @RequestMapping("/user/friends")
    public String userIndexFriends(@AuthenticationPrincipal UserDetails user, final Model model) {
        model.addAttribute("friends", reactiveService.getAllFriends(user, id -> injectUserIdToModel(model, id)));
        return "user/friends/index";
    }

    @RequestMapping("/user/users")
    public String userIndexList(@AuthenticationPrincipal UserDetails user, final Model model) {
        model.addAttribute("users", reactiveService.getAllUsers(user, id -> injectUserIdToModel(model, id)));
        return "user/users/index";
    }

    private void injectUserIdToModel(final Model model, Long id) {
        model.addAttribute("id", id);
    }

    @RequestMapping("/user/users/profile")
    public String userIndexProfile(@AuthenticationPrincipal UserDetails user, final Model model, @RequestParam("id") long id) {
        model.addAttribute("user", reactiveService.readById(id));
        return "user/users/profile";
    }

    @GetMapping("/user/search-users")
    public String searchUsers(
            @AuthenticationPrincipal UserDetails user,
            final Model model,
            @RequestParam(value = "firstName", defaultValue = "") String firstName,
            @RequestParam(value = "surName", defaultValue = "") String surName) {
        model.addAttribute("users", reactiveService.searchAllUsers(user.getUsername(), firstName, surName));
        return "user/users/index";
    }

    @PostMapping("/user/search-users")
    public String searchUsers() {
        return "user/search/index";
    }

    @RequestMapping("/error")
    public String error(@RequestParam("code") int code, final Model model) {
        String errorMessage = "Error message by code: " + code;
        model.addAttribute("errorMessage", errorMessage);

        return "error";
    }
}
