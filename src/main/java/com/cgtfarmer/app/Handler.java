package com.cgtfarmer.app;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Handler extends
    MicronautRequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  @Inject
  private MathService mathService;

  @Override
  public APIGatewayV2HTTPResponse execute(APIGatewayV2HTTPEvent event) {
    String eventRouteKey = event.getRouteKey();

    APIGatewayV2HTTPResponse response;

    switch (eventRouteKey) {
      case "GET /health":
        log.info("Health Endpoint");

        response = APIGatewayV2HTTPResponse.builder()
            .withStatusCode(200)
            .withHeaders(Map.of("Content-Type", "application/json"))
            .withBody("{ \"message\": \"Healthy\"}")
            .build();

        break;

      case "POST /add":
        log.info("Add Endpoint");
        int result = this.mathService.add(5, 2);

        response = APIGatewayV2HTTPResponse.builder()
            .withStatusCode(200)
            .withHeaders(Map.of("Content-Type", "application/json"))
            .withBody(String.format("{ \"result\": \"%d\"}", result))
            .build();

        break;

      default:
        log.info("404 Endpoint");

        response = APIGatewayV2HTTPResponse.builder()
            .withStatusCode(404)
            .withHeaders(Map.of("Content-Type", "application/json"))
            .withBody("{ \"message\": \"Not Found\"}")
            .build();
    }

    return response;
  }
}
