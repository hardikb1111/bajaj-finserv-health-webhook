package com.bajaj.webhook.runner;

import com.bajaj.webhook.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class WebhookRunner implements CommandLineRunner {

    @Autowired
    private WebhookService webhookService;


    @Override
    public void run(String... args) throws Exception {
        System.out.println();
        System.out.println("BAJAJ FINSERV HEALTH - WEBHOOK CHALLENGE");
        System.out.println("Date: " + java.time.LocalDateTime.now());
        System.out.println("Running automatically on application startup...");
        System.out.println();

        webhookService.processWebhookFlow();

        System.out.println();
        System.out.println("Application completed successfully!");
        System.out.println("Check the output above for submission results.");

        System.exit(0);
    }
}