package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoSchedulersService {

  static List<String> namesList = List.of("alex", "ben", "chloe");
  static List<String> namesList1 = List.of("adam", "jill", "jack");

  public Flux<String> explorePublishOn() {
    final var namesFlux = flux1(namesList)
      .publishOn(Schedulers.parallel())
      .log();

    final var namesFlux1 = flux1(namesList1)
      .publishOn(Schedulers.boundedElastic())
      .map(value -> {
        log.info("Value is: " + value);
        return value;
      })
      .log();

    return namesFlux.mergeWith(namesFlux1);
  }

  public Flux<String> exploreSubscribeOn() {
    final var namesFlux = flux1(namesList)
      .subscribeOn(Schedulers.boundedElastic())
      .log();

    final var namesFlux1 = flux1(namesList1)
      .subscribeOn(Schedulers.boundedElastic())
      .map(value -> {
        log.info("Value is: " + value);
        return value;
      })
      .log();

    return namesFlux.mergeWith(namesFlux1);
  }


  public ParallelFlux<String> exploreParallel() {

    final var noOrCores = Runtime.getRuntime()
      .availableProcessors();

    log.info("noOfCores: {}", noOrCores);

    return Flux.fromIterable(namesList)
      // "parallel" + "runOn" is one approach for parallelism in reactive programming.
      .parallel()
      .runOn(Schedulers.parallel())
//      .publishOn(Schedulers.parallel())
      .map(this::upperCase)
      .log();
  }

  public Flux<String> exploreParallelUsingFlatmap() {

    final var noOrCores = Runtime.getRuntime()
      .availableProcessors();

    log.info("noOfCores: {}", noOrCores);

    return Flux.fromIterable(namesList)
      // "flatMap" + "subscribeOn" is another approach for parallelism ins reactive programming.
      .flatMap(name ->
        Mono.just(name)
          .map(this::upperCase)
          .subscribeOn(Schedulers.parallel()))
      .log();
  }

  public Flux<String> exploreParallelUsingFlatmapSequential() {

    final var noOrCores = Runtime.getRuntime()
      .availableProcessors();

    log.info("noOfCores: {}", noOrCores);

    return Flux.fromIterable(namesList)
      .flatMapSequential(name ->
        Mono.just(name)
          .map(this::upperCase)
          .subscribeOn(Schedulers.parallel()))
      .log();
  }

  public Flux<String> exploreParallelUsingFlatmap1() {
    final var namesFlux = Flux.fromIterable(namesList)
      .flatMap(name ->
        Mono.just(name)
          .map(this::upperCase)
          .subscribeOn(Schedulers.parallel()))
      .log();

    final var namesFlux1 = Flux.fromIterable(namesList)
      .flatMap(name ->
        Mono.just(name)
          .map(this::upperCase)
          .subscribeOn(Schedulers.parallel()))
      .map(value -> {
        log.info("Value is: " + value);
        return value;
      })
      .log();

    return namesFlux.mergeWith(namesFlux1);
  }

  // This method creates a flux which uses a temporal function (delay), but is does not use a "publishOn" method.
  // This means that all the flux will be done on main thread at first sight.
  private Flux<String> flux1(List<String> namesList) {
    return Flux.fromIterable(namesList)
      .map(this::upperCase);
  }

  private String upperCase(String name) {
    delay(1000);
    return name.toUpperCase();
  }

}
