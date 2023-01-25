package hexlet.code.controllers;

import hexlet.code.Url;
import hexlet.code.UrlCheck;
import hexlet.code.query.QUrl;
import io.javalin.http.Handler;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class UrlCheckController {
    public static Handler createCheck = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        HttpResponse<String> response = Unirest.get(url.getName()).asString();
        String html = response.getBody();
        Document doc = Jsoup.parse(html);

        String title = "";
        String titleParsed = doc.title();

        if (!titleParsed.isEmpty()) {
            title = titleParsed;
        }

        String h1 = "";
        Element h1Parsed = doc.selectFirst("h1");

        if (h1Parsed != null) {
            h1 = h1Parsed.text();
        }

        String description = "";
        Element descriptionParsed = doc.selectFirst("meta[name=description]");

        if (descriptionParsed != null) {
            description = descriptionParsed.attr("content");
        }

        int statusCode = response.getStatus();
        UrlCheck urlCheck = new UrlCheck(title, h1, description, statusCode);
        urlCheck.save();

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + id);
    };
}
