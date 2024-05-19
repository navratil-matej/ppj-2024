package net.cuddlebat.ppj2024.repositories;

import org.junit.Before;
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
public class MeasurementTests
{
	@Autowired
	private MeasurementRepository mr;
	@Autowired
	private CityRepository cr;
	@Autowired
	private CountryRepository sr;
	
	private Date now;
	
	@Before
    public void init()
	{
        mr.deleteAll();
        cr.deleteAll();
		sr.deleteAll();
        
        var gb = sr.save(new Country("England", "GB"));
        var cz = sr.save(new Country("Czechia", "CZ"));
        
        var lond = cr.save(new City("London", gb, 10, 10));
        var prag = cr.save(new City("Prague", cz, 10, 20));        
        var gbSn = cr.save(new City("Same Name", gb, 20, 10));
        var czSn = cr.save(new City("Same Name", cz, 20, 20));
        
        now = new Date(System.currentTimeMillis());
        var aaa = new Date(System.currentTimeMillis() - 3600*1000);
        
        mr.save(new Measurement(lond, now, 1.0, 2.0, 3.0, 4.0, 10000.0));
        mr.save(new Measurement(lond, aaa, 5.0, 6.0, 7.0, 8.0, 10000.0));
        mr.save(new Measurement(prag, aaa, 4.0, 3.0, 2.0, 1.0,     0.0));
        mr.save(new Measurement(gbSn, now, 1.0, 1.0, 1.0, 1.0,     1.0));
        mr.save(new Measurement(czSn, now, 2.0, 2.0, 2.0, 2.0,     2.0));
    }
	
	@Test
	public void searchControllerTest()
	{
		var londAll = mr.findByCity("London", "GB");
		assertEquals("findByCity(London, GB) should contain two elements", 2, londAll.size());
		var id = cr.findByNameAndCountry("Prague", "CZ").get().getIdCity();
		var pragAll = mr.findByCityId(id);
		assertEquals("findByCityId(Prague ID) should contain 1 element", 1, pragAll.size());
		var londNow = mr.findByCityAndDate("London", "GB", now);
		assertTrue ("findByCityAndDate(London, GB, now) should contain an element", londNow.isPresent());
		var pragNow = mr.findByCityAndDate("Prague", "CZ", now);
		assertFalse("findByCityAndDate(Prague, CZ, now) should be empty", pragNow.isPresent());
	}
	
	@Test
	public void entityControllerTest()
	{
        var prag = cr.findByNameAndCountry("Prague", "CZ").orElseThrow();
        var id = mr.save(new Measurement(prag, now, 0.0, 0.0, 0.0, 0.0, 17.0)).getIdMeasurement();
        var pragNow = mr.findById(id);
        assertTrue("save and findById should return a measurement", pragNow.isPresent());
        assertEquals("save and findById should return the correct city", 17.0, pragNow.orElseThrow().getRain(), 1e-6);
        mr.deleteById(id);
        pragNow = mr.findById(id);
        assertFalse("deleteById and findById should return empty", pragNow.isPresent());
	}
}
