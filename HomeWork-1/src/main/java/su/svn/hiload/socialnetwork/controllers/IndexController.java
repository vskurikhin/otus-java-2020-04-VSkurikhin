package su.svn.hiload.socialnetwork.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private BCryptPasswordEncoder encoder;

    private final su.svn.hiload.socialnetwork.dao.r2dbc.UserInfoDao userInfoR2dbcDao;

    private final su.svn.hiload.socialnetwork.dao.r2dbc.UserInterestDao userInterestR2dbcDao;

    private final UserInfoDao userInfoJdbcDao;

    private final UserInterestDao userInterestJdbcDao;

    private final UserProfileDao userProfileJdbcDao;

    public IndexController(
            @Value("${application.security.strength}") int strength,
            su.svn.hiload.socialnetwork.dao.r2dbc.UserInfoDao userInfoR2dbcDao,
            @Qualifier("userInterestDao") su.svn.hiload.socialnetwork.dao.r2dbc.UserInterestDao userInterestR2dbcDao,
            UserInfoDao userInfoJdbcDao,
            UserInterestDao userInterestJdbcDao,
            UserProfileDao userProfileJdbcDao) {
        this.encoder = new BCryptPasswordEncoder(strength);
        this.userInfoJdbcDao = userInfoJdbcDao;
        this.userInfoR2dbcDao = userInfoR2dbcDao;
        this.userInterestJdbcDao = userInterestJdbcDao;
        this.userInterestR2dbcDao = userInterestR2dbcDao;
        this.userProfileJdbcDao = userProfileJdbcDao;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Mono<Void> userRegistration(RegistrationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
             response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
             response.getHeaders().setLocation(URI.create("/error?code=10"));

             return response.setComplete();
        }

        String hash = encoder.encode(form.getPassword());
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(form.getUsername());
        userProfile.setHash(hash);
        userProfile.setLocked(false);
        userProfile.setExpired(false);

        int count = userProfileJdbcDao.create(userProfile);
        if (count > 0) {
            response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
            response.getHeaders().setLocation(URI.create("/user/application"));

             return response.setComplete();
        }

        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(URI.create("/error?code=20"));

        return response.setComplete();
    }

    @RequestMapping("/user")
    public Mono<Void> userIndex(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/user/application"));

        return response.setComplete();
    }

    @GetMapping("/user/application")
    public String userIndexApplication(@AuthenticationPrincipal UserDetails user, final Model model) {
        final ApplicationForm form = new ApplicationForm();
        form.setUsername(user.getUsername());
        userProfileJdbcDao.readIdByLogin(user.getUsername())
                .ifPresentOrElse(id -> fillApplicationForm(form, id), NotFound::is);
        model.addAttribute("form", form);
        model.addAttribute("username", user.getUsername());

        return "user/application";
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

    @PostMapping("/user/application")
    public Mono<Void> userApplication(ApplicationForm form, BindingResult errors, ServerHttpResponse response) {
        if (errors.hasErrors()) {
            response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
            response.getHeaders().setLocation(URI.create("/error?code=30"));

            return response.setComplete();
        }
        userProfileJdbcDao.readLogin(form.getUsername())
                .ifPresentOrElse(userProfile -> userApplication(form, userProfile), NotFound::is);
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/user/friends"));

        return response.setComplete();
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
        System.err.println("interest = " + interest);
        return new UserInterest(interest.getId(), id, interest.getInterest());
    }

    @RequestMapping("/user/friends")
    public String userIndexFriends(@AuthenticationPrincipal UserDetails user, final Model model) {
        Optional<Long> optionalId = userProfileJdbcDao.readIdByLogin(user.getUsername());
        if (optionalId.isPresent()) {
            model.addAttribute("friends", getFriends());

        } else {
            model.addAttribute("friends", createReactiveDataDriverContextVariableFluxEmpty());
        }

        return "user/friends/index";
    }

    private IReactiveDataDriverContextVariable getFriends() {
        return new ReactiveDataDriverContextVariable(userInfoR2dbcDao.readAll(), 1);
    }

    @RequestMapping("/user/interest")
    public String userInterest(@AuthenticationPrincipal UserDetails user, final Model model) {
        Optional<Long> optionalId = userProfileJdbcDao.readIdByLogin(user.getUsername());
        if (optionalId.isPresent()) {
            model.addAttribute("interest", getInterests(optionalId.get()));

        } else {
            model.addAttribute("interest", createReactiveDataDriverContextVariableFluxEmpty());
        }

        return "user/friends/index";
    }

    private IReactiveDataDriverContextVariable createReactiveDataDriverContextVariableFluxEmpty() {
        return new ReactiveDataDriverContextVariable(Flux.empty(), 1);
    }

    private IReactiveDataDriverContextVariable getInterests(long id) {
        return new ReactiveDataDriverContextVariable(userInterestR2dbcDao.readAllByUserInfoId(id), 1);
    }

    @GetMapping("/error")
    public String error(@RequestParam("code") int code, final Model model) {
        String errorMessage = "Error message by code: " + code;
        model.addAttribute("errorMessage", errorMessage);

        return "error";
    }
}
