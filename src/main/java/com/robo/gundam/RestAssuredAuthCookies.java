package com.robo.gundam;
import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RestAssuredAuthCookies {
    public static void main(String[] args) {
    	
    	System.out.println("hello world!");
        // Set base URI
        RestAssured.baseURI = "https://example.com/api";

        // Step 1: Send login request and capture cookies
        Response loginResponse = given()
                .contentType("application/json")
                .body("{ \"username\": \"testUser\", \"password\": \"testPass\" }")
                .when()
                .post("/login")  // Replace with actual login endpoint
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Extract cookies from response
        Cookies cookies = loginResponse.getDetailedCookies();
        System.out.println("Captured Cookies: " + cookies);

        // Step 2: Use cookies in subsequent requests
        Response securedResponse = given()
                .cookies(cookies) // Reuse authentication cookies
                .when()
                .get("/secure-endpoint")  // Replace with actual secure API
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("Response from secured endpoint: " + securedResponse.asString());
    }
}
