/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone;

import java.time.Duration;
import java.util.TimeZone;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;

@Push(value = PushMode.DISABLED)
@Theme("vaadin+")
@NpmPackage(value = "@vaadin-component-factory/vcf-nav", version = "1.0.6")
@PWA(name = "Sauer Sitrone", shortName = "Sitrone", description = "Ihre personal Tamagotchi Fruend", offlinePath = "offline-page.html")
public class Sitrone implements AppShellConfigurator {

    @ConfigProperty(name = "quarkus.hibernate-orm.jdbc.timezone")
    String jdbcTimezone;

    private static Sitrone sitrone;

    public static void main(String... args) {
        Quarkus.run(args);
    }

    public static Sitrone getInstance() {
        return sitrone;
    }

    @PostConstruct
    void init() {
        sitrone = this; // NOSONAR
        TimeZone.setDefault(TimeZone.getTimeZone(jdbcTimezone));
    }

    void onStart(@Observes StartupEvent ev, Router router) {
        router.route()
                .method(HttpMethod.GET).method(HttpMethod.HEAD)
                .path("/media/*")
                .handler(StaticHandler.create("media/"));
    }
}
