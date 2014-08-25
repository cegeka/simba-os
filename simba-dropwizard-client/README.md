## Simba Dropwizard Client

This module for Simba provides `Injectables` and `Gateways` that bind together Dropwizard 0.6 and Simba.

### Usage

#### @Authenticated

An `Injectable` so you'll have access to a `SimbaPrincipal` object that was successfully authenticated by Simba.

#### SimbaManagerRestConfiguration

A Dropwizard `Configuration` class necessary to configure connecting to Simba's Thrift services and REST manager via a YAML file/block.

An example .yml file can be found in `src/test/resources/test-simba-rest-mgr.yml`.

#### Error handling

Overview of exceptions thrown, what they mean and how you should treat them.

### Set-up

There are some important things you need to configure in Simba before you can actually use this Dropwizard client.

#### Provide an application user access to the Simba Manager REST services

Because we use these REST services to add roles.

#### Configure a specific chainContext command chain

Because the default credential command chain of the Reference Implementation does some extra stuff that a non Servlet stack can't provide, you'll need some specific chains.

More specifically, you'll need a

* loginAuthChain
* logoutChain
* and a sessionChain

These are all used in the `SimbaGateway`.

Your chainContext.xml should contain the chains as such:

```xml
<bean id="loginAuthChain" class="org.simbasecurity.core.chain.ChainImpl">
  <property name="commands">
    <list>
      <ref bean="validateRequestParametersCommand" />
      <ref bean="checkUserActiveCommand" />
      <ref bean="jaasLoginCommand" />
      <ref bean="checkAccountBlockedCommand" />
      <ref bean="checkPasswordExpiredCommand" />
      <ref bean="createSessionCommand" />
    </list>
  </property>
</bean>

<bean id="logoutChain" class="org.simbasecurity.core.chain.ChainImpl">
  <property name="commands">
    <list>
      <ref bean="checkSessionCommand" />
      <ref bean="logoutCommand" />
    </list>
  </property>
</bean>

<bean id="sessionChain" class="org.simbasecurity.core.chain.ChainImpl">
  <property name="commands">
    <list>
      <ref bean="checkSessionCommand" />
      <ref bean="logoutCommand" />
      <ref bean="createCookieForNewSSOTokenCommand" />
      <ref bean="URLRuleCheckCommand" />
      <ref bean="checkShowChangePasswordCommand" />
      <ref bean="enterApplicationCommand" />
    </list>
  </property>
</bean>
```

Note that the manager's chain is configured through Simba itself via the simba.properties which will contain

    simba.manager.authorization.chain.name=managerAuthorizationChain

### Dependencies

#### Guava

[Guava](http://code.google.com/p/guava-libraries/) adds a little bit of a functional programming flavor to your code. Mainly used for the `Collections` and `Optional` utilities.


#### Guice

[Guice](http://code.google.com/p/google-guice/) provides light-weight Dependency Injection, and integrates nicely with Dropwizard.

#### AssertJ

[AssertJ](http://joel-costigliola.github.io/assertj/) is a follow up of the discontinued Fest Assert project. Makes our test assertions more readible thanks to its *Fluent Api* and has excellent helper methods for more easily asserting collections.
