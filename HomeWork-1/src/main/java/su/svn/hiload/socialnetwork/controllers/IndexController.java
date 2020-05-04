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
import su.svn.hiload.socialnetwork.services.OldSchoolBlockingService;
import su.svn.hiload.socialnetwork.services.ReactiveService;
import su.svn.hiload.socialnetwork.view.ApplicationForm;
import su.svn.hiload.socialnetwork.view.RegistrationForm;

import java.net.URI;

@Controller
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private final ReactiveService reactiveService;

    private final OldSchoolBlockingService blockingService;

    public IndexController(ReactiveService reactiveService, OldSchoolBlockingService blockingService) {
        this.blockingService = blockingService;
        this.reactiveService = reactiveService;
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
        if (blockingService.createUserProfile(form)) {
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
        blockingService.getUserApplication(user, model);

        return "user/application";
    }

    @PostMapping("/user/application")
    public Mono<Void> userApplication(ApplicationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
            response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
            response.getHeaders().setLocation(URI.create("/error?code=30"));

            return response.setComplete();
        }
        blockingService.postUserApplication(form);
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/user/users"));

        return response.setComplete();
    }

    @RequestMapping("/user/friends")
    public String userIndexFriends(@AuthenticationPrincipal UserDetails user, final Model model) {
        Long id = null;
        if ((id = blockingService.readIdByLogin(user.getUsername())) != null) {
            model.addAttribute("friends", reactiveService.getAllFriends(id));
        } else {
            model.addAttribute("friends", reactiveService.createReactiveDataDriverContextVariableFluxEmpty());
        }
        model.addAttribute("id", id);

        return "user/friends/index";
    }

    @RequestMapping("/user/users")
    public String userIndexList(@AuthenticationPrincipal UserDetails user, final Model model) {
        Long id = null;
        if ((id = blockingService.readIdByLogin(user.getUsername())) != null) {
            model.addAttribute("users", reactiveService.getAllUsers(id));
        } else {
            model.addAttribute("users", reactiveService.createReactiveDataDriverContextVariableFluxEmpty());
        }
        model.addAttribute("id", id);

        return "user/users/index";
    }

    @GetMapping("/error")
    public String error(@RequestParam("code") int code, final Model model) {
        String errorMessage = "Error message by code: " + code;
        model.addAttribute("errorMessage", errorMessage);

        return "error";
    }
}
