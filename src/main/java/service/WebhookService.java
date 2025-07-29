package com.bajaj.webhook.service;

import com.bajaj.webhook.model.WebhookRequest;
import com.bajaj.webhook.model.WebhookResponse;
import com.bajaj.webhook.model.SolutionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;


@Service
public class WebhookService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SqlSolutionService sqlSolutionService;


    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String TEST_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";


    public void processWebhookFlow() {
        try {
            System.out.println("Starting Bajaj Finserv Health Webhook Challenge...");
            System.out.println("============================================================");


            WebhookRequest request = new WebhookRequest("John Doe", "REG12347", "john@example.com");
            WebhookResponse response = generateWebhook(request);

            if (response == null) {
                System.err.println("Failed to generate webhook - terminating process");
                return;
            }

            System.out.println("Webhook generated successfully");
            System.out.println("Webhook URL: " + response.getWebhook());
            System.out.println("Access Token: " + response.getAccessToken().substring(0, 20) + "...");
            System.out.println();


            String regNo = request.getRegNo();
            String sqlSolution = sqlSolutionService.getSqlSolution(regNo);

            System.out.println("Registration Number: " + regNo);
            System.out.println("Last two digits: " + regNo.substring(regNo.length() - 2) +
                    " (" + (Integer.parseInt(regNo.substring(regNo.length() - 2)) % 2 == 1 ? "Odd" : "Even") +
                    " = Question " + (Integer.parseInt(regNo.substring(regNo.length() - 2)) % 2 == 1 ? "1" : "2") + ")");
            System.out.println();
            System.out.println("Generated SQL Solution:");
            System.out.println(sqlSolution);
            System.out.println();


            boolean submitted = submitSolution(sqlSolution, response.getAccessToken());

            if (submitted) {
                System.out.println("Solution submitted successfully!");
                System.out.println("Challenge completed! Check your submission status.");
                System.out.println("============================================================");
            } else {
                System.err.println("Failed to submit solution");
            }

        } catch (Exception e) {
            System.err.println("Error in webhook flow: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private WebhookResponse generateWebhook(WebhookRequest request) {
        try {
            System.out.println("Sending POST request to generate webhook...");
            System.out.println("Endpoint: " + GENERATE_WEBHOOK_URL);
            System.out.println("Request: " + request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                    GENERATE_WEBHOOK_URL, entity, WebhookResponse.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Webhook generation response: " + response.getStatusCode());
                return response.getBody();
            } else {
                System.err.println("Unexpected response status: " + response.getStatusCode());
                return null;
            }

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error generating webhook: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null;
        } catch (ResourceAccessException e) {
            System.err.println("Network error generating webhook: " + e.getMessage());
            System.err.println("Check your internet connection and try again");
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error generating webhook: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private boolean submitSolution(String sqlQuery, String accessToken) {
        try {
            System.out.println("Submitting SQL solution...");
            System.out.println("Endpoint: " + TEST_WEBHOOK_URL);
            System.out.println("Using JWT authentication");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    TEST_WEBHOOK_URL, entity, String.class);

            System.out.println("Submission response status: " + response.getStatusCode());
            System.out.println("Submission response body: " + response.getBody());

            return response.getStatusCode() == HttpStatus.OK;

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error submitting solution: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (ResourceAccessException e) {
            System.err.println("Network error submitting solution: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error submitting solution: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}