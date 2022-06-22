package com.learnreactiveprogramming;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

class ColdAndHotPublisherTest {


  @Test
  void coldPublisherTest() {
    final var flux = Flux.range(1, 10);

    flux.subscribe(value -> System.out.println("Subscriber 1: " + value));

    flux.subscribe(value -> System.out.println("Subscriber 2: " + value));
  }

  @Test
  void hotPublisherTest() {
    final var flux = Flux.range(1, 10)
      .delayElements(Duration.ofSeconds(1));

    final var connectableFlux = flux.publish();

    // Makes this flux start behaving as a hot stream.
    connectableFlux.connect();

    connectableFlux.subscribe(value -> System.out.println("Subscriber 1: " + value));

    delay(4000);

    connectableFlux.subscribe(value -> System.out.println("Subscriber 2: " + value));

    delay(10000);
  }

  @Test
  void hotPublisherTestAutoConnect() {
    final var flux = Flux.range(1, 10)
      .delayElements(Duration.ofSeconds(1));

    final var hotSource = flux.publish().autoConnect(2);

    hotSource.subscribe(value -> System.out.println("Subscriber 1: " + value));
    delay(2000);

    hotSource.subscribe(value -> System.out.println("Subscriber 2: " + value));
    System.out.println("Two subscribers are connected.");
    delay(2000);

    hotSource.subscribe(value -> System.out.println("Subscriber 3: " + value));
    delay(10000);
  }

  @Test
  void hotPublisherTestRefCount() {
    final var flux = Flux.range(1, 10)
      .delayElements(Duration.ofSeconds(1))
      .doOnCancel(() -> System.out.println("Received cancel signal."));

    final var hotSource = flux.publish().refCount(2);

    final var firstDisposable = hotSource.subscribe(value -> System.out.println("Subscriber 1: " + value));
    delay(2000);

    final var secondDisposable = hotSource.subscribe(value -> System.out.println("Subscriber 2: " + value));
    System.out.println("Two subscribers are connected.");
    delay(2000);
    firstDisposable.dispose();
    secondDisposable.dispose();

    hotSource.subscribe(value -> System.out.println("Subscriber 3: " + value));
    delay(2000);
    hotSource.subscribe(value -> System.out.println("Subscriber 4: " + value));
    delay(10000);
  }
}
