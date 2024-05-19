package net.cuddlebat.ppj2024.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.cuddlebat.ppj2024.orm.Measurement;
import net.cuddlebat.ppj2024.repositories.MeasurementRepository;

@RestController
public class WeatherAggregationController
{
	@Autowired
	private MeasurementRepository mr;
	
	@GetMapping("/Agg/Daily")
	public List<Measurement> findAll()
	{
		var target = new ArrayList<Measurement>();
		mr.findAll().forEach(target::add);
		return target;
	}
}
