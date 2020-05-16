package su.svn.hiload.socialnetwork.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ListCollectorToSizeInteger<T> implements Collector<T, List<T>, Integer> {

    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (userInterests1, userInterests2) -> {
            userInterests1.addAll(userInterests2);
            return userInterests1;
        };
    }

    @Override
    public Function<List<T>, Integer> finisher() {
        return List::size;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
