# Hotel-challenge

## Description

This Project is a coding challenge. 
It was designed as a backend Rest API to allow that every end-user can check the room availability, place a reservation, cancel it or modify it.

### Tech stack
* Java 11
* Spring boot
* Maven
* H2
* Lombok

## Usage

To run the Application, execute the following command after checked you have maven and java installed in your machine.
```bash
mvn spring-boot:run
```

### Run the tests
```bash
mvn test
```

## API


**Retrieve all reservations**
----
* **URL** <br />
  api/reservations
* **Method:** <br />
  `GET`
* **Success Response:**
  * **Code:** 200 OK<br />
    **Content:** 
    ```javascript
    [
        {
            "id": 1,
            "initialDate": "2021-08-19",
            "finalDate": "2021-08-21"
        },
        {
            "id": 2,
            "initialDate": "2021-08-22",
            "finalDate": "2021-08-24"
        }
    ]
    ```
    
**Retrieve one reservation by id**
----
* **URL** <br />
  api/reservations/:id
* **Method:**<br />
  `GET`
*  **URL Params**<br />
   `id=[long]`
* **Success Response:**
  * **Code:** 200 OK <br />
    **Content:** 
    ```javascript
    {
        "id": 1,
        "initialDate": "2021-08-19",
        "finalDate": "2021-08-21"
    }
    ```
* **Error Response:**
  * **Code:** 404 NOT FOUND <br />
    **Content:** 
    ```javascript
    {
        "timestamp": "2021-08-15T22:19:13.413713",
        "message": "Could not find reservation 1"
    }
    ```

**Check if a period is available**
----
* **URL** <br />
  api/reservations/check_availability?initialDate=:initialDate&finalDate=:finalDate
* **Method:**<br />
    `GET`
*  **URL Params**<br />
    `initialDate=[date]`<br />
    `finalDate=[date]` <br />
    example: initialDate=2021-08-19
* **Success Response:**
  * **Code:** 200 OK<br />
    **Content:** 
    ```javascript
       false
    ```

**Save a reservation**
----
* **URL** <br />
  api/reservations
* **Method:**<br />
    `POST`
*  **Data Params**<br />
    example: 
    ```javascript
    {
        "initialDate":"2021-08-19",
        "finalDate":"2021-08-21"
    }
    ```
* **Success Response:**
  * **Code:** 201 CREATED <br />
    **Content:** 
    ```javascript
    {
        "id": 1,
        "initialDate": "2021-08-19",
        "finalDate": "2021-08-21"
    }
    ```
* **Error Response:**
  * **Code:** 400 BAD REQUEST <br />
    **Content:** 
    ```javascript
    {
        "timestamp": "2021-08-15T22:40:46.579257",
        "message": "Validation Failed",
        "details": "finalDate is mandatory; "
    }
    ```

**Update a reservation**
----
* **URL** <br />
  api/reservations/:id
* **Method:**<br />
    `PUT`
*  **URL Params**<br />
    `id=[long]`
*  **Data Params**<br />
    example: 
    ```javascript
    {
        "initialDate":"2021-08-19",
        "finalDate":"2021-08-21"
    }
    ```
* **Success Response:**
  * **Code:** 200 OK<br />
    **Content:** 
    ```javascript
    {
        "id": 1,
        "initialDate": "2021-08-19",
        "finalDate": "2021-08-21"
    }
    ```
* **Error Response:**
  * **Code:** 400 BAD REQUEST <br />
    **Content:** 
    ```javascript
    {
        "timestamp": "2021-08-15T22:40:46.579257",
        "message": "Validation Failed",
        "details": "finalDate is mandatory; "
    }
    ```
    
  * **Code:** 404 NOT FOUND <br />
    **Content:** 
    ```javascript
    {
        "timestamp": "2021-08-15T22:19:13.413713",
        "message": "Could not find reservation 1"
    }
    ```

**Delete a reservation**
----
* **URL** <br />
  api/reservations/:id
* **Method:**<br />
    `DELETE`
*  **URL Params**<br />
    `id=[long]`
* **Success Response:**
  * **Code:** 204 NO CONTENT
  
* **Error Response:**   
  * **Code:** 404 NOT FOUND <br />
    **Content:** 
    ```javascript
    {
        "timestamp": "2021-08-15T22:19:13.413713",
        "message": "Could not find reservation 1"
    }
    ```
