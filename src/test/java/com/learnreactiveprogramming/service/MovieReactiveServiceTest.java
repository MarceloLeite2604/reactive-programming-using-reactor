package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.swing.plaf.metal.MetalIconFactory;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {

  private MovieInfoService movieInfoService;
  private ReviewService reviewService;
  private RevenueService revenueService;
  private MovieReactiveService movieReactiveService;

  @BeforeEach
  void setUp() {
    movieInfoService = new MovieInfoService();
    reviewService = new ReviewService();
    revenueService = new RevenueService();
    movieReactiveService = new MovieReactiveService(movieInfoService, reviewService, revenueService);
  }

  @Test
  void getAllMovies() {
    final var moviesFlux = movieReactiveService.getAllMovies();

    StepVerifier.create(moviesFlux)
      .assertNext(movie -> {
        assertEquals("Batman Begins", movie.getMovie()
          .getName());
        assertEquals(2, movie.getReviewList()
          .size());
      })
      .assertNext(movie -> {
        assertEquals("The Dark Knight", movie.getMovie()
          .getName());
        assertEquals(2, movie.getReviewList()
          .size());
      })
      .assertNext(movie -> {
        assertEquals("Dark Knight Rises", movie.getMovie()
          .getName());
        assertEquals(2, movie.getReviewList()
          .size());
      })
      .verifyComplete();
  }

  @Test
  void getMovieByIdUsingZip() {
    final long movieId = 100L;

    final var movieMono = movieReactiveService.getMovieByIdUsingZip(movieId);

    StepVerifier.create(movieMono)
      .assertNext(movie -> {
        assertEquals("Batman Begins", movie.getMovie()
          .getName());
        assertEquals(2, movie.getReviewList()
          .size());
      })
      .verifyComplete();
  }

  @Test
  void getMovieById() {
    final long movieId = 100L;

    final var movieMono = movieReactiveService.getMovieById(movieId);

    StepVerifier.create(movieMono)
      .assertNext(movie -> {
        assertEquals("Batman Begins", movie.getMovie()
          .getName());
        assertEquals(2, movie.getReviewList()
          .size());
      })
      .verifyComplete();
  }

  @Test
  void getMovieByIdWithRevenue() {
    final long movieId = 100L;

    final var movieMono = movieReactiveService.getMovieByIdWithRevenue(movieId);

    StepVerifier.create(movieMono)
      .assertNext(movie -> {
        assertEquals("Batman Begins", movie.getMovie()
          .getName());
        assertEquals(2, movie.getReviewList()
          .size());
        assertNotNull(movie.getRevenue());
      })
      .verifyComplete();
  }
}