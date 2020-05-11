package su.svn.hiload.socialnetwork.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.UserInfoDao;
import su.svn.hiload.socialnetwork.dao.UserInfoSignFriendDao;
import su.svn.hiload.socialnetwork.dao.UserInterestDao;
import su.svn.hiload.socialnetwork.dao.UserProfileDao;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.model.UserInfoSignFriend;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.model.security.UserProfile;
import su.svn.hiload.socialnetwork.view.ApplicationForm;
import su.svn.hiload.socialnetwork.view.Interest;
import su.svn.hiload.socialnetwork.view.Profile;
import su.svn.hiload.socialnetwork.view.RegistrationForm;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ReactiveService {

    private BCryptPasswordEncoder encoder;

    private final UserInfoDao userInfoR2dbcDao;

    private final UserInfoSignFriendDao userInfoSignFriendDao;

    private final UserProfileDao userProfileR2dbcDao;

    private final UserInterestDao userInterestR2dbcDao;

    public ReactiveService(
            @Value("${application.security.strength}") int strength,
            UserInfoDao userInfoR2dbcDao,
            UserInfoSignFriendDao userInfoSignFriendDao,
            UserProfileDao userProfileR2dbcDao,
            UserInterestDao userInterestR2dbcDao) {
        this.encoder = new BCryptPasswordEncoder(strength);
        this.userInfoR2dbcDao = userInfoR2dbcDao;
        this.userInfoSignFriendDao = userInfoSignFriendDao;
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

    public IReactiveDataDriverContextVariable getAllUsers(UserDetails user, Consumer<Long> consumer) {
        return new ReactiveDataDriverContextVariable(getAllUsersWithConsumeer(user, consumer));
    }

    private Flux<UserInfoSignFriend> getAllUsersWithConsumeer(UserDetails user, Consumer<Long> consumer) {
        return readByLogin(user.getUsername())
                .timeout(Duration.ofMillis(800), Mono.empty())
                .flatMapMany(userProfile -> {
                    consumer.accept(userProfile.getId());
                    return userInfoSignFriendDao.readAllUsersSignFriend(userProfile.getId());
                })
                .switchIfEmpty(Flux.empty());
    }

    public IReactiveDataDriverContextVariable getAllFriends(UserDetails user, Consumer<Long> consumer) {
        return new ReactiveDataDriverContextVariable(getAllFriendsWithConsumeer(user, consumer));
    }

    private Flux<UserInfo> getAllFriendsWithConsumeer(UserDetails user, Consumer<Long> consumer) {
        return readByLogin(user.getUsername())
                .timeout(Duration.ofMillis(800), Mono.empty())
                .flatMapMany(userProfile -> {
                    consumer.accept(userProfile.getId());
                    return userInfoR2dbcDao.readAllFriends(userProfile.getId());
                })
                .switchIfEmpty(Flux.empty());
    }

    public IReactiveDataDriverContextVariable createReactiveDataDriverContextVariableFluxEmpty() {
        return new ReactiveDataDriverContextVariable(Flux.empty(), 1);
    }

    public IReactiveDataDriverContextVariable getInterests(long id) {
        return new ReactiveDataDriverContextVariable(userInterestR2dbcDao.readAllByUserInfoId(id), 1);
    }

    public Mono<Profile> readById(long id) {
        return userInfoR2dbcDao.readFirstById(id)
                .timeout(Duration.ofMillis(800), Mono.empty())
                .map(userInfo -> convertUserInfo(userInfo, id));
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
        return userInfoR2dbcDao.readFirstById(id);
    }

    public Flux<UserInterest> readAllByUserInfoId(Long id) {
        return userInterestR2dbcDao.readAllByUserInfoId(id);
    }

    public Mono<Integer> postUserApplication(ApplicationForm form) {
        return userProfileR2dbcDao.findFirstByLogin(form.getUsername())
                .flatMap(userProfile -> postUserApplication(form, userProfile))
                .switchIfEmpty(Mono.just(-1));
    }

    private Mono<Integer> postUserApplication(ApplicationForm form, UserProfile userProfile) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userProfile.getId());
        userInfo.setFirstName(form.getFirstName());
        userInfo.setSurName(form.getSurName());
        userInfo.setAge(form.getAge());
        userInfo.setSex(form.getSex());
        userInfo.setCity(form.getCity());

        return userInfoR2dbcDao.existsById(userProfile.getId())
                .flatMap(exists -> updateOrCreate(userInfo, exists))
                .switchIfEmpty(Mono.just(-2));
    }

    private Mono<Integer> updateOrCreate(UserInfo userInfo, Boolean exists) {
        return exists ? userInfoR2dbcDao.update(userInfo) : userInfoR2dbcDao.create(userInfo);
    }
}
