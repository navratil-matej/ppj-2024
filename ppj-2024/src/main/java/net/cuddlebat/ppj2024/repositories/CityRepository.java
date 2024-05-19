package net.cuddlebat.ppj2024.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import net.cuddlebat.ppj2024.orm.City;

@RepositoryRestResource(collectionResourceRel="City", path="City")
public interface CityRepository extends CrudRepository<City, Integer>
{
    @Query("select c from City as c where name=:cityName and country.code=:countryCode")
	public Optional<City> findByNameAndCountry(@Param("cityName") String cityName, @Param("countryCode") String countryCode);

    @Query("select c from City as c where country.code=:countryCode")
	public List<City> findByCountry(@Param("countryCode") String countryCode);
}
