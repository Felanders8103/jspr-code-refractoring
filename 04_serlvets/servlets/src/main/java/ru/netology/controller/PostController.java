package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Optional;

public class PostController {
  public static final String APPLICATION_JSON = "application/json";
  private final PostService service;
  private final Gson gson = new Gson();

  public PostController(PostService service) {
    this.service = service;
  }

  public void all(HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var data = service.all();
    response.getWriter().print(gson.toJson(data));
  }

  public void getById(long id, HttpServletResponse response) throws IOException {
    // TODO: deserialize request & serialize response
    response.setContentType(APPLICATION_JSON);
    Optional<Post> optional = Optional.ofNullable(service.getById(id));
    if (optional.isPresent()) {
      Post post = optional.get();
      String json = gson.toJson(post);
      response.getWriter().print(json);
    }
    else {
      response.setStatus(404);
    }
  }

  public void save(Reader body, HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var post = gson.fromJson(body, Post.class);
    final var data = service.save(post);
    response.getWriter().print(gson.toJson(data));
  }

  public void removeById(long id, HttpServletResponse response) throws IOException {
    // TODO: deserialize request & serialize response
    response.setContentType(APPLICATION_JSON);
    Optional<Post> existing = Optional.ofNullable(service.getById(id));
    if (existing.isEmpty()) {
      response.setStatus(404);
      Map<String, String> error = Map.of("error", "Post not found");
      response.getWriter().print(gson.toJson(error));
      return;
    }
    service.removeById(id);
    Map<String, String> result = Map.of("status", "deleted", "id", String.valueOf(id));
    response.getWriter().print(gson.toJson(result));
  }
}
