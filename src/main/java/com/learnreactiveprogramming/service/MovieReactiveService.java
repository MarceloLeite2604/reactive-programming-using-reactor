package com.learnreactiveprogramming.service;

import ch.qos.logback.classic.spi.EventArgUtil;
import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class MovieReactiveService {

  private final MovieInfoService movieInfoService;

  private final ReviewService reviewService;

  public Flux<Movie> getAllMovies() {
    final var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

    return moviesInfoFlux.flatMap(movieInfo -> {
        final var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
          .collectList();

        return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
      })
      .onErrorMap(exception -> {
        log.error("Exception is: ", exception);
        throw new MovieException(exception.getMessage());
      })
      .log();
  }

  public Flux<Movie> getAllMoviesRetry() {
    final var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

    return moviesInfoFlux.flatMap(movieInfo -> {
        final var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
          .collectList();

        return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
      })
      .onErrorMap(exception -> {
        log.error("Exception is: ", exception);
        throw new MovieException(exception.getMessage());
      })
      .retry(3)
      .log();
  }

  public Flux<Movie> getAllMoviesRetryWhen() {
    final var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

    return moviesInfoFlux.flatMap(movieInfo -> {
        final var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
          .collectList();

        return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
      })
      .onErrorMap(exception -> {
        log.error("Exception is: ", exception);
        if (exception instanceof NetworkException) {
          throw new MovieException(exception.getMessage());
        }
        throw new ServiceException(exception.getMessage());
      })
      .retryWhen(getRetryBackoffSpec())
      .log();
  }

  public Flux<Movie> getAllMoviesRepeat() {
    final var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

    return moviesInfoFlux.flatMap(movieInfo -> {
        final var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
          .collectList();

        return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
      })
      .onErrorMap(exception -> {
        log.error("Exception is: ", exception);
        if (exception instanceof NetworkException) {
          throw new MovieException(exception.getMessage());
        }
        throw new ServiceException(exception.getMessage());
      })
      .retryWhen(getRetryBackoffSpec())
      .repeat()
      .log();
  }

  public Flux<Movie> getAllMoviesRepeat(long times) {
    final var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

    return moviesInfoFlux.flatMap(movieInfo -> {
        final var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
          .collectList();

        return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
      })
      .onErrorMap(exception -> {
        log.error("Exception is: ", exception);
        if (exception instanceof NetworkException) {
          throw new MovieException(exception.getMessage());
        }
        throw new ServiceException(exception.getMessage());
      })
      .retryWhen(getRetryBackoffSpec())
      .repeat(times)
      .log();
  }

  private RetryBackoffSpec getRetryBackoffSpec() {
    // "backoff is similar to "fixedDelay" (which adds a fixed delay between attempts), but it also adds a jitter between retries.
    final var retryWhen = Retry.backoff(3, Duration.ofMillis(500))
      .filter(exception -> exception instanceof MovieException)
      .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
        // "Propagate" is a Reactor handy function to propagate an exception throughout the pipeline.
        Exceptions.propagate(retrySignal.failure()));
    return retryWhen;
  }

  public Mono<Movie> getMovieByIdUsingZip(long movieId) {
    final var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
    final var reviewsMono = reviewService.retrieveReviewsFlux(movieId)
      .collectList();

    return movieInfoMono.zipWith(reviewsMono, Movie::new)
      .log();

  }

  public Mono<Movie> getMovieById(long movieId) {
    final var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);

    return movieInfoMono.flatMap(movieInfo ->
        reviewService.retrieveReviewsFlux(movieId)
          .collectList()
          .flatMap(reviews -> Mono.just(new Movie(movieInfo, reviews))))
      .log();
  }
}
