package su.svn.hiload.socialnetwork.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.UserInfoDao;
import su.svn.hiload.socialnetwork.dao.UserInterestDao;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.services.ReactiveService;
import su.svn.hiload.socialnetwork.view.FriendId;
import su.svn.hiload.socialnetwork.view.UserInfoDto;

import java.util.List;

@RestController
public class RestUserController {

    private final UserInfoDao userInfoDao;
    private final UserInterestDao userInterestDao;
    private final ReactiveService reactiveService;

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
}
