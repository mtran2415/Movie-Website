## CS 122B Project 5 Tomcat Pooling example

This example shows how to use Connection Pooling with JDBC.

### To run this example: 
1. clone this repository using `git clone https://github.com/UCI-Chenli-teaching/project5-TomcactPooling-example.git`
2. open Eclipse -> File -> import -> under "Maven" -> "Existing Maven Projects" -> Click "Finish".
3. For "Root Directory", click "Browse" and select this repository's folder. Click "Finish".
4. In `WebContent/META-INF/context.xml`, make sure the mysql username is `mytestuser` and password is `mypassword`.
5. Also make sure you have the `moviedb` database.
6. You can run this project on Tomcat now.

### Brief Explanation
`TomcatPoolingServlet.java` is a Java servlet that showcases connection pooling. The username and password of the MySQL is given to JDBC through a context.xml file. JDBC uses the credntials to create a connection pool. The servlet leases connections from this pool when needed and returns when the task is done.

Navigate to http://localhost:8080/project5-TomcatTest-example/tomcat-pooling to see a list of stars.
