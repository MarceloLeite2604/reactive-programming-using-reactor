package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceRestClientTest {

  private MovieReactiveService movieReactiveService;

  @BeforeEach
  void setUp() {
    final var webClient = WebClient.builder()
      .baseUrl("http://localhost:8080/movies")
      .build();

    final var movieInfoService = new MovieInfoService(webClient);
    final var reviewService = new ReviewService(webClient);
    final var revenueService = new RevenueService();
    movieReactiveService = new MovieReactiveService(movieInfoService, reviewService, revenueService);
  }

  @Test
  void getAllMoviesRestClient() {

    final var flux = movieReactiveService.getAllMoviesRestClient();

    StepVerifier.create(flux)
      .expectNextCount(7)
      .verifyComplete();
  }

  @Test
  void getMovieByIdRestClient() {
    final var movieId = 1L;
    final var mono = movieReactiveService.getMovieByIdRestClient(movieId);

    StepVerifier.create(mono)
      .expectNextCount(1)
      .verifyComplete();
  }
}