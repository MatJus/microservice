package pl.jusiak.backendmicro.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class HttpVerticle extends AbstractVerticle {

  private JWTAuth jwtAuth;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
      .setPath("keystore.jceks")
      .setPassword("secrets")));

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
  }

  private void registerUser(RoutingContext routingContext) {
  }

  private void addItem(RoutingContext routingContext) {
  }

  private void getItems(RoutingContext routingContext) {
  }

}
