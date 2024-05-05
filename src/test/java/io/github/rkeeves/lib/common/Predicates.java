package io.github.rkeeves.lib.common;

import java.util.function.Function;
import java.util.function.Predicate;

public class Predicates {

    public static <A extends Comparable<A>> Predicate<A> eq(A x) {
        return a -> a.compareTo(x) == 0;
    }

    public static <A extends Comparable<A>> Predicate<A> lt(A x) {
        return a -> a.compareTo(x) < 0;
    }

    public static <A extends Comparable<A>> Predicate<A> between(A min, A max) {
        return a -> min.compareTo(a) <= 0 && a.compareTo(max) <= 0;
    }

    public static <A extends Comparable<A>> Predicate<A> outside(A min, A max) {
        return between(min, max).negate();
    }

    public static <A, B> Predicate<A> contramap(Function<A, B> f, Predicate<B> p) {
        return a -> p.test(f.apply(a));
    }
}
