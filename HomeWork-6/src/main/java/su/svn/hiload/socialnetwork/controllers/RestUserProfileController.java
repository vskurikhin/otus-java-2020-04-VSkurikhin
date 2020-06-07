package su.svn.hiload.socialnetwork.controllers;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import su.svn.hiload.socialnetwork.model.security.UserProfile;
import su.svn.hiload.socialnetwork.services.ReactiveService;

@RestController
public class RestUserProfileController {

    private final ReactiveService reactiveService;

    public RestUserProfileController(ReactiveService reactiveService) {
        this.reactiveService = reactiveService;
    }

    @GetMapping("/public/user-profile/all")
    private Flux<UserProfile> userProfilesAll() {
        return reactiveService.searchUserProfilesAll();
    }
}
