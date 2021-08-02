package pl.jusiak.backendmicro.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import pl.jusiak.backendmicro.verticles.dav.ItemDAV;
import pl.jusiak.backendmicro.verticles.dav.UserDAV;

import java.util.Arrays;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Promise<String> HttpVerticlePromise = Promise.promise();
    Promise<String> UserDAVPromise = Promise.promise();
    Promise<String> ItemDAVPromise = Promise.promise();

    vertx.deployVerticle(new HttpVerticle(), HttpVerticlePromise);
    vertx.deployVerticle(new UserDAV(), UserDAVPromise);
    vertx.deployVerticle(new ItemDAV(), ItemDAVPromise);

    CompositeFuture.all(Arrays.asList(HttpVerticlePromise.future(),
      UserDAVPromise.future(),
      ItemDAVPromise.future()))
      .onComplete(ar -> {
        if (ar.succeeded()) {
          startPromise.complete();
        } else {
          startPromise.fail(ar.cause().getMessage());
        }
      });
  }
}
