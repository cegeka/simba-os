# Contributing

## Components

| Component | Description |
| ---       | ---         |
| simba-api | The API is used to determine what gets exposed between `client` and `core` components |
| simba-client | Since Simba conceptually uses the _agent model_, this component is to be used as an _agent_ in the web application that wants to interface with Simba. e.g. This component contains a ServletFilter that intercepts http communication and does authentication and authorization checks with Simba. |
| simba-core | The core component of Simba containing User and Identity management. |
| simba-dropwizard-client | Another client to be used in a DropWizard Application |
| simba-manager | A UI component written in AngularJS, that you can use to manage Users, Roles etc. It also provides rest services to perform various actions. |
| simba-ri | The _Reference Implementation_, this implies you can create your own implementation, and use the `api` and `core` components as an _SPI_. This component contains example database creation scripts which you'll need to have a working Simba implementation. |
| simba-webdriver-test | A component that will run webdriver tests to verify that Simbas features will keep on working. It runs against a running `simba-zoo` webapplication. |
| simba-zoo | Just a dummy web application that is secured via the `simba-ri` component. So this should contain configuration of simba users etc. |
| thrift | A directory with Thrift binaries. Thrift is used to generate cross-platform serializable objects to interface with `simba-api`. |
