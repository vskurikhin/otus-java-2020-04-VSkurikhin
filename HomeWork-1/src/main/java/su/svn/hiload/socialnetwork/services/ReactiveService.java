package su.svn.hiload.socialnetwork.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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
import su.svn.hiload.socialnetwork.utils.ErrorEnum;
import su.svn.hiload.socialnetwork.utils.InterestsCollectorToForm;
import su.svn.hiload.socialnetwork.utils.ListCollectorToSizeInteger;
import su.svn.hiload.socialnetwork.view.*;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static su.svn.hiload.socialnetwork.utils.ErrorCode.*;

@Service
public class ReactiveService {

    private BCryptPasswordEncoder encoder;

    private final int duration;

    private final int limit;

    private final UserInfoDao userInfoDao;

    private final UserInfoSignFriendDao userInfoSignFriendDao;

    private final UserProfileDao userProfileDao;

    private final UserInterestDao userInterestDao;


    public ReactiveService(
            @Value("${application.security.strength}") int strength,
            @Value("${application.reactive.duration}") int duration,
            @Value("${application.reactive.limit}") int limit,
            UserInfoDao userInfoDao,
            UserInfoSignFriendDao userInfoSignFriendDao,
            UserProfileDao userProfileDao,
            UserInterestDao userInterestDao) {
        this.encoder = new BCryptPasswordEncoder(strength);
        this.duration = duration;
        this.limit = limit;
        this.userInfoDao = userInfoDao;
        this.userInfoSignFriendDao = userInfoSignFriendDao;
        this.userProfileDao = userProfileDao;
        this.userInterestDao = userInterestDao;
    }

    public Mono<ErrorEnum> createUserProfile(RegistrationForm form) {
        return createUserProfile1(form)
                .timeout(Duration.ofMillis(3*duration), Mono.empty())
                .flatMap(count -> count > 0 ? Mono.just(ErrorEnum.OK) : Mono.just(ErrorEnum.E12))
                .switchIfEmpty(Mono.just(ErrorEnum.E13));
    }

    private Mono<Integer> createUserProfile1(RegistrationForm form) {
        String hash = encoder.encode(form.getPassword());
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(form.getUsername());
        userProfile.setHash(hash);
        userProfile.setLocked(false);
        userProfile.setExpired(false);

        return userProfileDao.create(userProfile);
    }

    public IReactiveDataDriverContextVariable getAllUsers(UserDetails user, Consumer<Long> consumer) {
        return new ReactiveDataDriverContextVariable(getAllUsersWithConsumer(user, consumer));
    }

    private Flux<UserInfoSignFriend> getAllUsersWithConsumer(UserDetails user, Consumer<Long> consumer) {
        return readByLogin(user.getUsername())
                .timeout(Duration.ofMillis(duration), Mono.empty())
                .flatMapMany(userProfile -> {
                    consumer.accept(userProfile.getId());
                    return userInfoSignFriendDao.readAllUsersSignFriend(userProfile.getId());
                })
                .switchIfEmpty(Flux.empty());
    }

    public IReactiveDataDriverContextVariable getAllFriends(UserDetails user, Consumer<Long> consumer) {
        return new ReactiveDataDriverContextVariable(getAllFriendsWithConsumer(user, consumer));
    }

    private Flux<UserInfo> getAllFriendsWithConsumer(UserDetails user, final Consumer<Long> consumer) {
        return readByLogin(user.getUsername())
                .timeout(Duration.ofMillis(duration), Mono.empty())
                .flatMapMany(userProfile -> readAllFriends(userProfile, consumer))
                .switchIfEmpty(Flux.empty());
    }

    private Flux<UserInfo> readAllFriends(UserProfile userProfile, Consumer<Long> consumer) {
        consumer.accept(userProfile.getId());
        return userInfoDao.readAllFriends(userProfile.getId());
    }

    public Mono<Profile> readById(long id) {
        return userInfoDao.readFirstById(id)
                .timeout(Duration.ofMillis(duration), Mono.empty())
                .map(userInfo -> convertUserInfo(userInfo, id));
    }

    private Mono<UserProfile> readByLogin(String login) {
        return userProfileDao.findFirstByLogin(login);
    }

    private Profile convertUserInfo(UserInfo userInfo, long id) {
        Profile profile = new Profile();
        profile.setFirstName(userInfo.getFirstName());
        profile.setSurName(userInfo.getSurName());
        profile.setAge(userInfo.getAge());
        profile.setSex(userInfo.getSex());
        profile.setCity(userInfo.getCity());

        userInterestDao.readAllByUserInfoId(id)
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

    private Mono<UserInfo> readInfoById(Long id) {
        return userInfoDao.readFirstById(id);
    }

    private Flux<UserInterest> readAllByUserInfoId(Long id) {
        return userInterestDao.readAllByUserInfoId(id);
    }

    public Mono<ErrorEnum> postUserApplication(final ApplicationForm form) {
        return userProfileDao.findFirstByLogin(form.getUsername())
                .timeout(Duration.ofMillis(duration), Mono.empty())
                .flatMap(userProfile -> postUserApplication(form, userProfile))
                .switchIfEmpty(Mono.just(ErrorEnum.E99));
    }

    private Mono<ErrorEnum> postUserApplication(final ApplicationForm form, final  UserProfile userProfile) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userProfile.getId());
        userInfo.setFirstName(form.getFirstName());
        userInfo.setSurName(form.getSurName());
        userInfo.setAge(form.getAge());
        userInfo.setSex(form.getSex());
        userInfo.setCity(form.getCity());

        return userInfoDao.existsById(userProfile.getId())
                .timeout(Duration.ofMillis(duration), Mono.empty())
                .flatMap(exists -> updateOrCreate(userInfo, exists))
                .flatMap(count -> saveInterests(form, userProfile, count))
                .flatMap(this::switchErrorCodeByInteger)
                .switchIfEmpty(Mono.just(ErrorEnum.E99));
    }

    private Mono<Integer> saveInterests(ApplicationForm form, UserProfile userProfile, Integer count) {
        if (null == form.getInterests()) {
            return Mono.just(0);
        }
        return count > -1 ? saveInterests(form.getInterests(), userProfile.getId()) : Mono.just(count);
    }

    private Mono<Integer> saveInterests(List<Interest> interests, long userId) {
        List<UserInterest> userInterests = interests.stream()
                .map(interest -> new UserInterest(interest.getId(), userId, interest.getInterest()))
                .collect(Collectors.toList());
        return userInterestDao.saveAll(userInterests)
                .timeout(Duration.ofMillis(duration), Mono.empty())
                .collect(new ListCollectorToSizeInteger<>());
    }

    private Mono<ErrorEnum> switchErrorCodeByInteger(int count) {
        if (count > -1) {
            return Mono.just(ErrorEnum.OK);
        }
        switch (count) {
            case CREATE_SWITCH_IF_EMPTY: return Mono.just(ErrorEnum.E14);
            case UPDATE_SWITCH_IF_EMPTY: return Mono.just(ErrorEnum.E15);
            default: return Mono.empty();
        }
    }

    private Mono<Integer> updateOrCreate(UserInfo userInfo, Boolean exists) {
        return exists ? userInfoDao.update(userInfo) : userInfoDao.create(userInfo);
    }

    public Mono<ApplicationForm> getUserApplication(String username) {
        return readByLogin(username)
                .timeout(Duration.ofMillis(duration), Mono.empty())
                .flatMap(this::getUserApplication)
                .switchIfEmpty(Mono.empty());
    }

    private Mono<ApplicationForm> getUserApplication(final UserProfile user) {
        final ApplicationForm form = new ApplicationForm();
        form.setUsername(user.getLogin());

        return readInfoById(user.getId())
                .timeout(Duration.ofMillis(duration), Mono.empty())
                .flatMap(userInfo -> fillApplicationForm(form, userInfo))
                .switchIfEmpty(Mono.just(form));
    }

    private Mono<ApplicationForm> fillApplicationForm(final ApplicationForm form, UserInfo userInfo) {
        form.setFirstName(userInfo.getFirstName());
        form.setSurName(userInfo.getSurName());
        form.setAge(userInfo.getAge());
        form.setSex(userInfo.getSex());
        form.setCity(userInfo.getCity());

        return readAllByUserInfoId(userInfo.getId())
                .collect(new InterestsCollectorToForm(form))
                .switchIfEmpty(Mono.just(form));
    }

    public Flux<UserInfo> searchUsers(String firstName, String surName) {
        if (firstName.length() > 0 && surName.length() > 0) {
            return userInfoDao.searchAllByFirstNameAndSurName(firstName, surName)
                    .take(limit)
                    .timeout(Duration.ofMillis(duration), Mono.empty());
        } else if (firstName.length() > 0) {
            return userInfoDao.searchAllByFirstName(firstName)
                    .take(limit)
                    .timeout(Duration.ofMillis(duration), Mono.empty());
        } else if (surName.length() > 0) {
            return userInfoDao.searchAllBySurName(surName)
                    .take(limit)
                    .timeout(Duration.ofMillis(duration), Mono.empty());
        }
        return Flux.empty();
    }

    public IReactiveDataDriverContextVariable searchAllUsers(String username, String firstName, String surName) {
        return new ReactiveDataDriverContextVariable(searchUsersSignFriend(username, firstName, surName));
    }

    private Flux<UserInfoSignFriend> searchUsersSignFriend(String username, String firstName, String surName) {
        if (firstName.length() > 0 && surName.length() > 0) {
            return readByLogin(username)
                    .timeout(Duration.ofMillis(duration), Mono.empty())
                    .flatMapMany(userProfile -> searchAllByFirstNameAndSurName(userProfile.getId(), firstName, surName))
                    .switchIfEmpty(Flux.empty());
        } else if (firstName.length() > 0) {
            return readByLogin(username)
                    .timeout(Duration.ofMillis(duration), Mono.empty())
                    .flatMapMany(userProfile -> searchAllByFirstName(userProfile.getId(), firstName))
                    .switchIfEmpty(Flux.empty());
        } else if (surName.length() > 0) {
            return readByLogin(username)
                    .timeout(Duration.ofMillis(duration), Mono.empty())
                    .flatMapMany(userProfile -> getTimeout(userProfile.getId(), surName))
                    .switchIfEmpty(Flux.empty());
        }
        return Flux.empty();
    }

    private Flux<UserInfoSignFriend> getTimeout(long id, String surName) {
        return userInfoSignFriendDao.searchAllBySurName(id, surName)
                .take(limit)
                .timeout(Duration.ofMillis(duration), Mono.empty());
    }

    private Flux<UserInfoSignFriend> searchAllByFirstName(long id, String firstName) {
        return userInfoSignFriendDao.searchAllByFirstName(id, firstName)
                .take(limit)
                .timeout(Duration.ofMillis(duration), Mono.empty());
    }

    private Flux<UserInfoSignFriend> searchAllByFirstNameAndSurName(long id, String firstName, String surName) {
        return userInfoSignFriendDao.searchAllByFirstNameAndSurName(id, firstName, surName)
                .take(limit)
                .timeout(Duration.ofMillis(duration), Mono.empty());
    }
}
