package com.virtualpairprogrammers.tracker.domain;

import java.math.BigDecimal;
import java.util.Date;

//import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;


//import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
//@Entity
public class VehiclePosition implements Comparable<VehiclePosition>
{
	@NotNull
	@Id
	private String id;

	@NotNull
	@Field
	private String name;

	@NotNull
	@Field
	private BigDecimal lat;

	@NotNull
	@Field
	private BigDecimal longitude;
	
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone="UTC")
	@NotNull
	@Field
	private Date timestamp;

	private BigDecimal speed;
	
	VehiclePosition() {}
	
	VehiclePosition(String id, String name, BigDecimal lat, BigDecimal lng, Date timestamp, BigDecimal speed) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.longitude = lng;
		this.timestamp = timestamp;
		this.speed = speed;
	}

	@Override
	public int compareTo(VehiclePosition o) 
	{
		return o.timestamp.compareTo(this.timestamp);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehiclePosition other = (VehiclePosition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	public String getId() { return this.id; }

	public String getName() {
		return this.name;
	}

	public BigDecimal getLat() {
		return this.lat;
	}

	public BigDecimal getLongitude() {
		return this.longitude;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public BigDecimal getSpeed() {
		return this.speed;
	}

	@Override
	public String toString() {
		return "VehiclePosition [id=" + id + "name=" + name + ", lat=" + lat + ", longitude=" + longitude + ", timestamp="
				+ timestamp + ", speed=" + speed + "]";
	}

}
