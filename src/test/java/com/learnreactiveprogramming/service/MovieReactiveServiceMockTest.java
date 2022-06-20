package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceMockTest {

  @InjectMocks
  private MovieReactiveService movieReactiveService;

  @Mock
  private MovieInfoService movieInfoService;

  @Mock
  private ReviewService reviewService;

  @Test
  void getAllMovies() {

    when(movieInfoService.retrieveMoviesFlux())
      .thenCallRealMethod();

    when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

    final var flux = movieReactiveService.getAllMovies();

    StepVerifier.create(flux)
      .expectNextCount(3)
      .verifyComplete();
  }

  @Test
  void getAllMovies1() {

    final var exceptionMessage = "Exception occurred in reviewService";

    when(movieInfoService.retrieveMoviesFlux())
      .thenCallRealMethod();

    when(reviewService.retrieveReviewsFlux(anyLong()))
      .thenThrow(new RuntimeException(exceptionMessage));

    final var flux = movieReactiveService.getAllMovies();

    StepVerifier.create(flux)
      .expectErrorMessage(exceptionMessage)
      .verify();
  }

  @Test
  void getAllMoviesRetry() {

    final var exceptionMessage = "Exception occurred in reviewService";

    when(movieInfoService.retrieveMoviesFlux())
      .thenCallRealMethod();

    when(reviewService.retrieveReviewsFlux(anyLong()))
      .thenThrow(new RuntimeException(exceptionMessage));

    final var flux = movieReactiveService.getAllMoviesRetry();

    StepVerifier.create(flux)
      .expectErrorMessage(exceptionMessage)
      .verify();

    verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMoviesRetryWhen() {

    final var exceptionMessage = "Exception occurred in reviewService";

    when(movieInfoService.retrieveMoviesFlux())
      .thenCallRealMethod();

    when(reviewService.retrieveReviewsFlux(anyLong()))
      .thenThrow(new NetworkException(exceptionMessage));

    final var flux = movieReactiveService.getAllMoviesRetryWhen();

    StepVerifier.create(flux)
      .expectErrorMessage(exceptionMessage)
      .verify();

    verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMoviesRetryWhen1() {

    final var exceptionMessage = "Exception occurred in reviewService";

    when(movieInfoService.retrieveMoviesFlux())
      .thenCallRealMethod();

    when(reviewService.retrieveReviewsFlux(anyLong()))
      .thenThrow(new ServiceException(exceptionMessage));

    final var flux = movieReactiveService.getAllMoviesRetryWhen();

    StepVerifier.create(flux)
      .expectErrorMessage(exceptionMessage)
      .verify();

    verify(reviewService, times(1)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMoviesRepeat() {

    final var exceptionMessage = "Exception occurred in reviewService";

    when(movieInfoService.retrieveMoviesFlux())
      .thenCallRealMethod();

    when(reviewService.retrieveReviewsFlux(anyLong()))
      .thenCallRealMethod();

    final var flux = movieReactiveService.getAllMoviesRepeat();

    StepVerifier.create(flux)
      .expectNextCount(6)
      .thenCancel()
      .verify();

    verify(reviewService, times(6)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMoviesRepeat1() {

    final var noOfTimes = 2L;

    when(movieInfoService.retrieveMoviesFlux())
      .thenCallRealMethod();

    when(reviewService.retrieveReviewsFlux(anyLong()))
      .thenCallRealMethod();

    final var flux = movieReactiveService.getAllMoviesRepeat(noOfTimes);

    StepVerifier.create(flux)
      .expectNextCount(9)
      .thenCancel()
      .verify();

    verify(reviewService, times(9)).retrieveReviewsFlux(isA(Long.class));
  }
}