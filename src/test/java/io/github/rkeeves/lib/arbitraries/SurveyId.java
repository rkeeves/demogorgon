package io.github.rkeeves.lib.arbitraries;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ArbitrarySupplier;

public class SurveyId {

    public static final int CARDINALITY = 100;

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
