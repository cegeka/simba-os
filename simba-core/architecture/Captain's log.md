# Captain's log
## February 2018 - Proper error messages in SimbaManager
Starting situation: errors in SimbaManager web are extremely generic and provide no information to the user of what happened.
For example, when an email is required, email already exists, username too short/long, or even optimistic locking occurred, the message on the web page says _something went wrong creating/updating data_.

To decrease work load for 1st line support, proper error messages are a must.

There are a couple of hurdles we need to overcome before we can pass domain validations (from simba-core) to the SimbaManager web app:

* The Thrift services all need to throw a specific error type e.g. `TSimbaError`.
* The `ServiceImpl`s (e.g. `UserServiceImpl`) should catch `SimbaException`s and throw `TSimbaError`s instead.
* The `BaseRESTService` needs to take into account `TSimbaError`s and throw `SimbaManagerRuntimeException`s.
* Then we'll need a generic way of translating those `SimbaManagerRuntimeException`s into JSON.
* And only then can we start to deal with those JSON error types in AngularJS and use the `error.json` files to map and translate those messages.

Worth noting is that we're breaking the API again with these changes, so simba _customers_ will need to update their simba-api and simba-client jars.

## December 2017 - Reset password via mail
Since GDPR will go live in may 2018 we are looking at potential security risks. 
One of these risks is using a default password when resetting a password.

We have implemented a more secure system where the user will receive a mail whereby he can reset his password.
![Image about reset password via mai](SVF-3454-Simba.png "Reset password via mail")
