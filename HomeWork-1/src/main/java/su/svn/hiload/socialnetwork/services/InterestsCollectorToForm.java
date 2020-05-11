package su.svn.hiload.socialnetwork.services;

import org.springframework.ui.Model;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.view.ApplicationForm;
import su.svn.hiload.socialnetwork.view.Interest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class InterestsCollectorToForm implements Collector<UserInterest, List<UserInterest>, String> {

    final ApplicationForm form;

    final Model model;

    final String view;

    public InterestsCollectorToForm(ApplicationForm form, Model model, String view) {
        this.form = form;
        this.model = model;
        this.view = view;
    }

    @Override
    public Supplier<List<UserInterest>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<UserInterest>, UserInterest> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<UserInterest>> combiner() {
        return (userInterests1, userInterests2) -> {
            userInterests1.addAll(userInterests2);
            return userInterests1;
        };
    }

    @Override
    public Function<List<UserInterest>, String> finisher() {
        return this::finisherApply;
    }

    private String finisherApply(List<UserInterest> userInterests) {
        List<Interest> interests = userInterests.stream()
                .map(functionUserInterestInterestFunction())
                .collect(Collectors.toList());
        form.setInterests(interests);
        model.addAttribute("form", form);

        return view;
    }

    private Function<UserInterest, Interest> functionUserInterestInterestFunction() {
        return userInterest -> new Interest(userInterest.getId(), userInterest.getInterest());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}