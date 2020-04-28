package su.svn.hiload.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import su.svn.hiload.socialnetwork.model.security.UserProfile;
import su.svn.hiload.socialnetwork.view.RegistrationForm;

@Controller
public class IndexController {

    BCryptPasswordEncoder encoder;

    private final su.svn.hiload.socialnetwork.dao.jdbc.UserProfileDao userProfileJdbcDao;

    private final su.svn.hiload.socialnetwork.dao.r2dbc.UserProfileDao userProfileR2dbcDao;

    public IndexController(
            @Value("${application.security.strength}") int strength,
            @Qualifier("userProfileJdbcDao") su.svn.hiload.socialnetwork.dao.jdbc.UserProfileDao userProfileJdbcDao,
            @Qualifier("userProfileR2dbcDao") su.svn.hiload.socialnetwork.dao.r2dbc.UserProfileDao userProfileR2dbcDao) {
        this.encoder = new BCryptPasswordEncoder(strength);
        this.userProfileJdbcDao = userProfileJdbcDao;
        this.userProfileR2dbcDao = userProfileR2dbcDao;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/user")
    public String userIndex(final Model model) {
        model.addAttribute("users", getUsers());

        return "user/index";
    }

    private IReactiveDataDriverContextVariable getUsers() {
        return new ReactiveDataDriverContextVariable(userProfileR2dbcDao.readAll(), 1);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String userRegistration(RegistrationForm form, BindingResult errors, final Model model) {
        if (errors.hasErrors()) {
            return "error";
        }

        String hash = encoder.encode(form.getPassword());
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(form.getUsername());
        userProfile.setHash(hash);
        userProfile.setLocked(false);
        userProfile.setExpired(false);

        int count = userProfileJdbcDao.create(userProfile);
        if (count > 0) {
            model.addAttribute("login", form.getUsername());
            model.addAttribute("message", "User registration successful.");
            return "user/index";
        }

        model.addAttribute("message", "Error- check the console log.");

        return "registration";
    }
}
