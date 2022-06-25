package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

class ReviewServiceTest {

  private ReviewService reviewService;

  @BeforeEach
  void setUp() {
    final var webclient = WebClient.builder()
      .baseUrl("http://localhost:8080/movies")
      .build();

    reviewService = new ReviewService(webclient);
  }

  @Test
  void retrieveReviewsFlux_RestClient() {
    final var movieInfoId = 1;

    final var flux = reviewService.retrieveReviewsFluxRestClient(movieInfoId);

    StepVerifier.create(flux)
      .expectNextCount(1)
      .verifyComplete();
  }
}