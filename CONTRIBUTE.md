# Contributing

## Components

| Component | Description | Deployable |
| ---       | ---         | ---              |
| simba-api | The API is used to determine what gets exposed between `client` and `core` components. | :x: |
| simba-client | Since Simba conceptually uses the _agent model_, this component is to be used as an _agent_ in the web application that wants to interface with Simba. e.g. This component contains a ServletFilter that intercepts http communication and does authentication and authorization checks with Simba. | :x: |
| simba-core | The core component of Simba containing User and Identity management. | :x: |
| simba-dropwizard-client | Another client to be used in a DropWizard Application. | :x: |
| simba-manager | A UI component written in AngularJS, that you can use to manage Users, Roles etc. It also provides rest services to perform various actions. These REST services are also used by the AngularJS web application and use Thrift bindings to talk to the `simba-ri` component. | :white_check_mark: |
| simba-ri | The _Reference Implementation_, this implies you can create your own implementation, and use the `api` and `core` components as an _SPI_. This component contains example database creation scripts which you'll need to have a working Simba implementation. | :white_check_mark: |
| simba-webdriver-test | A component that will run webdriver tests to verify that Simbas features will keep on working. It runs against a running `simba-zoo` webapplication. | :white_check_mark: |
| simba-zoo | Just a dummy web application that is secured via the `simba-ri` component. So this should contain configuration of simba users etc. | :white_check_mark: |
| thrift | A directory with Thrift binaries. Thrift is used to generate cross-platform serializable objects to interface with `simba-api`. | :x: |

## Running the different applications locally
### Use the Maven jetty plugin
Every deployable component (see above) has a Maven `jetty` plugin target to run a local jetty container and deploy the component. Look for a block that looks like this:

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>9.4.0.v20161208</version>
    <configuration>
        <httpConnector>
            <port>8085</port>
        </httpConnector>
        <webApp>
            <contextPath>/${project.artifactId}</contextPath>
        </webApp>
        <stopPort>8086</stopPort>
        <stopKey>jetty-stop</stopKey>
        <scanIntervalSeconds>10</scanIntervalSeconds>
        <systemProperties>
            <systemProperty>
                <name>simba.properties.file</name>
                <value>${basedir}/src/main/resources/simba.properties</value>
            </systemProperty>
        </systemProperties>
    </configuration>
</plugin>
```
To start a module: navigate to the module and execute `mvn jetty:run`.


### Build a War and deploy on an AppServer of your choice
Use Maven to build a war e.g. from the commandline via:

```
simba-os/simba-ri> $ mvn clean war
```

This will also compile `simba-core` and `simba-client` components.

### Automatic DBScript execution
Some components come with database scripts that are run automatically on application startup. Automatic run is configured in the respective `persistenceContext.xml` file.

* [simba-core/.../create_db.sql](simba-core/src/main/resources/db/hsqldb-embedded/scripts/create_db.sql)
* [simba-core/.../insert_parameters.sql](simba-core/src/main/resources/db/hsqldb-embedded/scripts/insert_parameters.sql)
* [simba-ri/.../insert_test_data.sql](simba-ri/src/main/resources/db.hsqldb-embedded.scripts/insert_test_data.sql)

### Modules
When running simba locally you'll always need to deploy *Simba-ri*.

Optional modules are:
- *Simba-manager*: for when you want to test user management.
- *Simba-zoo*: to see how the selected roles can affect the behaviour of an application.