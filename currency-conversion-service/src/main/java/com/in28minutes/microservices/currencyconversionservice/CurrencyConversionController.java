package com.in28minutes.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {

	@Autowired
	private CurrencyExchangeProxy proxy;
	
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{q}")
	public CurrencyConversion calculateCurrencyConversion(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal q) {

		HashMap<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);

		ResponseEntity<CurrencyConversion> template = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariables);
		CurrencyConversion exchange = template.getBody();

		return new CurrencyConversion(10001L, from, to, q, exchange.getConversionMultiple(),
				q.multiply(exchange.getConversionMultiple()), exchange.getEnvironment()+ " rest template");
	}
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{q}")
	public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal q) {
		
		CurrencyConversion exchange = proxy.retriveExchangeValue(from, to);

		return new CurrencyConversion(10001L, from, to, q, exchange.getConversionMultiple(),
				q.multiply(exchange.getConversionMultiple()), exchange.getEnvironment()+" feign");
	}
}
