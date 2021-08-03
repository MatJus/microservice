package pl.jusiak.backendmicro;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.TestSuite;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.jusiak.backendmicro.verticles.MainVerticle;

@RunWith(VertxUnitRunner.class)
public class TestMainVerticle {

  private Vertx vertx;

  @Before
  public void deploy_verticle(TestContext testContext) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle(), testContext.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void server_shuld_be_running(TestContext testContext) throws Throwable {
    //Async async = testContext.async();

    TestSuite suite = TestSuite.create("server_shuld_be_running");
    suite.test("", context -> {
      vertx.createHttpClient().request(HttpMethod.GET, 8888, "localhost", "/", context.asyncAssertSuccess(req -> {
        req.send(context.asyncAssertSuccess(resp -> {
          context.assertEquals(200, resp.statusCode());
        }));
      }));

      Async async = context.async();
      vertx.eventBus().consumer("user-actions", msg -> {
        async.complete();
      });
    });
  }
}
