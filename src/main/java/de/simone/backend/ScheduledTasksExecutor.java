/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */

package de.simone.backend;

import java.lang.management.ManagementFactory;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.simone.backend.llm.AiService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ScheduledTasksExecutor {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z",
            Locale.ENGLISH);

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String JSON = "application/json";

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    AiService aiService;

    @Scheduled(every = "P1D", delayed = "10m")
    public void oneDayTask() {

    }



    public void printStatistick() {
        Runtime runtime = Runtime.getRuntime();
        long startupTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        Log.infof(
                """
                        ------------------------------
                        Sitrone statistick:
                        ------------------------------
                        Startup time: %d Sec
                        Uptime: %d Sec
                        Memory used: %d MB
                        Memory max: %d MB
                        Available processors: %d
                        Java version: %s
                        """,
                startupTime, uptime,
                (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024),
                runtime.maxMemory() / (1024 * 1024),
                runtime.availableProcessors(),
                System.getProperty("java.version"));
    }

    private static Instant parseInstant(String timestamp) {
        // ISO-8601: 2025-11-26T13:50:00Z
        try {
            return Instant.parse(timestamp);
        } catch (Exception e) {
            //
        }

        // Tue, 25 Nov 2025 14:29:35 -0000
        try {
            OffsetDateTime odt = OffsetDateTime.parse(timestamp, formatter);
            return odt.toInstant();
        } catch (Exception e) {
            //
        }
        throw new IllegalArgumentException("the format '%s' could not be parsed to Instant");
    }

    public static void logTask(String message, long elements, long t1) {
        long time = System.currentTimeMillis() - t1;
        String message2 = message
                + " performed at "
                + new Date()
                + ". Processed elements: "
                + elements
                + ". Execution time: "
                + (time / 1000)
                + "s.";

            Log.info(message2);
    }

    public static boolean logApiError(HttpResponse<String> response, String apiName) {
        boolean isOk = true;
        if (response.statusCode() > 399) {
            Log.errorf("The Api '%s' returned a %d http code with response: %s",
                    apiName,
                    response.statusCode(),
                    response.body());
            isOk = false;
        }
        return isOk;
    }
}
