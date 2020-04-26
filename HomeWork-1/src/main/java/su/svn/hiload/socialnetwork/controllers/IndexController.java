package su.svn.hiload.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserProfileDao;

@Controller
public class IndexController {

    private final UserProfileDao userProfileDao;

    public IndexController(@Qualifier("userProfileR2dbcDao") UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    @RequestMapping("/")
    public String index(final Model model) {

        // loads 1 and display 1, stream data, data driven mode.
        IReactiveDataDriverContextVariable reactiveDataDrivenMode = new ReactiveDataDriverContextVariable(userProfileDao.findAll(), 1);

        model.addAttribute("users", reactiveDataDrivenMode);

        return "index";
    }

    @RequestMapping("/user")
    public String userIndex(final Model model) {

        // loads 1 and display 1, stream data, data driven mode.
        IReactiveDataDriverContextVariable reactiveDataDrivenMode = new ReactiveDataDriverContextVariable(userProfileDao.findAll(), 1);

        model.addAttribute("users", reactiveDataDrivenMode);

        return "user/index";
    }
}
