package pl.jusiak.backendmicro;

import io.vertx.core.Vertx;
import pl.jusiak.backendmicro.verticles.MainVerticle;

public class Main {
  public static void main(String[] args) {
    Vertx vertix = Vertx.vertx();
    vertix.deployVerticle(new MainVerticle());
  }
}
