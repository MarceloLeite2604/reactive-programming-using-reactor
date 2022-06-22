package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

  private final FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

  @Test
  void explorePublishOn() {

    final var flux = fluxAndMonoSchedulersService.explorePublishOn();

    StepVerifier.create(flux)
      .expectNextCount(6)
      .verifyComplete();
  }

  @Test
  void exploreSubscribeOn() {

    final var flux = fluxAndMonoSchedulersService.exploreSubscribeOn();

    StepVerifier.create(flux)
      .expectNextCount(6)
      .verifyComplete();
  }

  @Test
  void explorerParallel() {

    final var flux = fluxAndMonoSchedulersService.exploreParallel();

    StepVerifier.create(flux)
      .expectNextCount(3)
      .verifyComplete();
  }

  @Test
  void exploreParallelUsingFlatmap() {

    final var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatmap();

    StepVerifier.create(flux)
      .expectNextCount(3)
      .verifyComplete();
  }

  @Test
  void exploreParallelUsingFlatMap1() {

    final var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatmap1() ;

    StepVerifier.create(flux)
      .expectNextCount(6)
      .verifyComplete();
  }

  @Test
  void exploreParallelUsingFlatmapSequential() {
    final var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatmapSequential();

    StepVerifier.create(flux)
      .expectNext("ALEX", "BEN", "CHLOE")
      .verifyComplete();
  }
}
