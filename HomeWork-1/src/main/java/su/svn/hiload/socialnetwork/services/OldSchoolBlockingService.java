package su.svn.hiload.socialnetwork.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import su.svn.hiload.socialnetwork.dao.jdbc.UserInfoDao;
import su.svn.hiload.socialnetwork.dao.jdbc.UserInterestDao;
import su.svn.hiload.socialnetwork.dao.jdbc.UserProfileDao;
import su.svn.hiload.socialnetwork.exceptions.NotFound;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.model.security.UserProfile;
import su.svn.hiload.socialnetwork.view.ApplicationForm;
import su.svn.hiload.socialnetwork.view.Interest;
import su.svn.hiload.socialnetwork.view.RegistrationForm;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class OldSchoolBlockingService {

    private static final Logger LOG = LoggerFactory.getLogger(OldSchoolBlockingService.class);

    private BCryptPasswordEncoder encoder;

    private final UserInfoDao userInfoJdbcDao;

    private final UserInterestDao userInterestJdbcDao;

    private final UserProfileDao userProfileJdbcDao;

    public OldSchoolBlockingService(
            @Value("${application.security.strength}") int strength,
            UserInfoDao userInfoJdbcDao,
            UserInterestDao userInterestJdbcDao,
            UserProfileDao userProfileJdbcDao) {
        this.encoder = new BCryptPasswordEncoder(strength);
        this.userInfoJdbcDao = userInfoJdbcDao;
        this.userInterestJdbcDao = userInterestJdbcDao;
        this.userProfileJdbcDao = userProfileJdbcDao;
    }


    public boolean createUserProfile(RegistrationForm form) {
        String hash = encoder.encode(form.getPassword());
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(form.getUsername());
        userProfile.setHash(hash);
        userProfile.setLocked(false);
        userProfile.setExpired(false);

        return userProfileJdbcDao.create(userProfile) > 0;
    }


    public void getUserApplication(@AuthenticationPrincipal UserDetails user, Model model) {
        final ApplicationForm form = new ApplicationForm();
        form.setUsername(user.getUsername());
        userProfileJdbcDao.readIdByLogin(user.getUsername())
                .ifPresentOrElse(id -> fillApplicationForm(form, id), NotFound::is);
        model.addAttribute("form", form);
        model.addAttribute("username", user.getUsername());
    }

    private void fillApplicationForm(ApplicationForm form, long id) {
        CompletableFuture<List<UserInterest>> interestCompletableFuture = runUserInterestCompletableFuture(id);
        userInfoJdbcDao.readById(id).ifPresentOrElse(getUserInfoConsumer(form), NotFound::is);
        try {
            List<Interest> interest = interestCompletableFuture.get().stream()
                    .map(userInterest -> new Interest(userInterest.getId(), userInterest.getInterest()))
                    .collect(Collectors.toList());
            form.setInterests(interest);
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("fillApplicationForm ", e);
        }
    }

    private CompletableFuture<List<UserInterest>> runUserInterestCompletableFuture(long id) {
        return CompletableFuture.supplyAsync(getListUserInterestSupplier(id));
    }

    private Supplier<List<UserInterest>> getListUserInterestSupplier(long id) {
        return () -> userInterestJdbcDao.readAllByUserInfoId(id);
    }

    private Consumer<UserInfo> getUserInfoConsumer(ApplicationForm form) {
        return userInfo -> {
            form.setFirstName(userInfo.getFirstName());
            form.setSurName(userInfo.getSurName());
            form.setAge(userInfo.getAge());
            form.setSex(userInfo.getSex());
            form.setCity(userInfo.getCity());
        };
    }

    public void postUserApplication(ApplicationForm form) {
        userProfileJdbcDao.readLogin(form.getUsername())
                .ifPresentOrElse(userProfile -> userApplication(form, (UserProfile) userProfile), NotFound::is);
    }

    private void userApplication(ApplicationForm form, UserProfile userProfile) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userProfile.getId());
        userInfo.setFirstName(form.getFirstName());
        userInfo.setSurName(form.getSurName());
        userInfo.setAge(form.getAge());
        userInfo.setSex(form.getSex());
        userInfo.setCity(form.getCity());

        if (userInfoJdbcDao.existsById(userProfile.getId())) {
            userInfoJdbcDao.update(userInfo);
        } else {
            userInfoJdbcDao.create(userInfo);
        }
        if (form.getInterests() != null) {
            List<UserInterest> userInterests = form.getInterests().stream()
                    .filter(Objects::nonNull)
                    .filter(interest -> interest.getId() == null)
                    .filter(interest -> interest.getInterest() != null)
                    .map(interest -> createUserInterest(interest, userProfile.getId()))
                    .collect(Collectors.toList());
            userInterestJdbcDao.createBatch(userInterests);
        }
    }

    private UserInterest createUserInterest(Interest interest, long id) {
        return new UserInterest(interest.getId(), id, interest.getInterest());
    }

    @Nullable
    public Long readIdByLogin(String username) {
        return userProfileJdbcDao.readIdByLogin(username).orElse(null);
    }
}
