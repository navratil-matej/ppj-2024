package net.cuddlebat.ppj2024.service;

import java.util.Date;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import net.cuddlebat.ppj2024.orm.City;
import net.cuddlebat.ppj2024.orm.Country;
import net.cuddlebat.ppj2024.orm.Measurement;
import net.cuddlebat.ppj2024.repositories.CityRepository;
import net.cuddlebat.ppj2024.repositories.CountryRepository;
import net.cuddlebat.ppj2024.repositories.MeasurementRepository;
import net.cuddlebat.ppj2024.rest.WeatherAggregationController;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.cuddlebat.ppj2024.Ppj2024Application;
import net.cuddlebat.ppj2024.orm.City;
import net.cuddlebat.ppj2024.orm.Country;
import net.cuddlebat.ppj2024.orm.Measurement;
import net.cuddlebat.ppj2024.repositories.CityRepository;
import net.cuddlebat.ppj2024.repositories.CountryRepository;
import net.cuddlebat.ppj2024.repositories.MeasurementRepository;

import static org.junit.Assert.*;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Ppj2024Application.class)
@ActiveProfiles({"TEST"})
@TestPropertySource(locations = "classpath:application-test.properties")
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WeatherAggregationTests
{
	@Autowired
	private MeasurementRepository mr;
	@Autowired
	private CityRepository cr;
	@Autowired
	private CountryRepository sr;
	@Autowired
	private WeatherAggregationController wa;
	
	@Before
    public void init()
	{
        mr.deleteAll();
        cr.deleteAll();
		sr.deleteAll();
        
        var cz = sr.save(new Country("Czechia", "CZ"));
        
        var prg = cr.save(new City("Prague", cz, 10, 20));
        var lbc = cr.save(new City("Liberec", cz, 10, 20));
        
        var now = new Date(System.currentTimeMillis());
        var _2h = new Date(System.currentTimeMillis() -  2* 3600*1000);
        var _9h = new Date(System.currentTimeMillis() -  2* 3600*1000);
        var _2d = new Date(System.currentTimeMillis() -  2*86400*1000);
        var _9d = new Date(System.currentTimeMillis() -  9*86400*1000);
        var _3w = new Date(System.currentTimeMillis() - 21*86400*1000);
        
        mr.save(new Measurement(prg, now, 1.0, 2.0, 3.0, 4.0, 5.0));
        mr.save(new Measurement(prg, _2h, 2.0, 3.0, 4.0, 5.0, 6.0));
        mr.save(new Measurement(prg, _9h, 3.0, 4.0, 5.0, 6.0, 7.0));
        mr.save(new Measurement(prg, _2d, 4.0, 5.0, 6.0, 7.0, 8.0));
        mr.save(new Measurement(prg, _9d, 5.0, 6.0, 7.0, 8.0, 9.0));
        mr.save(new Measurement(prg, _3w, 6.0, 7.0, 8.0, 9.0, 0.0));
        mr.save(new Measurement(lbc, _9h, 1e5, 1e5, 1e5, 1e5, 1e5));
        mr.save(new Measurement(lbc, _3w, 1e5, 1e5, 1e5, 1e5, 1e5));
    }
	
	@Test
	public void aggregationControllerTest()
	{
        var id = cr.findByNameAndCountry("Prague", "CZ").orElseThrow().getIdCity();
		var aggs = wa.aggsForCity(id).getBody();
		
        assertEquals("aggs.now should match latest measurement: temp", 1.0, aggs.now().temperature(), 1e-6);
        assertEquals("aggs.now should match latest measurement: rain", 5.0, aggs.now().rain(), 1e-6);
        
        assertEquals("aggs.day should match latest 3 measurements: temp", 2.0, aggs.day().temperature(), 1e-6);
        assertEquals("aggs.day should match latest 3 measurements: rain", 6.0, aggs.day().rain(), 1e-6);
        
        assertEquals("aggs.week should match latest 4 measurements: temp", 2.5, aggs.week().temperature(), 1e-6);
        assertEquals("aggs.week should match latest 4 measurements: rain", 6.5, aggs.week().rain(), 1e-6);
        
        assertEquals("aggs.fortnight should match latest 5 measurements: temp", 3.0, aggs.fortnight().temperature(), 1e-6);
        assertEquals("aggs.fortnight should match latest 5 measurements: rain", 7.0, aggs.fortnight().rain(), 1e-6);
	}
}
