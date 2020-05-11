package su.svn.hiload.socialnetwork.utils;

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

public class InterestsCollectorToForm implements Collector<UserInterest, List<UserInterest>, ApplicationForm> {

    final ApplicationForm form;

    public InterestsCollectorToForm(ApplicationForm form) {
        this.form = form;
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
    public Function<List<UserInterest>, ApplicationForm> finisher() {
        return this::finisherApply;
    }

    private ApplicationForm finisherApply(List<UserInterest> userInterests) {
        List<Interest> interests = userInterests.stream()
                .map(functionUserInterestInterestFunction())
                .collect(Collectors.toList());
        form.setInterests(interests);

        return form;
    }

    private Function<UserInterest, Interest> functionUserInterestInterestFunction() {
        return userInterest -> new Interest(userInterest.getId(), userInterest.getInterest());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
