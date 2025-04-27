technical_doc file provides the important classes and explanation. I suggest to go through the file to get the better understanding of the application

application.properties:
-----------------------
-> If you want to ask application to generate the csv file please set the below 
properties in application.properties file
spring.csv.generation.enabled=true
spring.emp.csv.file.path=employees.csv (you can provide any name)
spring.no.of.emp.records=1000 (you can provide any no of records)

-> If you want to use your own csv file please set the below 
properties in application.properties file (you can move your csv file into the project directory)
spring.csv.generation.enabled=false
spring.emp.csv.file.path=your_file_name.csv

#After setting the properties, follow the below steps to run the app.

#1) Open a terminal and navigate to the directory where you saved the project.
#2) Run mvn clean install to build the project and run the tests.
#3) Run mvn spring-boot:run to execute the application and print the reports to the console.
