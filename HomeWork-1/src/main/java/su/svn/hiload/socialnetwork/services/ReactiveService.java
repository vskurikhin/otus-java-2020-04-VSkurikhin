package su.svn.hiload.socialnetwork.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInfoDao;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInterestDao;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserProfileDao;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.model.security.UserProfile;
import su.svn.hiload.socialnetwork.view.Interest;
import su.svn.hiload.socialnetwork.view.Profile;
import su.svn.hiload.socialnetwork.view.RegistrationForm;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactiveService {

    private BCryptPasswordEncoder encoder;

    private final UserInfoDao userInfoR2dbcDao;

    private final UserProfileDao userProfileR2dbcDao;

    private final UserInterestDao userInterestR2dbcDao;

    public ReactiveService(
            @Value("${application.security.strength}") int strength,
            UserInfoDao userInfoR2dbcDao,
            UserProfileDao userProfileR2dbcDao,
            UserInterestDao userInterestR2dbcDao) {
        this.encoder = new BCryptPasswordEncoder(strength);
        this.userInfoR2dbcDao = userInfoR2dbcDao;
        this.userProfileR2dbcDao = userProfileR2dbcDao;
        this.userInterestR2dbcDao = userInterestR2dbcDao;
    }

    public Mono<Integer> createUserProfile(RegistrationForm form) {
        String hash = encoder.encode(form.getPassword());
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(form.getUsername());
        userProfile.setHash(hash);
        userProfile.setLocked(false);
        userProfile.setExpired(false);

        return userProfileR2dbcDao.create(userProfile);
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

    public Mono<Profile> readById(long id) {
        return userInfoR2dbcDao.readById(id).map(userInfo -> convertUserInfo(userInfo, id));
    }

    public Mono<UserProfile> readByLogin(String login) {
        return userProfileR2dbcDao.findFirstByLogin(login);
    }

    private Profile convertUserInfo(UserInfo userInfo, long id) {
        Profile profile = new Profile();
        profile.setFirstName(userInfo.getFirstName());
        profile.setSurName(userInfo.getSurName());
        profile.setAge(userInfo.getAge());
        profile.setSex(userInfo.getSex());
        profile.setCity(userInfo.getCity());

        userInterestR2dbcDao.readAllByUserInfoId(id)
                .collectList()
                .map(this::streamConvertUserInterest)
                .subscribe(profile::setInterests);

        return profile;
    }

    private List<Interest> streamConvertUserInterest(List<UserInterest> userInterests) {
        return userInterests.stream()
                .map(ReactiveService.this::convertUserInterest)
                .collect(Collectors.toList());
    }

    private Interest convertUserInterest(UserInterest userInterest) {
        Interest interest = new Interest();
        interest.setId(userInterest.getId());
        interest.setInterest(userInterest.getInterest());

        return interest;
    }

    public Mono<UserInfo> readInfoById(Long id) {
        return userInfoR2dbcDao.readById(id);
    }

    public Flux<UserInterest> readAllByUserInfoId(Long id) {
        return userInterestR2dbcDao.readAllByUserInfoId(id);
    }
}
