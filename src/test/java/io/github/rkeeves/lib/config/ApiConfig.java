package io.github.rkeeves.lib.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:api-config.properties"})
public interface ApiConfig extends Config {
    String baseUri();

    static ApiConfig readIO() {
        return ConfigFactory.create(ApiConfig.class);
    }
}