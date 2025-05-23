package com.cgtfarmer.app;

import jakarta.inject.Singleton;

@Singleton
public final class MathService {

  public int add(int a, int b) {
    return a + b;
  }
}
