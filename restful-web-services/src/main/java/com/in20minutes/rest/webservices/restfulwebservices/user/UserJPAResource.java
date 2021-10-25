package com.in20minutes.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.in20minutes.rest.webservices.restfulwebservices.post.Post;
import com.in20minutes.rest.webservices.restfulwebservices.post.PostNotFoundException;
import com.in20minutes.rest.webservices.restfulwebservices.post.PostRepository;

@RestController
public class UserJPAResource {
	@Autowired
	private UserDAOService service;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired 
	private PostRepository postRepository;
	
	//user
	@GetMapping(path = "/jpa/users")
	public List<User> retriveAllUsers() {
		return userRepository.findAll();
	}

	@GetMapping(path = "/jpa/users/{userId}")
	public EntityModel<User> retriveUser(@PathVariable int userId) throws UserNotFoundException {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("id-" + userId);
		}
		User user = userOptional.get();
		// add link of the specified method to response
		EntityModel<User> model = EntityModel.of(user);
		WebMvcLinkBuilder linkToUsers = linkTo(methodOn(this.getClass()).retriveAllUsers());
		model.add(linkToUsers.withRel("all-users"));
		return model;
	}

	@PostMapping(path = "/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User newUser = userRepository.save(user);
		// add links to response header
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping(path = "/jpa/users/{userId}")
	public void deleteUser(@PathVariable int userId) throws UserNotFoundException {
		userRepository.deleteById(userId);
	}
	
	//post
	@GetMapping(path = "/jpa/users/{userId}/posts")
	public List<Post> retriveAllPosts(@PathVariable int userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		if(userOptional.isEmpty()) {
			throw new UserNotFoundException("id-" + userId);
		}
		User user = userOptional.get();
		return user.getPosts();
	}
	
	@PostMapping(path = "/jpa/users/{userId}/posts")
	public ResponseEntity<Object> createPost(@PathVariable int userId, @Valid @RequestBody Post post) {
		Optional<User> userOptional = userRepository.findById(userId);
		if(userOptional.isEmpty()) {
			throw new UserNotFoundException("id-" + userId);
		}
		
		User user = userOptional.get();
		post.setUser(user);
		Post newPost = postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newPost.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}
	
	@GetMapping(path = "/jpa/users/{userId}/posts/{postId}")
	public Post retriveSinglePost(@PathVariable int userId, @PathVariable int postId) {
		Optional<User> userOptional = userRepository.findById(userId);
		if(userOptional.isEmpty()) {
			throw new UserNotFoundException("id-" + userId);
		}
		User user = userOptional.get();
		
		Optional<Post> postOptional = postRepository.findById(postId);
		if(postOptional.isEmpty()) {
			throw new PostNotFoundException("id-" + postId);
		}
		Post post = postOptional.get();
		
		if(!post.getUser().equals(user)) {
			// post doesn't belong to user
			throw new RuntimeException("Error! Please review userId and postId");
		}
		return post;
	}
	
	@DeleteMapping(path = "/jpa/users/{userId}/posts/postId")
	public void deletePost(@PathVariable int userId, @PathVariable int postId) throws UserNotFoundException {		
		Optional<User> userOptional = userRepository.findById(userId);
		if(userOptional.isEmpty()) {
			throw new UserNotFoundException("id-" + userId);
		}
		User user = userOptional.get();
		
		Optional<Post> postOptional = postRepository.findById(postId);
		if(postOptional.isEmpty()) {
			throw new PostNotFoundException("id-" + postId);
		}
		Post post = postOptional.get();
		
		if(!post.getUser().equals(user)) {
			// post doesn't belong to user
			throw new RuntimeException("Error! Please review userId and postId");
		}
		
		// delete post
//		user.getPosts().remove(post);
//		post.setUser(null);
		postRepository.delete(post);
	}
}
