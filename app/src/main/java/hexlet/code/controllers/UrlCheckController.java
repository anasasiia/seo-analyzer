package hexlet.code.controllers;

import hexlet.code.UrlCheck;
import io.javalin.http.Handler;

public class UrlCheckController {
    public static Handler createCheck = ctx -> {
        String title = "";
        String description = "";
        String h1 = "";
        int statusCode = 200;
        UrlCheck urlCheck = new UrlCheck(title, h1, description, statusCode);
        urlCheck.save();
        ctx.attribute("checks", urlCheck);
    };
}
