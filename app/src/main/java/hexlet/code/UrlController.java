package hexlet.code;

import hexlet.code.query.QUrl;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;

import java.net.URL;
import java.util.List;

public class UrlController {
    public static Handler createUrl = ctx -> {
        URL aURL = new URL(ctx.formParam("url"));
        String protocol = aURL.getProtocol();
        String authority = aURL.getAuthority();

        String name = protocol + "://" + authority;

        Url url = new Url(name);

        if (protocol.isEmpty() || authority.isEmpty()) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", url);
            ctx.render("");
            return;
        }

        boolean isExisted = new QUrl()
                .name.iequalTo(name)
                        .exists();

        if (isExisted) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", url);
            ctx.render("");
            return;
        }
        url.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    public static Handler listUrls = ctx -> {
        List<Url> urls = new QUrl().findList();

        ctx.attribute("urls", urls);
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

        ctx.attribute("url", url);
        ctx.render("show.html");
    };
}
