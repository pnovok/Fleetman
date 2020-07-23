package com.virtualpairprogrammers.tracker.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.virtualpairprogrammers.tracker.domain.VehicleBuilder;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GlobalPosition;
import org.gavaghan.geodesy.GeodeticCalculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.stereotype.Repository;

import com.virtualpairprogrammers.tracker.domain.VehicleNotFoundException;
import com.virtualpairprogrammers.tracker.domain.VehiclePosition;


/**
 * This is a quick and dirty implementation of a Couchbase data store.
 */

@N1qlPrimaryIndexed
//@ViewIndexed(designDoc = "VehiclePosition", viewName = "all")
@Primary
//@Profile({"localhost", "production-microservice", "local-microservice"})
@Repository
public class DataCouchbaseDbImpl implements Data {

	private static final BigDecimal MPS_TO_MPH_FACTOR = new BigDecimal("2.236936");
	private GeodeticCalculator geoCalc = new GeodeticCalculator();

	@Autowired
	private PositionRepository couchbaseDb;

    @Override
	public void updatePosition(VehiclePosition position) {
		String vehicleName = position.getName();
		BigDecimal speed = calculateSpeedInMph(vehicleName, position);
		VehiclePosition vehicleWithSpeed = new VehicleBuilder().withVehiclePostion(position).withSpeed(speed).build();
    	couchbaseDb.save(vehicleWithSpeed);
	}

	@Override
	public VehiclePosition getLatestPositionFor(String vehicleName) throws VehicleNotFoundException {
		List<VehiclePosition> all = (List<VehiclePosition>) couchbaseDb.findByNameOrderByTimestampAsc(vehicleName);
		if (all.size() == 0) throw new VehicleNotFoundException();
		System.out.println("Last position for the car is "+ all.get(all.size()-1));
		return all.get(all.size() - 1);
	}

	@Override
	public void addAllReports(VehiclePosition[] allReports) {
		for (VehiclePosition next: allReports)
		{
			this.updatePosition(next);
		}
	}


	public Collection<VehiclePosition> getLatestPositionsOfAllVehiclesUpdatedSince(Date since) {
		//If no date, then a really old date is set
		if (since == null) {
			String sDate1="24/01/1974";
			try {
				since =new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		List<VehiclePosition> all = couchbaseDb.findByTimestampAfter(since);
		List<VehiclePosition> latestReports = new ArrayList<>();
		Set<String> VehicleNames = new HashSet<>();

		Comparator<VehiclePosition> byName = Comparator.comparing(VehiclePosition::getName);
		Comparator<VehiclePosition> byTimestamp = Comparator.comparing(VehiclePosition::getTimestamp);

		Collections.sort(all, byName.thenComparing(byTimestamp).reversed()); //Sort by name and then timestamp

		//Iterate through the collection and add the latest positions for each truck
		for (VehiclePosition s : all) {
			if (latestReports.size() == 0 || !latestReports.get(latestReports.size() - 1).getName().equals(s.getName())) {
					latestReports.add(s);
			}
		}

		return latestReports;
	}

	@Override
	public TreeSet<VehiclePosition> getAllReportsForVehicleSince(String name, Date timestamp)
			throws VehicleNotFoundException {
		return  couchbaseDb.findByNameAndTimestampAfterOrderByTimestampAsc(name, timestamp);
	}

	@Override
	public Collection<VehiclePosition> getHistoryFor(String vehicleName) throws VehicleNotFoundException {
		return new TreeSet<VehiclePosition>((Collection<? extends VehiclePosition>) couchbaseDb.findByNameOrderByTimestampAsc(vehicleName));
	}

	private BigDecimal calculateSpeedInMph(String vehicleName, VehiclePosition newPosition)  {

    	List<VehiclePosition> positions = couchbaseDb.findByNameOrderByTimestampAsc(vehicleName);
		if (positions.isEmpty()) return null;

		VehiclePosition posB = newPosition;
		VehiclePosition posA = positions.get(positions.size()-1); //This is actually the last report recorded


		long timeAinMillis = posA.getTimestamp().getTime();
		long timeBinMillis = posB.getTimestamp().getTime();
		long timeInMillis = timeBinMillis - timeAinMillis;
		if (timeInMillis == 0) return new BigDecimal("0");

		BigDecimal timeInSeconds = new BigDecimal(timeInMillis / 1000.0);

		GlobalPosition pointA = new GlobalPosition(posA.getLat().doubleValue(), posA.getLongitude().doubleValue(), 0.0);
		GlobalPosition pointB = new GlobalPosition(posB.getLat().doubleValue(), posB.getLongitude().doubleValue(), 0.0);

		double distance = geoCalc.calculateGeodeticCurve(Ellipsoid.WGS84, pointA, pointB).getEllipsoidalDistance(); // Distance between Point A and Point B
		BigDecimal distanceInMetres = new BigDecimal (""+ distance);

		BigDecimal speedInMps = distanceInMetres.divide(timeInSeconds, RoundingMode.HALF_UP);
		BigDecimal milesPerHour = speedInMps.multiply(MPS_TO_MPH_FACTOR);
		return milesPerHour;
	}

}
