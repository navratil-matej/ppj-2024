package net.cuddlebat.ppj2024.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import net.cuddlebat.ppj2024.Ppj2024Application;
import net.cuddlebat.ppj2024.orm.City;
import net.cuddlebat.ppj2024.orm.Country;
import net.cuddlebat.ppj2024.orm.Measurement;
import net.cuddlebat.ppj2024.repositories.CityRepository;
import net.cuddlebat.ppj2024.repositories.CountryRepository;
import net.cuddlebat.ppj2024.repositories.MeasurementRepository;

public class WeatherApiService implements InitializingBean, DisposableBean
{
	@Value("${weatherApi.apiKey}")
	private String apiKey;
	@Value("${weatherApi.apiUrl}")
	private String apiUrl;
	@Value("${weatherApi.apiPeriodMs}")
	private long apiPeriodMs;
	@Value("${weatherApi.cities}")
	private List<String> cities;
	@Value("${weatherApi.enabled}")
	private boolean enabled;
	
	@Autowired
	private MeasurementRepository mr;
	@Autowired
	private CityRepository cr;
	@Autowired
	private CountryRepository sr;

    private static final Logger log = LoggerFactory.getLogger(WeatherApiService.class);
	
	private Thread job;
	private HttpClient client;
	
	@Override
	public void afterPropertiesSet() throws Exception
	{
		if(enabled)
		{
			client = HttpClient.newHttpClient();
			job = makeThread();
			job.start();
		}
	}

	@Override
	public void destroy() throws Exception
	{
		if(enabled)
		{
			job.interrupt();
		}
	}
	
	private Thread makeThread()
	{
		return new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					log.info("Entering initial loop over cities in config.");
					for(var id : cities)
					{
						log.info(id);
						var pieces = id.split("@");
						var maybeCity = cr.findByNameAndCountry(pieces[0], pieces[1]);
						maybeCity.ifPresentOrElse(
							(x) -> fetchMeasurements(x),
							( ) -> fetchCity(pieces[0], pieces[1])
						);
						Thread.sleep(apiPeriodMs);
					}

					log.info("Initial loop complete.");
					while (true)
					{
						log.trace("Entering a loop over cities in db.");
						for(var city : cr.findAll())
						{
							log.trace(city.getName() + ", " + city.getCountry().getName());
							fetchMeasurements(city);
							Thread.sleep(apiPeriodMs);
						}
					}
				}
				catch(InterruptedException e) {}
			}

			private void fetchCity(String name, String countryCode)
			{
				var url = "%s/geo/1.0/direct?q=%s,%s&limit=1&appid=%s".formatted(
					apiUrl,
					name,
					countryCode,
					apiKey
				);
				log.trace(url);
				var request = HttpRequest.newBuilder(URI.create(url))
					.header("accept", "application/json")
					.build();
				var response = client.sendAsync(request, BodyHandlers.ofString())
					.thenApply(HttpResponse::body)
					.thenApply(JSONArray::new)
					.join();
				var maybeCountry = sr.findByCode(countryCode);
				if(maybeCountry.isEmpty())
					log.info("Registering new country from code, please edit name: " + countryCode);
			 	var obj = cr.<City>save(new City(
			 		name,
			 		maybeCountry.orElse(sr.<Country>save(new Country(countryCode, countryCode))),
			 		response.getJSONObject(0).getDouble("lat"),
			 		response.getJSONObject(0).getDouble("lon")
			 	));
			 	log.debug("New entry: " + obj.toString());
			}

			private void fetchMeasurements(City city)
			{
				var url = "%s/data/2.5/weather?lat=%f&lon=%f&appid=%s".formatted(
					apiUrl,
					city.getLatitude(),
					city.getLongitude(),
					apiKey
				);
				log.trace(url);
				var request = HttpRequest.newBuilder(URI.create(url))
					.header("accept", "application/json")
					.build();
				var response = client.sendAsync(request, BodyHandlers.ofString())
					.thenApply(HttpResponse::body)
					.thenApply(JSONObject::new)
					.join();
				var main = response.optJSONObject("main");
				var wind = response.optJSONObject("wind");
				var rain = response.optJSONObject("rain");
				var date = new Date(response.getLong("dt") * 1000);
				var maybeMeasurement = mr.findByCityAndDate(city.getName(), city.getCountry().getCode(), date);
				if(maybeMeasurement.isEmpty())
				{
				 	var obj = mr.<Measurement>save(new Measurement(
				 		city,
				 		date,
				 		main != null ? main.optDouble("temp")     : Double.NaN,
						main != null ? main.optDouble("pressure") : Double.NaN,
						main != null ? main.optDouble("humidity") : Double.NaN,
						wind != null ? wind.optDouble("speed")    : Double.NaN,
						rain != null ? rain.optDouble("1h")       : Double.NaN
				 	));
				 	log.debug("New entry: " + obj.toString());
				}
				else log.trace("No new entry.");
			}
		};
	}
}
