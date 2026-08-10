# Project Title: Dog Seizure Tracker Application

Summary: Dog Seizure Tracker is a Java-based application designed to help dog owners monitor and manage their pets' seizure activity. The application provides a centralized platform for recording dog information, and logging seizure events to help owners and veterinarians better understand and manage canine epilepsy and other seizure-related conditions.

![img.png](DST.png)

### **Key Features**
* User Profile, secure user authentication and login
* Personal account management
* Association of dogs with their owners
* Admin panel for managing user's roles 
* Dog management features
* Seizure log tracking


### **User Profile**
_Users can make an account and edit their information:_

* Username
* Password

* First name
* Last name
* Phone number
* Email address


### **Dog Management**
_Users can register and manage their dogs by recording:_

* Dog name
* Breed
* Gender
* Date of birth
* Food/Diet information


### **Seizure log**
_Users can record seizure events with detailed information:_

* Date of occurrence
* Time of occurrence
* Duration
* Severity level
* Recovery details

### **Roles**

**_Admin can:_**
* change User's role
* delete User's profile

**_User can:_**
* create profile and log in
* edit their information
* delete their profile
* add dog
* edit dog's information
* delete dog
* add seizure log
* edit seizure log
* delete seizure log


### **Technology Stack**
* Java 21
* Spring Boot 3.4.0
* Maven
* MySQL
* Spring MVC + Thymeleaf
* Spring Security
* Spring Data JPA
* Spring Cache
* OpenFeign

### **Supported Functionalities**
* User registration, login, profile update and account deletion
* Admin role switch and user management
* Dog create, update and delete
* Seizure create, update and delete
* Monthly seizure summary generation
* Medication create, update, delete and view through integrated microservice
* Medication schedule create, update, delete and due schedule view through integrated microservice
* Seizure report export to PDF

### **Integrations**
* `medication-svc` via OpenFeign on `http://localhost:8081/api/v1`
* PDF generation via `openhtmltopdf`

### **Getting Started Prerequisites**
* Java Development Kit (JDK) 17+
* Apache Maven 3.8+
* MySQL Server
* IDE (IntelliJ IDEA, Eclipse, or similar)

### **Run Locally**
* Clone repository:
  `git clone https://github.com/Miryana-st/dog-seizure-tracker.git`
* Configure database in `application.properties`:
  `spring.datasource.url=jdbc:mysql://localhost:3306/dog-seizure_tracker_app?createDatabaseIfNotExist=true`
* Start application:
  `mvn spring-boot:run`
* Open:
  `http://localhost:8080/`
