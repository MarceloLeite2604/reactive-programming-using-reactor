package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Slf4j
public class FluxAndMonoGeneratorService {

  public Flux<String> namesFlux() {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"));
//      .log();
  }

  public Mono<String> nameMono() {
    return Mono.just("alex")
      .log();
  }

  public Flux<String> namesFluxMap() {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
      .map(String::toUpperCase)
      .doOnNext(name -> System.out.println("Name is " + name))
      .doOnSubscribe(subscription -> System.out.println("Subscription is " + subscription))
      .doOnComplete(() -> {
        System.out.println("Inside the complete callback.");
      })
      .doFinally(signalType -> System.out.println("Inside doFinally " + signalType));
  }

  public Flux<String> namesFluxFilter(int length) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
      .map(String::toUpperCase)
      .filter(name -> name.length() > length)
      .map(name -> name.length() + "-" + name);
  }

  public Flux<String> namesFluxImmutability() {
    final var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));

    namesFlux.map(String::toUpperCase);

    return namesFlux;
  }

  public Mono<String> namesMono_map_filter(int stringLength) {
    return Mono.just("alex")
      .map(String::toUpperCase)
      .filter(name -> name.length() > stringLength)
      .defaultIfEmpty("default")
      .log();
  }

  public Mono<String> namesMono_map_filter_switchIfEmpty(int stringLength) {

    final var defaultMono = Mono.just("default");

    return Mono.just("alex")
      .map(String::toUpperCase)
      .filter(name -> name.length() > stringLength)
      .switchIfEmpty(defaultMono)
      .log();
  }

  public Flux<String> exploreConcat() {
    final var abcFlux = Flux.just("A", "B", "C");
    final var defFlux = Flux.just("D", "E", "F");

    // Concat subscribes on publishers one at a time.
    return Flux.concat(abcFlux, defFlux)
      .log();
  }

  public Flux<String> exploreConcatWith() {
    final var abcFlux = Flux.just("A", "B", "C");
    final var defFlux = Flux.just("D", "E", "F");

    return abcFlux.concatWith(defFlux)
      .log();
  }

  public Flux<String> exploreConcatWithMono() {
    final var abcMono = Flux.just("A");
    final var defMono = Flux.just("D");

    return abcMono.concatWith(defMono)
      .log();
  }

  public Flux<String> exploreMerge() {
    final var abcFlux = Flux.just("A", "B", "C")
      .delayElements(Duration.ofMillis(100));
    final var defFlux = Flux.just("D", "E", "F")
      .delayElements(Duration.ofMillis(125));
    // Merge subscribes on all publishers and consumes them as values are sent.
    return Flux.merge(abcFlux, defFlux)
      .log();
  }

  public Flux<String> exploreMergeWith() {
    final var abcFlux = Flux.just("A", "B", "C")
      .delayElements(Duration.ofMillis(100));
    final var defFlux = Flux.just("D", "E", "F")
      .delayElements(Duration.ofMillis(125));
    // Merge subscribes on all publishers and consumes them as values are sent.
    return abcFlux.mergeWith(defFlux)
      .log();
  }

  public Flux<String> exploreMergeWithMono() {
    final var aFlux = Mono.just("A");
    final var bFlux = Mono.just("B");

    return aFlux.mergeWith(bFlux)
      .log();
  }

  public Flux<String> exploreMergeSequential() {
    final var abcFlux = Flux.just("A", "B", "C")
      .delayElements(Duration.ofMillis(150))
      .log();

    final var defFlux = Flux.just("D", "E", "F")
      .delayElements(Duration.ofMillis(125))
      .log();

    /*
     * MergeSequential is in-between concat and merge. While still subscribing on
     * all publishers at the same time and accepting values from all sources, it
     * has a final step to order elements according to the subscription order.
     */
    return Flux.mergeSequential(abcFlux, defFlux)
      .log();
  }

  public Flux<String> exploreZip() {
    final var abcFlux = Flux.just("A", "B", "C");

    final var defFlux = Flux.just("D", "E", "F");

    return Flux.zip(abcFlux, defFlux, (first, second) -> first + second)
      .log();
  }

  public Flux<String> exploreZip1() {
    final var abcFlux = Flux.just("A", "B", "C");

    final var defFlux = Flux.just("D", "E", "F");

    final var _123Flux = Flux.just("1", "2", "3");

    final var _456Flux = Flux.just("4", "5", "6");

    return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
      .map(tuple4 -> tuple4.getT1() + tuple4.getT2() + tuple4.getT3() + tuple4.getT4())
      .log();
  }

  public Flux<String> exploreZipWith() {
    final var abcFlux = Flux.just("A", "B", "C");

    final var defFlux = Flux.just("D", "E", "F");

    return abcFlux.zipWith(defFlux, (first, second) -> first + second)
      .log();
  }

  public Mono<String> exploreZipWithMono() {
    final var aFlux = Mono.just("A");
    final var bFlux = Mono.just("B");

    return aFlux.zipWith(bFlux)
      .map(tuple2 -> tuple2.getT1() + tuple2.getT2())
      .log();
  }

  public Flux<String> exceptionFlux() {
    return Flux.just("A", "B", "C")
      // Once an exception is received, the subscription is cancelled. This means that no event will be sent right after the exception.
      .concatWith(Flux.error(new RuntimeException("Exception occurred")))
      .concatWith(Flux.just("D"))
      .log();
  }

  public Flux<String> exploreOnErrorReturn() {
    return Flux.just("A", "B", "C")
      // Once an exception is received, the subscription is cancelled. This means that no event will be sent right after the exception.
      .concatWith(Flux.error(new IllegalStateException("Exception occurred")))
      .onErrorReturn("D")
      .log();
  }

  public Flux<String> exploreOnErrorResume(Exception exception) {

    final var recoveryFlux = Flux.just("D", "E", "F");

    return Flux.just("A", "B", "C")
      // Once an exception is received, the subscription is cancelled. This means that no event will be sent right after the exception.
      .concatWith(Flux.error(exception))
      .onErrorResume(pipeException -> {
        log.error("Exception is ", pipeException);
        if (pipeException instanceof IllegalStateException) {
          return recoveryFlux;
        } else {
          return Flux.error(pipeException);
        }
      })
      .log();
  }

  public Flux<String> exploreOnErrorContinue() {
    return Flux.just("A", "B", "C")
      .map(value -> {
        if ("B".equals(value)) {
          throw new IllegalStateException("Exception occurred.");
        }

        return value;
      })
      .concatWith(Flux.just("D"))
      .onErrorContinue((exception, value) -> {
        log.error("Exception is ", exception);
        ;
        log.info("Value is \"{}\".", value);
        ;
      })
      .log();
  }

  public Flux<String> exploreOnErrorMap() {
    return Flux.just("A", "B", "C")
      .map(value -> {
        if ("B".equals(value)) {
          throw new IllegalStateException("Exception occurred.");
        }

        return value;
      })
      .concatWith(Flux.just("D"))
      .onErrorMap(pipelineException -> {
        log.error("Exception is ", pipelineException);
        ;
        return new ReactorException(pipelineException, pipelineException.getMessage());
      })
      .log();
  }

  public Flux<String> exploreDoOnError() {
    return Flux.just("A", "B", "C")
      // Once an exception is received, the subscription is cancelled. This means that no event will be sent right after the exception.
      .concatWith(Flux.error(new IllegalStateException("Exception occurred")))
      .doOnError(exception -> log.error("Exception is " + exception))
      .log();
  }

  public Mono<Object> exploreMonoOnErrorReturn() {
    return Mono.just("A")
      .map(value -> {
        throw new RuntimeException("Exception occurred");
      })
      .onErrorReturn("abc")
      .log();
  }

  public Mono<Object> exploreMonoOnErrorMap(Exception exception) {
    return Mono.just("A")
      .map(value -> {
        throw new RuntimeException("Exception occurred");
      })
      .onErrorMap(pipelineException -> {
        log.error("Exception is ", pipelineException);
        return exception;
      })
      .log();
  }

  public Mono<String> explore_mono_onErrorContinue(String value) {
    return Mono.just(value)
      .map(pipelineValue -> {
        if ("abc".equals(pipelineValue)) {
          throw new RuntimeException("Exception occurred");
        }
        return pipelineValue;
      })
      .onErrorContinue((exception, pipelineValue ) -> {
        log.error("Exception is " + exception);
        log.info("Value is {}.", pipelineValue);
      })
      .log();
  }

  public Flux<String> namesFluxFlatMap(int length) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
      .map(String::toUpperCase)
      .filter(name -> name.length() > length)
      .flatMap(this::splitString);
  }

  public Flux<String> namesFluxFlatMapAsync(int length) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
      .map(String::toUpperCase)
      .filter(name -> name.length() > length)
      // FlatMap does not guarantee elements order.
      .flatMap(this::splitStringWithDelay)
      .log();
  }

  public Flux<String> namesFluxConcatMap(int length) {

    // We can reuse this functionality on other pipelines.
    final UnaryOperator<Flux<String>> filterMap = name -> name.map(String::toUpperCase)
      .filter(s -> s.length() > length);

    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
      .transform(filterMap)
      // ConcatMap preserves element order, but it takes longer to resolve.
      .concatMap(this::splitStringWithDelay)
      .log();
  }

  public Flux<String> namesFluxTransform(int length) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
      .map(String::toUpperCase)
      .filter(name -> name.length() > length)
      .flatMap(this::splitString)
      .defaultIfEmpty("default");
  }

  public Flux<String> namesFluxSwitchIfEmpty(int length) {

    final UnaryOperator<Flux<String>> filterMap = name -> name.map(String::toUpperCase)
      .filter(s -> s.length() > length)
      .flatMap(this::splitString);

    final var defaultFlux = Flux.just("default")
      .transform(filterMap);

    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
      .transform(filterMap)
      .switchIfEmpty(defaultFlux)
      .log();
  }

  public Flux<String> splitString(String value) {
    return Flux.fromArray(value.split(""));
  }

  public Flux<String> splitStringWithDelay(String value) {
//    final var delayMillis = new SecureRandom().nextInt(1000);
    final var delayMillis = 1000;
    return Flux.fromArray(value.split(""))
      .delayElements(Duration.ofMillis(delayMillis));
  }

  public Mono<List<String>> namesMonoFlatMap(int stringLength) {
    return Mono.just("alex")
      .map(String::toUpperCase)
      .filter(name -> name.length() > stringLength)
      .flatMap(this::splitStringMono);
  }

  public Flux<String> namesMonoFlatMapMany(int stringLength) {
    return Mono.just("alex")
      .map(String::toUpperCase)
      .filter(name -> name.length() > stringLength)
      .flatMapMany(this::splitString);
  }

  private Mono<List<String>> splitStringMono(String value) {
    return Mono.just(List.of(value.split("")));
  }

  public static void main(String[] args) {
    final var fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    fluxAndMonoGeneratorService.namesFlux()
      .subscribe(name -> System.out.println("Flux name: " + name));

    fluxAndMonoGeneratorService.nameMono()
      .subscribe(name -> System.out.println("Mono name: " + name));
  }
}
