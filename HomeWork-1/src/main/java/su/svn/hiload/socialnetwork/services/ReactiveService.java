package su.svn.hiload.socialnetwork.services;

import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInfoDao;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInterestDao;

@Service
public class ReactiveService {

    private final UserInfoDao userInfoR2dbcDao;

    private final UserInterestDao userInterestR2dbcDao;

    public ReactiveService(UserInfoDao userInfoR2dbcDao, UserInterestDao userInterestR2dbcDao) {
        this.userInfoR2dbcDao = userInfoR2dbcDao;
        this.userInterestR2dbcDao = userInterestR2dbcDao;
    }

    public IReactiveDataDriverContextVariable getAllUsers(long id) {
        return new ReactiveDataDriverContextVariable(userInfoR2dbcDao.readAllUsers(id), 1);
    }

    public IReactiveDataDriverContextVariable getAllFriends(long id) {
        return new ReactiveDataDriverContextVariable(userInfoR2dbcDao.readAllFriends(id), 1);
    }

    public IReactiveDataDriverContextVariable createReactiveDataDriverContextVariableFluxEmpty() {
        return new ReactiveDataDriverContextVariable(Flux.empty(), 1);
    }

    public IReactiveDataDriverContextVariable getInterests(long id) {
        return new ReactiveDataDriverContextVariable(userInterestR2dbcDao.readAllByUserInfoId(id), 1);
    }

}
