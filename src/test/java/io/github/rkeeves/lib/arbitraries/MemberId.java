package io.github.rkeeves.lib.arbitraries;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ArbitrarySupplier;

public final class MemberId {

    public static final int CARDINALITY = 300;

    public static class NonExisting implements ArbitrarySupplier<Integer> {
        @Override
        public Arbitrary<Integer> get() {
            return  Arbitraries.integers().greaterOrEqual(CARDINALITY + 1);
        }
    }

    public static class Existing implements ArbitrarySupplier<Integer> {
        @Override
        public Arbitrary<Integer> get() {
            return  Arbitraries.integers().between(1, CARDINALITY);
        }
    }
}
