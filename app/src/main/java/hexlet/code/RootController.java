package hexlet.code;


import io.javalin.http.Handler;

public class RootController {
    public static Handler welcome = ctx -> {
        ctx.render("application.html");
    };
}
