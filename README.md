# Reservation Management System for Nail Shops


## 1. Problem Statement
**Reservation System:** Enable customers to book appointments online, reducing wait times and improving scheduling efficiency.

**Service Selection:** Provide customers with a range of services to choose from during booking. This includes, but is not limited to:

 - Manicure
 - Pedicure
 - Acrylic Nails
 - Gel & Shellac Nails
 - Nail Art
 - Dip Powder Nails
 - Polish Change
 - Hand and Foot Treatments
 - Nail Repair
   
**Billing and Receipts:** Generate billing and receipts based on chosen services.

**Loyalty Points Tracking:** Implement a loyalty program where customers can earn points for each reservation, which can be tracked and redeemed for future services.

## 2. Requirement Analysis
## 2.1 Functional Requirement
**Manager**
 - Create/Delete technician
 - Create/Delete manager
 - Create/Update/Delete service
 - View/Update/Cancel/Complete appointment
 - Login
 - Update profile
 - Change password
 - Print receipt

**Technician**
 - Login
 - Update profile
 - Change password
 - View appointments

**Customer**
- Register member
- Login
 - Update profile
 - Change password
 - View appointments
 - Book/Update/Cancel appointments
   
## 2.2 Non-functional Requirement
**Not-required authentication**
 - Create customer
 - Login
   
**Required authentication**
 All functionalities except login and create customer must be authenticated and authorizated by role.

**Password must be encrypted**

**Delete functionality: do not physically delete**

## 3. UML Diagram
![image](https://github.com/user-attachments/assets/939da771-95ff-4975-a140-c1ffff519056)

![image](https://github.com/user-attachments/assets/d2c09c64-25c6-442d-b1f5-a137382f9e39)

![image](https://github.com/user-attachments/assets/5949d2d4-feb8-47e9-8a9d-526c58022622)

## 4. Architecture
**Technologies used:**

**Back-end:** 
  - SpringBoot, Spring MVC, Spring Data JPA, Spring Security, MapStruct
  - Database: MySQL
  - Authentication and Authorization: JWT token
  - Testing: JUnit, Mockito, Postman

**Front-end**
  - Angular 17.3
  - Bootstrap, jwt-decode

![image](https://github.com/user-attachments/assets/bb80ca5e-f048-4fc5-89ec-738b7785396e)

**Front-end: Angular Components**

![image](https://github.com/user-attachments/assets/a755ee83-5f55-4149-8f20-8c7845f46002)

![image](https://github.com/user-attachments/assets/c4606d7a-3145-4e3d-a536-30bbf98b69b5)

## 5. ERD
![image](https://github.com/user-attachments/assets/4a98ff38-a49b-41f0-ab4f-2c13e2a77aa1)

## 6. Local Setup Instructions
## 6.1 Back-end
**1. Setup database connection in application.properties**

**2. Start the application**

 When the application starts, it creates 3 roles (MANAGER, TECHNICIAN, CUSTOMER) and 1 MANAGER account. The setup is in config/SetupInitialData.java

## 6.2 Front-end
**1. Configure API URL in app/common/constants.ts**

**2. Start the application (in command line type "ng serve")**

## 7. Azure Setup Instructions
## 7.1 Back-end
**Using Docker compose to deploy back-end APIs and MySQL on Azure App Services**

  -	Create Dockerfile for back-end to build image
  -	Create Docker compose file including MySQL and back-end API
  -	Create jar file (make sure to comment out database information in application.properties file. If not, it does not work when deploying to Azure)

   		mvn clean package -DskipTests
   	
  -	Build docker image for back-end API
    
    	docker build -t butbui86/nail-shop-api:1.0.1 .
  -	Push back-end image to Docker Hub (https://hub.docker.com/)
    
    	docker push butbui86/nail-shop-api:1.0.1
  -	Create Web App
    
    Go to App Services, create Web App and select Publish as Container.

   ![image](https://github.com/user-attachments/assets/57807b9a-74b0-44c2-84b2-3821094c433a)

   In Deployment -> Deployment Center, configure
    
Container type: Docker Compose

Registry source: Docker Hub
        
And upload the Docker compose file prepared into Config textbox
    
   ![image](https://github.com/user-attachments/assets/6edf87d8-c4a6-4840-ba39-3c2d6be0cb23)


## 7.2 Front-end

Go to App Services, create Static Web App

![image](https://github.com/user-attachments/assets/5277d223-5f24-46e3-bee7-f7a11d020a32)

![image](https://github.com/user-attachments/assets/b55a5cf1-e0e0-4c16-bcce-49fb4e7eb76b)

Then Azure will create a workflow file in the above repository. When there are any changes in this repository, GitHub action is triggered, the code is built and deployed to Azure Static Web App.

![image](https://github.com/user-attachments/assets/afd06d23-e79e-474a-85fd-d797f984bb52)

![image](https://github.com/user-attachments/assets/074eea08-c400-4f1f-9679-b98f16b440cc)

