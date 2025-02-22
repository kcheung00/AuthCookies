package com.robo.gundam;
import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;

public class RestAssuredAuthCookies {
    public static Cookies authenticateAndGetCookies() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        String loginPayload = "{\"username\": \"admin\", \"password\": \"password123\"}";
        
        Response response = RestAssured.given()
            .contentType("application/json")
            .body(loginPayload)
            .post("/auth")
            .then()
            .statusCode(200)
            .extract()
            .response();
        
        return response.getDetailedCookies();
    }

    public static void useCookiesInPlaywright(Cookies restAssuredCookies) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();

            List<Cookie> playwrightCookies = new ArrayList<>();
            for (io.restassured.http.Cookie raCookie : restAssuredCookies) {
                Cookie cookie = new Cookie(raCookie.getName(), raCookie.getValue());
                cookie.domain = raCookie.getDomain() != null ? raCookie.getDomain() : ".herokuapp.com";
                cookie.path = raCookie.getPath() != null ? raCookie.getPath() : "/";
                cookie.expires = raCookie.getExpiryDate() != null ? raCookie.getExpiryDate().getTime() / 1000.0 : -1;
                cookie.httpOnly = raCookie.isHttpOnly();
                cookie.secure = raCookie.isSecured();
                playwrightCookies.add(cookie);
            }

            context.addCookies(playwrightCookies);
            Page page = context.newPage();
            page.navigate("https://restful-booker.herokuapp.com");
            System.out.println("Page title: " + page.title());

            Thread.sleep(5000); // Optional: Inspect manually
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Cookies cookies = authenticateAndGetCookies();
        useCookiesInPlaywright(cookies);
    }}
