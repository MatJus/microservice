package pl.jusiak.backendmicro.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import pl.jusiak.backendmicro.model.User;

public class HttpVerticle extends AbstractVerticle {

  private JWTAuth jwtAuth;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
      .setPath("keystore.jceks")
      .setPassword("secret")));

    Router router = Router.router(vertx);
    router.get("/")
      .handler(ctx -> {
        ctx.response()
          .setStatusCode(200)
          .putHeader("content-type", "text/plain")
          .end("Welcome!");
      });

    router.route("/login*").handler(BodyHandler.create());
    router.post("/login")
      .consumes("*/json")
      .handler(this::loginUser);

    router.route("/register*").handler(BodyHandler.create());
    router.post("/register")
      .consumes("*/json")
      .handler(this::registerUser);

    router.route("/items*").handler(BodyHandler.create());
    router.post("/items")
      .consumes("*/json")
      .handler(JWTAuthHandler.create(jwtAuth))
      .handler(this::addItem);

    router.get("/items")
      .handler(JWTAuthHandler.create(jwtAuth))
      .handler(this::getItems);

    vertx.createHttpServer(new HttpServerOptions())
      .requestHandler(router)
      .listen(8888, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port 8888");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private void loginUser(RoutingContext routingContext) {

    JsonObject userLogPassJson = routingContext.getBodyAsJson().getJsonObject("user");

    final User user = new User(userLogPassJson.getString("login"),
      userLogPassJson.getString("password"));

    JsonObject authInfo = new JsonObject()
      .put("username", user.getLogin())
      .put("password", user.getPassword());

    JsonObject message = new JsonObject()
      .put("action", "login-user")
      .put("authinfo", authInfo);

    vertx.eventBus().request("user-actions", message, ar -> {
      if (ar.succeeded()) {
        JsonObject returnedUserJson = ((JsonObject) ar.result().body()).getJsonObject("user");
        final User returnedUser = new User(returnedUserJson);
        String token = jwtAuth.generateToken(new JsonObject()
          .put("login", returnedUser.getLogin())
          .put("_id", returnedUser.getId().toString()), new JWTOptions().setIgnoreExpiration(true));
        routingContext.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(new JsonObject().put("access_token", token)));
      } else {

        routingContext.response()
          .setStatusCode(500)
          .putHeader("content-type", "text/plain; charset=utf-8")
          .end("Login failure: " + ar.cause().getMessage());
      }
    });
  }

  private void registerUser(RoutingContext routingContext) {

    JsonObject userLogPassJson = routingContext.getBodyAsJson().getJsonObject("user");

    JsonObject message = new JsonObject()
      .put("action", "register-user")
      .put("user", userLogPassJson);

    vertx.eventBus().request("user-actions", message, ar -> {
      if (ar.succeeded()) {
        routingContext.response()
          .setStatusCode(201)
          .putHeader("content-type", "text/plain; charset=utf-8")
          .end("User created!");
      } else {
        routingContext.response()
          .setStatusCode(500)
          .putHeader("content-type", "text/plain; charset=utf-8")
          .end("Registration failure:" + ar.cause().getMessage());
      }
    });
  }

  private void addItem(RoutingContext routingContext) {

    JsonObject itemJson = routingContext.getBodyAsJson().getJsonObject("item");

    JsonObject message = new JsonObject()
      .put("action", "add-item")
      .put("item", itemJson)
      .put("owner", routingContext.user().get("_id"));

    vertx.eventBus().request("items-actions", message, ar -> {
      if (ar.succeeded()) {
        routingContext.response()
          .setStatusCode(201)
          .putHeader("content-type", "text/plain; charset=utf-8")
          .end("Item added!");
      } else {
        routingContext.response()
          .setStatusCode(500)
          .putHeader("content-type", "text/plain; charset=utf-8")
          .end("Insert failure: " + ar.cause().getMessage());
      }
    });
  }

  private void getItems(RoutingContext routingContext) {

    JsonObject message = new JsonObject()
      .put("action", "get-items")
      .put("owner", routingContext.user().get("_id"));

    vertx.eventBus().request("items-actions", message, ar -> {
      if (ar.succeeded()) {
        routingContext.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(ar.result().body()));
      } else {
        routingContext.response()
          .setStatusCode(500)
          .putHeader("content-type", "text/plain; charset=utf-8")
          .end("Wiadomość zwrotna: " + ar.cause().getMessage());
      }
    });
  }
}
