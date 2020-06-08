package su.svn.hiload.socialnetwork.controllers;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.Message;
import su.svn.hiload.socialnetwork.services.ReactiveService;

@RestController
public class RestMessageController {

    private final ReactiveService reactiveService;

    public RestMessageController(ReactiveService reactiveService) {
        this.reactiveService = reactiveService;
    }

    @GetMapping("/public/message/all")
    private Flux<Message> messagesAll() {
        return reactiveService.searchMessagesAll();
    }

    @PostMapping("/public/message")
    Mono<Message> newMessage(@RequestBody Message message) {
        return reactiveService.create(message);
    }

    @PutMapping("/public/message")
    Mono<Message> updateMessage(@RequestBody Message message) {
        return reactiveService.update(message);
    }

    @DeleteMapping("/public/message")
    Mono<Void> deleteMessage(@RequestBody Message message) {
        return reactiveService.delete(message);
    }
}
