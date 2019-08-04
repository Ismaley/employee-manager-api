# employee-manager-api
A simple employee management api to create employee hierarchies and finding employee's supervisors.

## Technologies used
* Kotlin 1.2.71
* Spring Boot Framework
* Spring JPA / Hibernate
* Spring Security
* H2 in-memory DB

## Building and Running

To build the project run the below command in project's root folder
```
./gradlew clean build
```

To run the application run the below command in project's root folder after building the project
```
cd build/libs && java -jar employee-manager-api-0.0.1-SNAPSHOT.jar
```

## Testing

The project's code is covered with unit and integration tests. The integration tests covers all the operations done by the api along with some error cases as well.

To run the project's tests run the below command in project's root folder
```
./gradlew clean test
```

## Authentication

The project supports in memory authentication and there's an endpoint to perform a login request. There's already an user configured as admin with the following username and pass.

```
curl -i -X POST -d username=admin -d password=admin -c /tmp/cookies.txt \
http://localhost:8080/login

```

The above request returns 200 and a cookie to be used to access the resources of the API.

## API documentation
Application exposes following REST endpoints.

| Http method | Endpoint                                            | Description                                                    |
|-------------|-----------------------------------------------------|----------------------------------------------------------------|
| POST        | /api/employees                                      | Creates a new employee hierarchy                               |
| GET         | /api/employees/supervisors?employeeName={name}      | Retrieves the employee's supervisor and it's supervisors       |


### POST /api/employees

Creates a new employee hierarchy

Sample request:
```json
{
    "Pete": "Nick",
    "Barbara": "Nick",
    "Nick": "Sophie",
    "Sophie": "Jonas"
}
```

Sample success response with status code 201:
```json
{
	"Jonas": {
		"Sophie": {
			"Nick": {
				"Pete": {},
				"Barbara": {}
			}
		}
	}
}
```
Sample failure response with status code 400:
```json
{
    "reason": "Invalid employee hierarchy",
    "message": "Employee hierarchy contains a cyclic dependency, hierarchy must have a boss."
}
```

#### endpoint validations

* Returns status code: 400 if hierarchy contains a cyclic dependency.
* Returns status code: 400 if hierarchy contains more than one boss.
* Returns status code: 400 if hierarchy is empty.
* Returns status code: 400 if hierarchy contains an employee without a supervisor associated.


### POST /api/employees/supervisors?employeeName={name}

Retrieves the employee's supervisor and it's supervisors

Sample success response with status code 200 for employeeName = "Barbara":
```json
{
    "Barbara": "Nick",
    "Nick": "Sophie",
    "Sophie": "Jonas"
}
```
Sample failure response with status code 400:
```json
{
    "reason": "Invalid search param",
    "message": "Employee name must not be blank."
}
```

Sample failure response with status code 404 for employeeName = "Mike":
```json
{
    "reason": "Employee not found",
    "message": "Employee Mike not found"
}
```
#### endpoint validations

* Returns status code: 400 if search name is empty.
* Returns status code: 404 if employee does not exists.

