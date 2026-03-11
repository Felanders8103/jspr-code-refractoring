package ru.netology.repository;

import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Stub
public class PostRepository {

  private final Map<Long, Post> posts = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong(0);

  public List<Post> all() {
    return new ArrayList<>(posts.values());
  }

  public Optional<Post> getById(long id) {
    Post post = posts.get(id);
    return Optional.ofNullable(post);
  }

  public Post save(Post post) {
    long id = post.getId();
    if (id == 0) {
      long newId = idCounter.incrementAndGet();
      Post newPost = new Post(newId, post.getContent());
      posts.put(newId, newPost);
      return newPost;
    }
    else {
      if (!posts.containsKey(id)) {
        throw new RuntimeException("Post not found");
      }
      Post updatedPost = new Post(id, post.getContent());
      posts.put(id, updatedPost);
      return updatedPost;
    }
  }

  public void removeById(long id) {
    posts.remove(id);
  }
}
