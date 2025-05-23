package com.cgtfarmer.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
public class MathServiceTest {

  @Inject
  private MathService mathService;

  @Test
  void test0() {
    assertEquals(7, this.mathService.add(5, 2));
  }
}
