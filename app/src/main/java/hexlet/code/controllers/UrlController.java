package hexlet.code.controllers;

import hexlet.code.Url;
import hexlet.code.UrlCheck;
import hexlet.code.query.QUrl;
import hexlet.code.query.QUrlCheck;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

public class UrlController {
    public static Handler createUrl = ctx -> {
        String nameFromParam = ctx.formParam("url");

//        if (nameFromParam.isEmpty()) {
//            ctx.sessionAttribute("flash", "Некорректный URL");
//            ctx.sessionAttribute("flash-type", "danger");
//            ctx.redirect("/");
//        }
//
        String protocol = "";
        String authority = "";

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
}
