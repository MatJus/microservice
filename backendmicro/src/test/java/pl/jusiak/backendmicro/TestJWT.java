package pl.jusiak.backendmicro;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
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
public class TestJWT {

  private Vertx vertx;
  private String validToken;
  private String invalidToken;

  @Before
  public void deploy_verticle(TestContext testContext) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle(), testContext.asyncAssertSuccess());


    JWTAuth jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
        .setPath("keystore.jceks")
        .setPassword("secret")));

    validToken = jwtAuth.generateToken(new JsonObject().put("login", "Mati"), new JWTOptions());
    invalidToken = validToken.substring(0, (validToken.length() - 7));
  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void shuld_successful_authenticate(TestContext testContext) throws Throwable {

    TestSuite suite = TestSuite.create("server_shuld_be_running");
    suite.test("", context -> {
      vertx.createHttpClient().request(HttpMethod.GET, 8888, "localhost", "/item", context.asyncAssertSuccess(req -> {
        req.putHeader("content-type", "application/json");
        req.putHeader("Authorization", "Bearer " + validToken);
        req.send(context.asyncAssertSuccess(resp -> {
          context.assertEquals(200, resp.statusCode());
          context.async().complete();
        }));
      }));
    });
  }

  @Test
  public void shuld_not_authenticate(TestContext testContext) throws Throwable {
    TestSuite suite = TestSuite.create("server_shuld_be_running");
    suite.test("", context -> {
      vertx.createHttpClient().request(HttpMethod.GET, 8888, "localhost", "/item", context.asyncAssertSuccess(req -> {
        req.putHeader("content-type", "application/json");
        req.putHeader("Authorization", "Bearer " + invalidToken);
        req.send(context.asyncAssertSuccess(resp -> {
          context.assertEquals(401, resp.statusCode());
          context.async().complete();
        }));
      }));
    });
  }
}
