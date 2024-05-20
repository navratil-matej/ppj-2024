package net.cuddlebat.ppj2024.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import net.cuddlebat.ppj2024.orm.Measurement;
import net.cuddlebat.ppj2024.repositories.CityRepository;
import net.cuddlebat.ppj2024.repositories.MeasurementRepository;

@RestController
public class WeatherAggregationController
{
	@Autowired
	private MeasurementRepository mr;
	
	@GetMapping("/city/{id}/aggs")
	public ResponseEntity<MeasurementAggsSet> aggsForCity(@PathVariable("id") long id)
	{
		var data = mr.findAllByCityId(id);
		var latest = data.stream()
			.max((x, y) -> x.getDate().compareTo(y.getDate()))
			.orElseThrow();
		
		var target = new MeasurementAggsSet(
			MeasurementAggs.of(latest),
			daysBack(data,  1),
			daysBack(data,  7),
			daysBack(data, 14)
		);
		return new ResponseEntity<MeasurementAggsSet>(target, HttpStatus.OK);
	}
	
	private MeasurementAggs daysBack(List<Measurement> data, int days)
	{
		return data.stream()
			.filter(x -> x.getDate().after(new Date(System.currentTimeMillis() - 86400 * 1000 * days)))
			.map(MeasurementAggs::of)
			.reduce(MeasurementAggs::add)
			.orElseThrow()
			.mean();
	}
	
	public record MeasurementAggs(double temperature, double pressure, double humidity, double wind, double rain, @JsonIgnore int count)
	{
		public static MeasurementAggs of(Measurement m)
		{
			return new MeasurementAggs(m.getTemperature(), m.getPressure(), m.getHumidity(), m.getWind(), m.getRain(), 1);
		}

		public MeasurementAggs add(MeasurementAggs other)
		{
			return new MeasurementAggs(
				temperature() + other.temperature(),
				pressure() + other.pressure(),
				humidity() + other.humidity(),
				wind() + other.wind(),
				rain() + other.rain(),
				count + other.count()
			);
		}

		public MeasurementAggs mean()
		{
			return new MeasurementAggs(
				temperature() / count(),
				pressure() / count(),
				humidity() / count(),
				wind() / count(),
				rain() / count(),
				1
			);
		}
	}
	
	public record MeasurementAggsSet(MeasurementAggs now, MeasurementAggs day, MeasurementAggs week, MeasurementAggs fortnight) { }
}
