package com.in20minutes.rest.webservices.restfulwebservices.filtering;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@RestController
public class FilteringController {

	// hide field3
	@GetMapping(path = "/filtering")
	public MappingJacksonValue retriveSomeBean() {
		SomeBean bean = new SomeBean("value1", "value2", "value3");
		
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1","field2");
		return filterResource(filter, bean);
	}

	// hide field1
	@GetMapping(path = "/filtering-list")
	public MappingJacksonValue retriveListOfSomeBeans() {
		List<SomeBean> list = Arrays.asList(new SomeBean("value11", "value12", "value13"),
				new SomeBean("value21", "value22", "value23"));
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field2","field3");
		return filterResource(filter, list);
	}
	
	private MappingJacksonValue filterResource(SimpleBeanPropertyFilter filter, Object resource) {
		FilterProvider filters = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter);
		MappingJacksonValue mapping = new MappingJacksonValue(resource);
		mapping.setFilters(filters);
		
		return mapping;
	}
}
