package net.cuddlebat.ppj2024.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import net.cuddlebat.ppj2024.orm.Measurement;

@RepositoryRestResource(collectionResourceRel="Measurement", path="Measurement")
public interface MeasurementRepository extends CrudRepository<Measurement, Integer>
{
    @Query("select m from Measurement as m where city.name=:cityName and city.country.code=:countryCode")
	public List<Measurement> findByCity(@Param("cityName") String cityName, @Param("countryCode") String countryCode);
    
    @Query("select m from Measurement as m where city.idCity=:idCity")
	public List<Measurement> findByCityId(@Param("idCity") long idCity);

    @Query("select m from Measurement as m where city.name=:cityName and city.country.code=:countryCode and date=:date")
	public Optional<Measurement> findByCityAndDate(@Param("cityName") String cityName, @Param("countryCode") String countryCode, @Param("date") Date date);
}
