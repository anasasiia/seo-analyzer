package hexlet.code.controllers;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import hexlet.code.model.query.QUrlCheck;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

public class UrlController {
    public static Handler createUrl = ctx -> {
        String nameFromParam = ctx.formParam("url");

        String protocol;
        String authority;

        try {
            URL aURL = new URL(nameFromParam);
            protocol = aURL.getProtocol();
            authority = aURL.getAuthority();
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        String name = protocol + "://" + authority;
        Url url = new Url(name);

        boolean isExisted = new QUrl()
                .name.iequalTo(name)
                .exists();

        if (isExisted) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/urls");
            return;
        }

        url.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int rowsPerPage = 10;
        int offset = (page - 1) * rowsPerPage;

        PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(offset)
                .setMaxRows(rowsPerPage)
                .orderBy()
                    .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

        int lastPage = pagedUrls.getTotalPageCount() + 1;
        int currentPage = pagedUrls.getPageIndex() + 1;

        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed().toList();

        ctx.attribute("urls", urls);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("index.html");
    };

    public static Handler showUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        List<UrlCheck> checks = new QUrlCheck()
                .url.name.iequalTo(url.getName())
                .orderBy()
                    .id.desc()
                .findList();

        ctx.attribute("checks", checks);
        ctx.attribute("url", url);
        ctx.render("show.html");
    };

    public static Handler createCheck = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        try {
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
            UrlCheck urlCheck = new UrlCheck(title, h1, description, statusCode, url);
            urlCheck.save();

        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Страница не существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/urls/" + id);
            return;
        }

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + id);
    };
}
