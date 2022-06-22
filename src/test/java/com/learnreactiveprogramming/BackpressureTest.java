package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class BackpressureTest {

  @Test
  void testBackPressure() {
    final var rangeFlux = Flux.range(1, 100)
      .log();

    rangeFlux
      .subscribe(new BaseSubscriber<>() {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
          request(2);
        }

        @Override
        protected void hookOnNext(Integer value) {
//          super.hookOnNext(value);
          log.info("hookOnNext: {}", value);

          if (value == 2) {
            cancel();
          }
        }

        @Override
        protected void hookOnComplete() {
//          super.hookOnComplete();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
//          super.hookOnError(throwable);
        }

        @Override
        protected void hookOnCancel() {
//          super.hookOnCancel();
          log.info("Inside cancel.");
        }
      });
    //.subscribe(number ->  log.info("Number is: {}", number));
  }

  @Test
  void testBackPressure1() throws Exception {
    final var rangeFlux = Flux.range(1, 100)
      .log();

    final var latch = new CountDownLatch(1);

    rangeFlux
      .subscribe(new BaseSubscriber<>() {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
          request(2);
        }

        @Override
        protected void hookOnNext(Integer value) {
//          super.hookOnNext(value);
          log.info("hookOnNext: {}", value);

          if (value % 2 == 0 || value < 50) {
            request(2);
          } else {
            cancel();
          }
        }

        @Override
        protected void hookOnComplete() {
//          super.hookOnComplete();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
//          super.hookOnError(throwable);
        }

        @Override
        protected void hookOnCancel() {
//          super.hookOnCancel();
          log.info("Inside cancel.");
          latch.countDown();
        }
      });

    assertTrue(latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  void testBackPressureDrop() throws Exception {
    final var rangeFlux = Flux.range(1, 100)
      .log();

    final var latch = new CountDownLatch(1);

    rangeFlux
      // Although BaseSubscriber hookOnSubscribe method only requests 2 elements, the request will be unbounded due to "onBackpressureDrop" method.
      // It acts as bridge between the publisher and the subscriber.
      .onBackpressureDrop(item -> {
        log.info("Dropped items is: {}", item);
      })
      .subscribe(new BaseSubscriber<>() {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
          request(2);
        }

        @Override
        protected void hookOnNext(Integer value) {
//          super.hookOnNext(value);
          log.info("hookOnNext: {}", value);

//          if (value % 2 == 0 || value < 50) {
//            request(2);
//          } else {
//            cancel();
//          }
          if (value == 2) {
            hookOnCancel();
          }
        }

        @Override
        protected void hookOnComplete() {
//          super.hookOnComplete();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
//          super.hookOnError(throwable);
        }

        @Override
        protected void hookOnCancel() {
//          super.hookOnCancel();
          log.info("Inside cancel.");
          latch.countDown();
        }
      });

    assertTrue(latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  void testBackPressureBuffer() throws Exception {
    final var rangeFlux = Flux.range(1, 100)
      .log();

    final var latch = new CountDownLatch(1);

    rangeFlux
      .onBackpressureBuffer(10, value -> {
        log.info("Last buffered element is: {}", value);
      })
      .subscribe(new BaseSubscriber<>() {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
          request(1);
        }

        @Override
        protected void hookOnNext(Integer value) {
//          super.hookOnNext(value);
          log.info("hookOnNext: {}", value);

          if (value < 50) {
            request(1);
          } else {
            hookOnCancel();
          }
        }

        @Override
        protected void hookOnComplete() {
//          super.hookOnComplete();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
//          super.hookOnError(throwable);
        }

        @Override
        protected void hookOnCancel() {
//          super.hookOnCancel();
          log.info("Inside cancel.");
          latch.countDown();
        }
      });

    assertTrue(latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  void testBackPressureError() throws Exception {
    final var rangeFlux = Flux.range(1, 100)
      .log();

    final var latch = new CountDownLatch(1);

    rangeFlux
      .onBackpressureError()
      .subscribe(new BaseSubscriber<>() {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
          request(1);
        }

        @Override
        protected void hookOnNext(Integer value) {
//          super.hookOnNext(value);
          log.info("hookOnNext: {}", value);

          if (value < 50) {
            request(1);
          } else {
            hookOnCancel();
          }
        }

        @Override
        protected void hookOnComplete() {
//          super.hookOnComplete();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
//          super.hookOnError(throwable);
          log.error("Exception is:", throwable);
        }

        @Override
        protected void hookOnCancel() {
//          super.hookOnCancel();
          log.info("Inside cancel.");
          latch.countDown();
        }
      });

    assertTrue(latch.await(5, TimeUnit.SECONDS));
  }
}
