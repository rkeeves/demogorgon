package io.github.rkeeves.lib.arbitraries;

import io.github.rkeeves.lib.dto.Status;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ArbitrarySupplier;

import java.util.Arrays;
import java.util.Set;

public final class StatusString {

    public static class Valid implements ArbitrarySupplier<String> {
        @Override
        public Arbitrary<String> get() {
            return Arbitraries.of(Arrays.stream(Status.values()).map(Status::name).toList());
        }
    }

    public static class Invalid implements ArbitrarySupplier<String> {
        @Override
        public Arbitrary<String> get() {
            return Arbitraries.of(Set.of("SOME_ARBITRARY_NON_PARSEABLE_STATUS", "", "filterd", "99", "%"));
        }
    }
}
