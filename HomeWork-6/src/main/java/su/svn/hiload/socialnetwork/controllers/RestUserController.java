package su.svn.hiload.socialnetwork.controllers;

import org.springframework.web.bind.annotation.*;
import su.svn.hiload.socialnetwork.services.ReactiveService;

@RestController
public class RestUserController {

    private final ReactiveService reactiveService;

    public RestUserController(ReactiveService reactiveService) {
        this.reactiveService = reactiveService;
    }
}
