# Use the Java 12 base image
FROM openjdk:12

# Set the working directory in the image
WORKDIR /app

# Copy the application code into the image
COPY . /app



# Start a new Docker image for Apache Tomcat 8.5
FROM tomcat:8.5

# Copy the built WAR file into the Tomcat webapps directory
COPY ./target/APIContextInteraction.war /usr/local/tomcat/webapps/

# Expose the Tomcat HTTP port
EXPOSE 8080

# Start Tomcat when the container is run
CMD ["catalina.sh", "run"]
