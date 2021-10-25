package com.in20minutes.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

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

@RestController
public class UserResource {

	@Autowired
	private UserDAOService service;

	@GetMapping(path = "/users")
	public List<User> retriveAllUsers() {
		return service.findAll();
	}

	@GetMapping(path = "/users/{userId}")
	public EntityModel<User> retriveUser(@PathVariable int userId) throws UserNotFoundException {
		User user = service.findOne(userId);
		if (user == null) {
			throw new UserNotFoundException("id-" + userId);
		}
		// add link of the specified method to response
		EntityModel<User> model = EntityModel.of(user);
		WebMvcLinkBuilder linkToUsers = linkTo(methodOn(this.getClass()).retriveAllUsers());
		model.add(linkToUsers.withRel("all-users"));
		return model;
	}

	@PostMapping(path = "/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User newUser = service.save(user);
		// add links to response header
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping(path = "/users/{userId}")
	public void deleteUser(@PathVariable int userId) throws UserNotFoundException {
		User user = service.deleteById(userId);
		if (user == null) {
			throw new UserNotFoundException("id-" + userId);
		}
	}
}
