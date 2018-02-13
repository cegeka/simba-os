# SimbaManagerException framework
* **Old situation**: In the backend while creating or updating a user, we do a few validations. If one of the validations fail, we throw a SimbaException. 
    Which is visible in the server log. In the frontend we just check if something went wrong and if so, we throw general error which says f.e. something went wrong during the update of the data.
   *The problem is that its unclear to a user, what exactly went wrong.*
* **New situation**: The validation still happens, but now we throw the error to the frontend. (see image below)

## Error message flow

![Error message flow](ErrorMessagesSimba.jpg)

## Glossary

`SimbaExceptionThriftHandler`: maps SimbaExceptions to TSimbaErrors with correct errorkeys values.

`SimbaExceptionHandlingCaller`: Uses handler and is injected into SimbaCore services for easier testing.

`BaseRESTService`: catches TSimbaErrors and rethrows `SimbaManagerExceptions`.

`SimbaManagerExceptionHandler`: Translates `SimbaManagerException`s to JSON using a `@ControllerAdvice` and a `ResponseEntity`.

## Spring docs material we used

[MVC ExceptionHandlers](https://docs.spring.io/spring/docs/4.3.6.RELEASE/spring-framework-reference/htmlsingle/#mvc-exceptionhandlers)

[ResponseEntityExceptionHandler](https://docs.spring.io/spring/docs/4.3.6.RELEASE/javadoc-api/index.html?org/springframework/web/servlet/mvc/method/annotation/ResponseEntityExceptionHandler.html)

[ControllerAdvice](https://docs.spring.io/spring/docs/4.3.6.RELEASE/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)


