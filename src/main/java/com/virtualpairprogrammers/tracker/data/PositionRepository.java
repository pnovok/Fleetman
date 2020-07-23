package com.virtualpairprogrammers.tracker.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.couchbase.repository.CouchbasePagingAndSortingRepository;

import com.virtualpairprogrammers.tracker.domain.VehiclePosition;
import org.springframework.stereotype.Repository;

public interface PositionRepository extends CouchbasePagingAndSortingRepository<VehiclePosition, String> {

	TreeSet<VehiclePosition> findByNameAndTimestampAfterOrderByTimestampAsc(String name, Date timestamp);

	List<VehiclePosition> findByName(String name);

	List<VehiclePosition> findByNameOrderByTimestampAsc(String name);

	List<VehiclePosition> findByTimestampAfter(Date since);

	//@Query("SELECT * FROM #{#n1ql.bucket} WHERE #{#n1ql.filter} and name = $1 ORDER By timestamp ASC")
	//List<VehiclePosition> findByNameAndSort(String name);


}
