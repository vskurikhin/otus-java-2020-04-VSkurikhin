package su.svn.hiload.socialnetwork.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.UserInfoDao;
import su.svn.hiload.socialnetwork.dao.UserInterestDao;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.model.UserLog;
import su.svn.hiload.socialnetwork.services.ReactiveService;
import su.svn.hiload.socialnetwork.view.FriendId;
import su.svn.hiload.socialnetwork.view.UserInfoDto;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class RestUserController {

    private final UserInfoDao userInfoDao;
    private final UserInterestDao userInterestDao;
    private final ReactiveService reactiveService;

    private final AtomicLong count = new AtomicLong(0);

    public RestUserController(UserInfoDao userInfoDao, UserInterestDao userInterestDao, ReactiveService reactiveService) {
        this.userInfoDao = userInfoDao;
        this.userInterestDao = userInterestDao;
        this.reactiveService = reactiveService;
    }

    @GetMapping("/public/search-users")
    private Flux<UserInfo> searchUser(
            @RequestParam(value = "firstName", defaultValue = "") String firstName,
            @RequestParam(value = "surName", defaultValue = "") String surName) {
        return reactiveService.searchUsers(firstName, surName);
    }

    @GetMapping("/public/search-users-with-interests")
    private Flux<UserInfoDto> searchUserWithInterests(
            @RequestParam(value = "firstName", defaultValue = "") String firstName,
            @RequestParam(value = "surName", defaultValue = "") String surName) {
        return reactiveService.searchUsersWithInterests(firstName, surName);
    }

    @GetMapping("${application.rest.user}/interests/{id}")
    private Flux<UserInterest> readAllByUserInfoId(@PathVariable long id) {
        return userInterestDao.readAllByUserInfoId(id);
    }

    @PostMapping(value = "${application.rest.user}/add-friend/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    private Mono<UserInfo> addFriend(@PathVariable long id, @RequestBody FriendId friendId) {
        return userInfoDao.addFriend(id, friendId.getFriendId());
    }

    @GetMapping("/public/transaction")
    private Mono<UserLog> transaction(@RequestParam(value = "id", defaultValue = "") long id) {
        return reactiveService.transaction(id).doOnSubscribe(subscription -> incrementAndGetPrint());
    }

    private void incrementAndGetPrint() {
        long c = count.incrementAndGet();
        System.err.printf("%d\r", c);
    }
}
