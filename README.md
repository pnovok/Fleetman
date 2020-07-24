# Fleetman

Fleetman application simulates monitoring of a fleet of trucks. The application generates geo positioning data for each truck, stores this data either in memory or in the database (Couchbase), and feeds location data into a convenient map-based web UI where we could see the latest position of each truck, its speed and a current journey. The original code for this application is taken from the Udemy class on K8s and Microservices: https://www.udemy.com/course/kubernetes-microservices/ and you can find it here: https://github.com/DickChesterwood/k8s-fleetman

I made the following changes:

1) Upgraded the version of Spring Boot 1.5.2 to 2.2.3;
2) Replaced Mongodb persistent storage implementation with Couchbase;
3) Added speed calculation for the database storage implementation version of the applicaiton

The application has been tested locally on my Mac with.

To run the app locally:

1) Install and run Apache ActiveMQ
2) Compile Java modules for position tracker, position simulator and api-gateway and run each of the .jar files produced.
3) Compile and Run the webapp angular module with the following command: npm start

That should bring up the map showing each truck location, last seen time and speed.
