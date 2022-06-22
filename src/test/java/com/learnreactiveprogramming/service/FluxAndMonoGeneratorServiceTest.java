package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.List;

class FluxAndMonoGeneratorServiceTest {

  private final FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

  @Test
  void namesFlux() {
    final var namesFlux = fluxAndMonoGeneratorService.namesFlux();

    StepVerifier.create(namesFlux)
      //.expectNext("alex", "ben", "chloe")
      .expectNextCount(3)
      .verifyComplete();
  }

  @Test
  void nameMono() {
    final var nameMono = fluxAndMonoGeneratorService.nameMono();

    StepVerifier.create(nameMono)
      .expectNext("alex")
      .verifyComplete();
  }

  @Test
  void namesFluxMap() {
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxMap();

    StepVerifier.create(namesFlux)
      .expectNext("ALEX", "BEN", "CHLOE")
      .verifyComplete();
  }

  @Test
  void namesFluxImmutability() {
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxImmutability();

    StepVerifier.create(namesFlux)
      .expectNext("alex", "ben", "chloe")
      .verifyComplete();
  }

  @Test
  void namesFluxFilter() {
    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxFilter(length);

    StepVerifier.create(namesFlux)
      .expectNext("4-ALEX", "5-CHLOE")
      .verifyComplete();
  }

  @Test
  void namesMono_map_filter() {
    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesMono_map_filter(length);

    StepVerifier.create(namesFlux)
      .expectNext("ALEX")
      .verifyComplete();
  }

  @Test
  void namesMono_map_filter_defaultIfEmpty() {
    final var length = 4;
    final var namesFlux = fluxAndMonoGeneratorService.namesMono_map_filter(length);

    StepVerifier.create(namesFlux)
      .expectNext("default")
      .verifyComplete();
  }

  @Test
  void namesMono_map_filter_switchIfEmpty() {
    final var length = 4;
    final var namesFlux = fluxAndMonoGeneratorService.namesMono_map_filter_switchIfEmpty(length);

    StepVerifier.create(namesFlux)
      .expectNext("default")
      .verifyComplete();
  }

  @Test
  void namesFluxFlatMap() {
    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(length);

    StepVerifier.create(namesFlux)
      .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
      .verifyComplete();
  }

  @Test
  void namesFluxFlatMapAsync() {
    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(length);

    StepVerifier.create(namesFlux)
      .expectNextCount(9)
      .verifyComplete();
  }

  @Test
  void namesFluxConcatMap() {
    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMap(length);

    StepVerifier.create(namesFlux)
      .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
      .verifyComplete();
  }

  @Test
  void namesFluxConcatMapVirtualTimer() {

    // Creates a virtual clock, allowing time to be manipulated on tests.
    VirtualTimeScheduler.getOrSet();

    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMap(length);

    // Will execute tests using a virtual clock.
    StepVerifier.withVirtualTime(() -> namesFlux)
      // Awaits the specified amount of time. It is a good practice to use a value higher than expected.
      .thenAwait(Duration.ofSeconds(10))
      .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
      .verifyComplete();
  }

  @Test
  void namesMonoFlatMap() {
    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMap(length);

    StepVerifier.create(namesFlux)
      .expectNext(List.of("A", "L", "E", "X"))
      .verifyComplete();
  }

  @Test
  void namesMonoFlatMapMany() {
    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMapMany(length);

    StepVerifier.create(namesFlux)
      .expectNext("A", "L", "E", "X")
      .verifyComplete();
  }

  @Test
  void namesFluxTransform() {
    final var length = 3;
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(length);

    StepVerifier.create(namesFlux)
      .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
      .verifyComplete();
  }

  @Test
  void namesFluxTransform1() {
    final var length = 6;
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(length);

    StepVerifier.create(namesFlux)
      .expectNext("default")
      .verifyComplete();
  }

  @Test
  void namesFluxTransformSwitchIfEmpty() {
    final var length = 6;
    final var namesFlux = fluxAndMonoGeneratorService.namesFluxSwitchIfEmpty(length);

    StepVerifier.create(namesFlux)
      .expectNext("D", "E", "F", "A", "U", "L", "T")
      .verifyComplete();
  }

  @Test
  void exploreConcat() {
    final var concatFlux = fluxAndMonoGeneratorService.exploreConcat();

    StepVerifier.create(concatFlux)
      .expectNext("A", "B", "C", "D", "E", "F")
      .verifyComplete();
  }

  @Test
  void exploreConcatWith() {
    final var concatFlux = fluxAndMonoGeneratorService.exploreConcatWith();

    StepVerifier.create(concatFlux)
      .expectNext("A", "B", "C", "D", "E", "F")
      .verifyComplete();
  }

  @Test
  void exploreConcatWithMono() {
    final var concatFlux = fluxAndMonoGeneratorService.exploreConcatWithMono();

    StepVerifier.create(concatFlux)
      .expectNext("A", "D")
      .verifyComplete();
  }

  @Test
  void exploreMerge() {
    final var value = fluxAndMonoGeneratorService.exploreMerge();

    StepVerifier.create(value)
      .expectNext("A", "D", "B", "E", "C", "F")
      .verifyComplete();
  }

  @Test
  void explore_mergeWith() {
    final var value = fluxAndMonoGeneratorService.exploreMergeWith();

    StepVerifier.create(value)
      .expectNext("A", "D", "B", "E", "C", "F")
      .verifyComplete();
  }

  @Test
  void explore_mergeWithMono() {
    final var value = fluxAndMonoGeneratorService.exploreMergeWithMono();

    StepVerifier.create(value)
      .expectNext("A", "B")
      .verifyComplete();
  }

  @Test
  void exploreMergeSequential() {
    final var value = fluxAndMonoGeneratorService.exploreMergeSequential();

    StepVerifier.create(value)
      .expectNext("A", "B", "C", "D", "E", "F")
      .verifyComplete();
  }

  @Test
  void exploreZip() {
    final var value = fluxAndMonoGeneratorService.exploreZip();

    StepVerifier.create(value)
      .expectNext("AD", "BE", "CF")
      .verifyComplete();
  }

  @Test
  void exploreZip1() {
    final var value = fluxAndMonoGeneratorService.exploreZip1();

    StepVerifier.create(value)
      .expectNext("AD14", "BE25", "CF36")
      .verifyComplete();
  }

  @Test
  void explore_zipWith() {
    final var value = fluxAndMonoGeneratorService.exploreZipWith();

    StepVerifier.create(value)
      .expectNext("AD", "BE", "CF")
      .verifyComplete();
  }

  @Test
  void explore_zipWithMono() {
    final var value = fluxAndMonoGeneratorService.exploreZipWithMono();

    StepVerifier.create(value)
      .expectNext("AB")
      .verifyComplete();
  }

  @Test
  void exceptionFlux() {
    final var flux = fluxAndMonoGeneratorService.exceptionFlux();

    StepVerifier.create(flux)
      .expectNext("A", "B", "C")
      .expectError(RuntimeException.class)
      .verify();
  }

  @Test
  void exceptionFlux1() {
    final var flux = fluxAndMonoGeneratorService.exceptionFlux();

    StepVerifier.create(flux)
      .expectNext("A", "B", "C")
      .expectError()
      .verify();
  }

  @Test
  void exceptionFlux2() {
    final var flux = fluxAndMonoGeneratorService.exceptionFlux();

    StepVerifier.create(flux)
      .expectNext("A", "B", "C")
      .expectErrorMessage("Exception occurred")
      .verify();
  }

  @Test
  void exploreOnErrorReturn() {
    final var flux = fluxAndMonoGeneratorService.exploreOnErrorReturn();

    StepVerifier.create(flux)
      .expectNext("A", "B", "C", "D")
      .verifyComplete();
  }

  @Test
  void exploreOnErrorResume() {
    final var exception = new IllegalStateException("Not a valid state");

    final var flux = fluxAndMonoGeneratorService.exploreOnErrorResume(exception);

    StepVerifier.create(flux)
      .expectNext("A", "B", "C", "D", "E", "F")
      .verifyComplete();
  }

  @Test
  void exploreOnErrorResume1() {
    final var exception = new RuntimeException("Runtime exception");

    final var flux = fluxAndMonoGeneratorService.exploreOnErrorResume(exception);

    StepVerifier.create(flux)
      .expectNext("A", "B", "C")
      .expectError(RuntimeException.class)
      .verify();
  }

  @Test
  void exploreOnErrorContinue() {

    final var flux = fluxAndMonoGeneratorService.exploreOnErrorContinue();

    StepVerifier.create(flux)
      .expectNext("A", "C", "D")
      .verifyComplete();
  }

  @Test
  void exploreOnErrorMap() {

    final var flux = fluxAndMonoGeneratorService.exploreOnErrorMap();

    StepVerifier.create(flux)
      .expectNext("A")
      .expectError(ReactorException.class)
      .verify();
  }

  @Test
  void exploreDoOnError() {

    final var flux = fluxAndMonoGeneratorService.exploreDoOnError();

    StepVerifier.create(flux)
      .expectNext("A", "B", "C")
      .expectError(IllegalStateException.class)
      .verify();
  }

  @Test
  void exploreMonoOnErrorReturn() {

    final var flux = fluxAndMonoGeneratorService.exploreMonoOnErrorReturn();

    StepVerifier.create(flux)
      .expectNext("abc")
      .verifyComplete();

  }

  @Test
  void exploreMonoOnErrorMap() {
    final var exception = new IllegalStateException("Illegal state exception occurred");

    final var flux = fluxAndMonoGeneratorService.exploreMonoOnErrorMap(exception);

    StepVerifier.create(flux)
      .expectError(IllegalStateException.class)
      .verify();
  }

  @Test
  void explore_mono_onErrorContinue() {
    final var input = "abc";

    final var flux = fluxAndMonoGeneratorService.explore_mono_onErrorContinue(input);

    StepVerifier.create(flux)
      .verifyComplete();
  }

  @Test
  void explore_mono_onErrorContinue1() {
    final var input = "reactor";

    final var flux = fluxAndMonoGeneratorService.explore_mono_onErrorContinue(input);

    StepVerifier.create(flux)
      .expectNext("reactor")
      .verifyComplete();
  }
}
