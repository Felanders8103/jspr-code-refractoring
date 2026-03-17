package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryStubImpl implements PostRepository {
  public List<Post> all() {
    return Collections.emptyList();
  }

  public Optional<Post> getById(long id) {
    return Optional.empty();
  }

  public Post save(Post post) {
    return post;
  }

  public void removeById(long id) {
  }
}