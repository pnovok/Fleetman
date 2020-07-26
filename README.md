# Fleetman

Fleetman application simulates monitoring of a fleet of trucks. The application generates geo positioning data for each truck, stores this data either in memory or in the database (Couchbase), and feeds location data into a convenient map-based web UI where we could see the latest position of each truck, its speed and a current journey. The original code for this application is taken from the Udemy class on K8s and Microservices: https://www.udemy.com/course/kubernetes-microservices/ and you can find it here: https://github.com/DickChesterwood/k8s-fleetman

I made the following changes:

1) Upgraded the version of Spring Boot 1.5.2 to 2.2.1;
2) Replaced Mongo persistence storage implementation with Couchbase;
3) Added speed calculation for the database version of the application;

Fleetman has been tested locally on my Mac with 16GB of RAM and 6 CPU cores configuration.

To run the app locally:

1) Install and run Apache ActiveMQ.
2) Install and run Couchbase, configure the bucket, setup the RBAC user with the same name as a bucket and grant it bucket permissions. Add 2 secondary indexes on name and timestamp

CREATE INDEX `ix_name` ON `test`(`name`);
CREATE INDEX `ix_timestamp` ON `test`(`timestamp`);

3) Compile Java modules for position tracker, position simulator and api-gateway. Run each of the .jar files produced.
4) Compile and Run the webapp angular module with the following command: 

npm start 

That should bring up the map showing each truck location, last seen time and speed.
