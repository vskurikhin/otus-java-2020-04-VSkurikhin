package su.svn.hiload.socialnetwork.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInterestDao;
import su.svn.hiload.socialnetwork.model.UserInterest;

@RestController
@RequestMapping("/user/interests")
public class RestUserController {

    private final UserInterestDao userInterestDao;

    public RestUserController(UserInterestDao userInterestDao) {
        this.userInterestDao = userInterestDao;
    }

    @GetMapping("/{id}")
    private Flux<UserInterest> readAllByUserInfoId(@PathVariable long id) {
        return userInterestDao.readAllByUserInfoId(id);
    }
}
