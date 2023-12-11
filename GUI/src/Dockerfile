# Use a base Java image
FROM openjdk:latest

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy the compiled Java files and resources into the container
COPY . /usr/src/app

# Compile your Java code (if needed)
# RUN javac YourJavaFileName.java

# Define the command to run your Java application
CMD ["java", "ChatServer"]
