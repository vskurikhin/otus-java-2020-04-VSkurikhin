package su.svn.hiload.socialnetwork.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.UserInfoDao;
import su.svn.hiload.socialnetwork.dao.UserInterestDao;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.view.FriendId;

@RestController
public class RestUserController {

    private final UserInfoDao userInfoDao;
    private final UserInterestDao userInterestDao;

    public RestUserController(UserInfoDao userInfoDao, UserInterestDao userInterestDao) {
        this.userInfoDao = userInfoDao;
        this.userInterestDao = userInterestDao;
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
