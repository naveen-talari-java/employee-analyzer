application.properties:
-----------------------
#this property used to enable/disable the csv file generation
#if sets to true, program will take care of creating the csv file
spring.csv.generation.enabled=true

#need to set below properties as well if enabled above property
spring.emp.csv.file.path=employees.csv (you can provide any name)
spring.no.of.emp.records=1000 (you can provide any no of records)

#if sets to false (which means you want to use your own csv file)
spring.csv.generation.enabled=false

#If csv generation is disabled, you can put the your own csv file in the project directory and set the below property with the file name
spring.emp.csv.file.path=your_file_name.csv

#After setting the properties, follow the below steps to run the app.

#1) Open a terminal and navigate to the directory where you saved the project.
#2) Run mvn clean install to build the project and run the tests.
#3) Run mvn spring-boot:run to execute the application and print the reports to the console.
