package net.cuddlebat.ppj2024.orm;

import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity @Table(uniqueConstraints=@UniqueConstraint(columnNames={"city", "date"}))
public class Measurement
{
	@Id @GeneratedValue @Column
	@JsonIgnore
	private int idMeasurement;
	@ManyToOne @JoinColumn(name="idCity") @OnDelete(action = OnDeleteAction.CASCADE)
	private City city;
	@Column
	private Date date;
	@Column(nullable=true)
	private double temperature;
	@Column(nullable=true)
	private double pressure;
	@Column(nullable=true)
	private double humidity;
	@Column(nullable=true)
	private double wind;
	@Column(nullable=true)
	private double rain;
	
	public Measurement() {}

	public Measurement(City city, Date date, double temperature, double pressure, double humidity, double wind, double rain)
	{
		super();
		this.city = city;
		this.date = date;
		this.temperature = temperature;
		this.pressure = pressure;
		this.humidity = humidity;
		this.wind = wind;
		this.rain = rain;
	}

	public int getIdMeasurement()
	{
		return idMeasurement;
	}

	public void setIdMeasurement(int idMeasurement)
	{
		this.idMeasurement = idMeasurement;
	}

	public City getCity()
	{
		return city;
	}

	public void setCity(City city)
	{
		this.city = city;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public double getTemperature()
	{
		return temperature;
	}

	public void setTemperature(double temperature)
	{
		this.temperature = temperature;
	}

	public double getPressure()
	{
		return pressure;
	}

	public void setPressure(double pressure)
	{
		this.pressure = pressure;
	}

	public double getHumidity()
	{
		return humidity;
	}

	public void setHumidity(double humidity)
	{
		this.humidity = humidity;
	}

	public double getWind()
	{
		return wind;
	}

	public void setWind(double wind)
	{
		this.wind = wind;
	}

	public double getRain()
	{
		return rain;
	}

	public void setRain(double rain)
	{
		this.rain = rain;
	}

	@Override
	public String toString()
	{
		return "Measurement [idMeasurement=" + idMeasurement + ", city=" + city + ", date=" + date + ", temperature="
				+ temperature + ", pressure=" + pressure + ", humidity=" + humidity + ", wind=" + wind + ", rain="
				+ rain + "]";
	}
}