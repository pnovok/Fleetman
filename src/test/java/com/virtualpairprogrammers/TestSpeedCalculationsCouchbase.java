package com.virtualpairprogrammers;

import com.virtualpairprogrammers.tracker.data.Data;
import com.virtualpairprogrammers.tracker.data.DataCouchbaseDbImpl;
import com.virtualpairprogrammers.tracker.domain.VehicleBuilder;
import com.virtualpairprogrammers.tracker.domain.VehicleNotFoundException;
import com.virtualpairprogrammers.tracker.domain.VehiclePosition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * These tests are based on rough measurements on a real map. Therefore, we only expect
 * results to be correct within 0.1mph. This is good enough for this system anyway.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@SpringBootConfiguration
@EnableAutoConfiguration
@Component
@ComponentScan("com.virtualpairprogrammers.tracker.data")
public class TestSpeedCalculationsCouchbase {

	@Autowired
	private Data data;

	@Test
	public void testSpeedAsMeasuredBetweenTwoPointsCoveredInFiveSeconds() throws VehicleNotFoundException {
		// These data points measured on a map
		// 1: 53.33507, -1.53766
		VehiclePosition report1 = new VehicleBuilder()
										.withId("key:1:city_truck")
										.withName("city_truck")
										.withLat("53.33507")
										.withLng("-1.53766")
										.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:24 BST 2017"))
										.build();
										
		data.updatePosition(report1);
		
		VehiclePosition pos = data.getLatestPositionFor("city_truck");
		assertNull("Expected speed of vehicle with one report is null", pos.getSpeed());
		
		// Point 2 is measured at 153m apart. 53.33635, -1.53682
		VehiclePosition report2 = new VehicleBuilder()
				.withId("key:2:city_truck")
				.withName("city_truck")
				.withLat("53.33635")
				.withLng("-1.53682")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:29 BST 2017"))
				.build();
		
		data.updatePosition(report2);
		
		pos = data.getLatestPositionFor("city_truck");
		
		// The two points are 153m apart, covered in 5s which gives 30.6m/s
		// This equates to 68.45025 mph. We're not expecting any of this to be dead accurate!
		// We'll go for within 0.1 mph...
		
		assertEquals(68.45025, pos.getSpeed().doubleValue(), 0.1);
	}

	@Test
	public void testSpeedWhenTravellingExactlyOneKilometerInOneMinute() throws VehicleNotFoundException {
		// These two points are on OS grid lines 1km apart, as measured by Memory Map.
		VehiclePosition report1 = new VehicleBuilder()
				.withId("key:1:city_truck1")
				.withName("city_truck1")
				.withLat("53.33393")
				.withLng("-1.52097")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:00 BST 2017"))
				.build();

		data.updatePosition(report1);
		
		VehiclePosition pos = data.getLatestPositionFor("city_truck1");
		assertNull("Expected speed of vehicle with one report is null", pos.getSpeed());
		
		VehiclePosition report2 = new VehicleBuilder()
				.withId("key:2:city_truck1")
				.withName("city_truck1")
				.withLat("53.34292")
				.withLng("-1.52083")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:27:00 BST 2017"))
				.build();
		data.updatePosition(report2);
		
		pos = data.getLatestPositionFor("city_truck1");
		
		// 1km apart, gives a speed of 16.67m/s ie 37.28mph
		assertEquals(37.28, pos.getSpeed().doubleValue(), 0.1);
	}
	
	@Test
	public void testStationaryVehicle() throws VehicleNotFoundException {
				VehiclePosition report1 = new VehicleBuilder()
				.withId("key:1:city_truck2")
				.withName("city_truck2")
				.withLat("53.33393")
				.withLng("-1.52097")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:00 BST 2017"))
				.build();
		data.updatePosition(report1);
		
		VehiclePosition pos = data.getLatestPositionFor("city_truck2");
		assertNull("Expected speed of vehicle with one report is null", pos.getSpeed());
		
		VehiclePosition report2 = new VehicleBuilder()
				.withId("key:2:city_truck2")
				.withName("city_truck2")
				.withLat("53.33393")
				.withLng("-1.52097")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:05 BST 2017"))
				.build();
		
		data.updatePosition(report2);
		
		pos = data.getLatestPositionFor("city_truck2");
		
		assertEquals(0, pos.getSpeed().doubleValue(), 0);
	}
	
	@Test
	public void testSpeedIsBasedOnlyOnLastReport() throws VehicleNotFoundException {

		// These two points are on OS grid lines 1km apart, as measured by Memory Map.
		VehiclePosition report1 = new VehicleBuilder()
				.withId("key:2:city_truck3")
				.withName("city_truck3")
				.withLat("53.33393")
				.withLng("-1.52097")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:00 BST 2017"))
				.build();
		data.updatePosition(report1);
			
		VehiclePosition report2 = new VehicleBuilder()
				.withId("key:2:city_truck3")
				.withName("city_truck3")
				.withLat("53.34292")
				.withLng("-1.52083")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:27:00 BST 2017"))
				.build();
		data.updatePosition(report2);
		
		VehiclePosition report3 = new VehicleBuilder()
				.withId("key:3:city_truck3")
				.withName("city_truck3")
				.withLat("53.33635")
				.withLng("-1.53682")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:28:24 BST 2017"))
				.build();		
		data.updatePosition(report3);
		
		VehiclePosition pos = data.getLatestPositionFor("city_truck3");
		
		// This last leg is 1.29km, and it took 84 seconds. 34.35mph 
		assertEquals(34.35, pos.getSpeed().doubleValue(), 0.1);
	}	
}
