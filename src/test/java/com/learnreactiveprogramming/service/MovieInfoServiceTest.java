package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

  private WebClient webClient;

  private MovieInfoService movieInfoService;

  @BeforeEach
  void setUp() {
    webClient = WebClient.builder()
      .baseUrl("http://localhost:8080/movies")
      .build();
    movieInfoService = new MovieInfoService(webClient);
  }

  @Test
  void retrieveAllMovieInfoRestClient() {

    final var flux = movieInfoService.retrieveAllMovieInfoRestClient();

    StepVerifier.create(flux)
      .expectNextCount(7)
      .verifyComplete();
  }

  @Test
  void retrieveMovieInfoById_RestClient() {

    final var movieId = 1;
    final var mono = movieInfoService.retrieveMovieInfoByIdRestClient(movieId);

    StepVerifier.create(mono)
      .expectNextCount(1)
      .verifyComplete();
  }
}