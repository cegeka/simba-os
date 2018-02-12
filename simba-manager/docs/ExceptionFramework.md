# SimbaManagerException framework
## Glossary

`SimbaExceptionThriftHandler`: maps SimbaExceptions to TSimbaErrors with correct errorkeys values.

`SimbaExceptionHandlingCaller`: Uses handler and is injected into SimbaCore services for easier testing.

`BaseRESTService`: catches TSimbaErrors and rethrows `SimbaManagerExceptions`.

`SimbaManagerExceptionHandler`: Translates `SimbaManagerException`s to JSON using a `@ControllerAdvice` and a `ResponseEntity`.

## Spring docs material we used

[MVC ExceptionHandlers](https://docs.spring.io/spring/docs/4.3.6.RELEASE/spring-framework-reference/htmlsingle/#mvc-exceptionhandlers)

[ResponseEntityExceptionHandler](https://docs.spring.io/spring/docs/4.3.6.RELEASE/javadoc-api/index.html?org/springframework/web/servlet/mvc/method/annotation/ResponseEntityExceptionHandler.html)

[ControllerAdvice](https://docs.spring.io/spring/docs/4.3.6.RELEASE/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)


