package io.github.rkeeves.lib.common;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sets {

    public static <A, B> Set<B> setBy(Function<A, B> f, List<A> xs) {
        return xs.stream().map(f).collect(Collectors.toSet());
    }

    public static <A> Optional<A> oneFromIntersection(Set<A> as, Set<A> bs) {
        return as.stream().filter(bs::contains).findAny();
    }
}
