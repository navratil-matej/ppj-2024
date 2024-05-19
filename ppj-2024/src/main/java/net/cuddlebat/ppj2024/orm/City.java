package net.cuddlebat.ppj2024.orm;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table
public class City
{
	@Id @GeneratedValue @Column
	@JsonIgnore
	private int idCity;
	@Column
	private String name;
	@ManyToOne @JoinColumn(name="idCountry")
	private Country country;
	@Column
	private double latitude;
	@Column
	private double longitude;
	
	public City() {}

	public City(String name, Country country, double latitude, double longitude)
	{
		super();
		this.name = name;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getIdCity()
	{
		return idCity;
	}

	public void setIdCity(int idCity)
	{
		this.idCity = idCity;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Country getCountry()
	{
		return country;
	}

	public void setCountry(Country country)
	{
		this.country = country;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	
	
}
