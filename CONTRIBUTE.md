# Contributing

## Building Simba

Prerequisites for Simba:
* GIT
* Java 8

Simba Manager technology stack:
* AngularJS
* NodeJS
* Grunt
* Bower

1. Simba has a dependency on the thrift compiler (v0.9.1). The windows thrift compiler is provided via this code repository, but to
build Simba on another OS, you will need to provide the compiler. Check the [Apache Thrift website](http://thrift.apache.org/)
how to get a compiler for your system.

After that you can add a profile for you OS in the parent pom.xml providing the location of the thrift compiler.

2. You need to add C:\<simba_dir>\simba-api\target\generated-sources\thrift\java to your classpath

3. Add following lines to your maven settings file:
```xml
  <servers>
    <server>
      <id>nexus</id>
      <username>simba</username>
      <password>simba</password>
    </server>
    <server>
      <id>simba-snapshots</id>
      <username>simba</username>
      <password>simba</password>
    </server>
    <server>
      <id>simba-releases</id>
      <username>simba</username>
      <password>simba</password>
    </server>
  </servers>

  <mirrors>
    <mirror>
      <id>nexus</id>
      <mirrorOf>*</mirrorOf>
      <url>http://nexus.cegeka.be/nexus/content/groups/public</url>
    </mirror>
  </mirrors>
  
  <profiles>
    <profile>
      <id>nexus</id>
      <repositories>
        <repository>
          <id>nexus</id>
          <url>http://nexus</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>nexus</id>
          <url>http://nexus</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>nexus</activeProfile>
  </activeProfiles>
```

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

Optional modules are (use jetty run):
- *Simba-manager*: for when you want to test user management.
- *Simba-zoo*: to see how the selected roles can affect the behaviour of an application.

## Locally verifying your changes

Run a `mvn clean install` from the simba-os root dir.

## Releasing a new version

Sometimes changes require the major/minor version to be bumped, and a new release is necessary.

Making a Simba release, ie. deploying the artifacts to the Simba nexus repository relies on Maven and the `maven-release-plugin`.

The `maven-release-plugin` takes care of the following things:
* automatic version number bump
* git tag
* publishing to nexus

### Making a SNAPSHOT release

To make a snapshot release run the `mvn deploy` goal. This will build, test and package the local sources and publish these artifacts to the configured nexus snapshot repository.

This is typically used in a scenario where you have extended Simba OS with your custom implementation. 
You can then check that changes to a Simba OS snapshot work with your custom implementation that depends on it.

Once you've verified that your custom implementation and Simba OS snapshot can collaborate properly, you're ready to make an official release.

### Making an OFFICIAL release

An official release is a multi-step process.

1. Run a build with tests: `mvn clean install`, make sure it is successful.

2. Commit and push all local changes

3. Run the maven goal: `mvn release:prepare`

   This will prompt for the release version, SCM tag and next snapshot version. Afterwards it will update the pom
   files, perform a `git tag`, and prepare a `release.properties` file for next step.
   
   You will have to do a `git push` yourself still.

4. Run the maven goal: `mvn release:perform -DuseReleaseProfile=false`

   This will perform the previously prepared release. This means uploading all artifacts to the nexus **release**
   repository.
   
   The `-DuseReleaseProfile=false` argument is needed because otherwhise the maven-javadoc-plugin will cause trouble

5. Go to the [SimbaOS releases on GitHub](https://github.com/cegeka/simba-os/releases) and draft a new release. 

   Choose the correct, **existing** tag for the release. Use _Release x.y.z_ as release title and properly describe the changes. 
   
   Do not forget to mention
   the earliest compatible version for the api and the client. If there are no breaking changes in current release
   these can be copied from the previous release. 
   
   Check the _pre-release_ checkbox if current release is not production ready. You can uncheck when the release has successfully passed the acceptance phase.
