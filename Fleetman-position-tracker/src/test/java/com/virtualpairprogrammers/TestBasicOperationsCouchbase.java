//Testing Couchbase methods here
package com.virtualpairprogrammers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//import com.virtualpairprogrammers.tracker.data.CustomCouchbaseConfig;
import com.virtualpairprogrammers.tracker.data.Data;

import com.virtualpairprogrammers.tracker.data.PositionRepository;
import com.virtualpairprogrammers.tracker.domain.VehicleBuilder;
import com.virtualpairprogrammers.tracker.domain.VehicleNotFoundException;
import com.virtualpairprogrammers.tracker.domain.VehiclePosition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootConfiguration
@EnableAutoConfiguration
@Component
@ComponentScan("com.virtualpairprogrammers.tracker.data")
public class TestBasicOperationsCouchbase {

	@Autowired
	private Data testData;

	@Autowired
	private PositionRepository posRep;

	private VehiclePosition firstReport;
	private VehiclePosition secondReport;
	private VehiclePosition thirdReport;
	private VehiclePosition fourthReport;
	private VehiclePosition fifthReport;
	private VehiclePosition sixthReport;
	private VehiclePosition seventhReport;
	private VehiclePosition[] allReports;


	public TestBasicOperationsCouchbase() {


		// Set to trap any problems with non UK locales
		Locale.setDefault(new Locale("tr", "TR"));

		firstReport = new VehicleBuilder()
				.withId("key:1")
				.withName("who cares")
				.withLat("1.0")
				.withLng("1.0")
				.withTimestamp(TestUtils.getDateFrom("Wed Feb 01 10:26:12 BST 2017"))
				.build();

		secondReport = new VehicleBuilder()
				.withId("key:2")
				.withName("who cares")
				.withLat("2.0")
				.withLng("2.0")
				.withTimestamp(TestUtils.getDateFrom("Mon May 01 10:26:12 BST 2017"))
				.build();

		thirdReport = new VehicleBuilder()
				.withId("key:3")
				.withName("who cares")
				.withLat("3.0")
				.withLng("3.0")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:12 BST 2017"))
				.build();

		fourthReport = new VehicleBuilder()
						.withId("key:4")
						.withName("who cares")
						.withLat("4.0")
						.withLng("4.0")
						.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:24 BST 2017"))
						.build();

		fifthReport = new VehicleBuilder()
				.withId("key:5")
				.withName("who cares")
				.withLat("5.0")
				.withLng("5.0")
				.withTimestamp(TestUtils.getDateFrom("Wed Jul 05 10:26:30 BST 2017"))
				.build();

		sixthReport = new VehicleBuilder()
				.withId("key:6")
				.withName("who cares")
				.withLat("6.0")
				.withLng("6.0")
				.withTimestamp(TestUtils.getDateFrom("Thu Jul 06 10:26:12 BST 2017"))
				.build();

		seventhReport = new VehicleBuilder()
				.withId("key:7")
				.withName("who cares")
				.withLat("7.0")
				.withLng("7.0")
				.withTimestamp(TestUtils.getDateFrom("Wed May 09 19:55:12 BST 2018"))
				.build();

		allReports = new VehiclePosition[] {firstReport, secondReport, thirdReport, fourthReport, fifthReport, sixthReport, seventhReport};
	}

	//Testing simple insert into Couchbase
	@Test
	public void testGettingSaveToDbToWork() {
		testData.updatePosition(thirdReport);
	}

	//Testing a simple insert into Couchbase and getting a location report for a test vehicle.
    @Test
	public void testGettingAllReportsWorks() {
		testData.updatePosition(thirdReport);
		try
		{
			VehiclePosition foundPosition = testData.getLatestPositionFor("who cares");
			assertEquals(thirdReport, foundPosition);
		}
		catch (VehicleNotFoundException e)
		{
			fail("Vehicle was not found - it should have been!!");
		}
	}

	//Testing a VehicelNotFoundException by passing a name of the non-existent vehicle and requesting a position report
	@Test(expected = VehicleNotFoundException.class)
	public void testFindingANonExistentVehicleRaisesException() throws VehicleNotFoundException {
		testData.updatePosition(thirdReport);
		testData.getLatestPositionFor("wrong name");
	}

	//Testing a last position report for specific vehicle
	@Test
	public void testGettingLastReportWorks() throws VehicleNotFoundException
	{
		testData.updatePosition(firstReport);
		testData.updatePosition(secondReport);
		testData.updatePosition(thirdReport);

		VehiclePosition foundPosition = testData.getLatestPositionFor("who cares");
		assertEquals(thirdReport, foundPosition);
	}


	//Testing that the last position report are returned for the latest timestamp even when report arrive in the wrong order.
	@Test
	public void testGettingLastReportWorksBasedOnTimestampEvenIfReportsArriveInTheWrongOrder() throws VehicleNotFoundException
	{
		testData.updatePosition(sixthReport);
		testData.updatePosition(secondReport);
	    testData.updatePosition(seventhReport);
	    testData.updatePosition(firstReport);
	    testData.updatePosition(thirdReport);

		VehiclePosition foundPosition = testData.getLatestPositionFor("who cares");
		assertEquals(seventhReport, foundPosition);
	}

	//Testing position reports obtained for a particular vehicle after a certain timestamp
	//Method findByNameAndTimestampAfter(name, timestamp) has timestamp parameter which is not inclusive
	@Test
	public void testGettingAllReportsForVehicleSinceAParticularTime() throws VehicleNotFoundException
	{
		testData.addAllReports(allReports);

		// This is the exact timestamp of report 4 minus 1 second.
		Collection<VehiclePosition> reports = testData.getAllReportsForVehicleSince("who cares", TestUtils.getDateFrom("Wed Jul 05 10:26:23 BST 2017"));

		// should contain 4, 5, 6, 7
		Set<VehiclePosition> expectedReports = new HashSet<>();
		expectedReports.add(fourthReport);
		expectedReports.add(fifthReport);
		expectedReports.add(sixthReport);
		expectedReports.add(seventhReport);

		assertEquals(4, reports.size());
		assertTrue(reports.containsAll(expectedReports));
	}

	//Testing that no position reports will be produced for a test vehicle after a certain timestamp
	//Report 7 is the oldest and is dated by May 9-th 2018
	@Test
	public void testGettingAllReportsSinceAVeryLateTimeResultsInNoReports() throws VehicleNotFoundException
	{
		testData.addAllReports(allReports);
		Collection <VehiclePosition> results = testData.getAllReportsForVehicleSince("who cares", TestUtils.getDateFrom("Thu May 10 12:00:00 BST 2018"));
		assertTrue(results.isEmpty());
	}

	//Testing that we bring back all position reports after going back to history
	@Test
	public void testGettingAllReportsSinceAVeryLongTimeAgoReturnsTheLot() throws VehicleNotFoundException
	{
		testData.addAllReports(allReports);
		Collection <VehiclePosition> results = testData.getAllReportsForVehicleSince("who cares", TestUtils.getDateFrom("Sat Jun 09 12:00:00 BST 1973"));
		assertEquals(7, results.size());
		assertTrue(results.containsAll(Arrays.asList(allReports)));
	}


	//Looking if there are no reports found if the wrong vehicle name is passed
	@Test//(expected=VehicleNotFoundException.class)
	public void testGettingAllReportsForNonExistentVehicleThrowsException() throws VehicleNotFoundException
	{
		testData.addAllReports(allReports);
		Collection <VehiclePosition> results = testData.getAllReportsForVehicleSince("unknown", TestUtils.getDateFrom("Sat Jun 09 12:00:00 BST 1973"));
		System.out.println("Found "+ results.size() + " position reports");
		assertTrue(results.isEmpty());

	}

	//Looking all position reports for a given vehicle
	@Test
	public void testGettingReportsHistoryForAVehicle() throws VehicleNotFoundException
	{
		testData.addAllReports(allReports);
		Collection <VehiclePosition> results = testData.getHistoryFor("who cares");
		System.out.println("Found "+ results.size() + " position reports");
		assertEquals(7, results.size());
	}
}
