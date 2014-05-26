Insert into SIMBA_POLICY (ID,VERSION,NAME) values (1,0,'zoo URL');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (2,0,'generalSheetReadPolicy');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (3,0,'generalSheetWritePolicy');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (4,0,'financialDataReadPolicy');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (5,0,'financialDataWritePolicy');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (6,0,'managementReadPolicy');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (7,0,'managementWritePolicy');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (8,0,'SessionTabPolicy');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (9,0,'ConfigurationTabPolicy');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (10,0,'PHP Test URL');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (11,0,'simba manager url');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (12,0,'simba messagebroker url');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (13,0,'Jersey Test URL');
Insert into SIMBA_POLICY (ID,VERSION,NAME) values (14,0,'Simba Manager Services URL');

Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE) values (1,0,'Simba Zoo URL','*/simba-zoo/*',1,1,1,'URL');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE) values (2,0,'Simba PHP Test URL','*.php',1,1,1,'URL');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE) values (3,0,'SIMBA MANAGER','*/simba-manager/*',11,1,1,'URL');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE) values (4,0,'SIMBA messagebroker','*/messagebroker/*',12,1,1,'URL');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE) values (5,0,'Simba Jersey Test URL','*/simba/jersey/*',13,1,1,'URL');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE) values (6,0,'Simba Manager Services','*/simba/manager/*',14,1,1,'URL');

Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (101,0,'generalSheet read Permission','aansluitingen',2,0,0,1,0,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (102,0,'generalSheet write Permission','aansluitingen',3,1,1,1,1,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (103,0,'financialData read Permission','algemeen',4,0,0,1,0,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (104,0,'financialData write Permission','algemeen',5,1,1,1,1,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (105,0,'management roles read Permission','manage-roles',6,0,0,1,0,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (106,0,'management roles write Permission','manage-roles',7,1,1,1,1,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (107,0,'management users read Permission','manage-users',6,0,0,1,0,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (108,0,'management users write Permission','manage-users',7,1,1,1,1,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (109,0,'management policies read Permission','manage-policies',6,0,0,1,0,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (110,0,'management policies write Permission','manage-policies',7,1,1,1,1,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (111,0,'management groups read Permission','manage-groups',6,0,0,1,0,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (112,0,'management groups write Permission','manage-groups',7,1,1,1,1,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (113,0,'management sessions read Permission','manage-sessions',8,0,0,1,0,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (114,0,'management sessions write Permission','manage-sessions',8,1,1,1,1,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (115,0,'management configuration read Permission','manage-configuration',9,0,0,1,0,'RESOURCE');
Insert into SIMBA_RULE (ID,VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) values (116,0,'management configuration write Permission','manage-configuration',9,1,1,1,1,'RESOURCE');

Insert into SIMBA_ROLE (ID,VERSION,NAME) values (1,0,'zoo user');
Insert into SIMBA_ROLE (ID,VERSION,NAME) values (2,0,'junior');
Insert into SIMBA_ROLE (ID,VERSION,NAME) values (3,0,'senior');
Insert into SIMBA_ROLE (ID,VERSION,NAME) values (4,0,'admin');

Insert into SIMBA_USER (ID,VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) values (1,1,0,TIMESTAMP('2011-12-11'),'Admin',null,0,'nl_NL','Admin','TqXiRtMAW6CRV/qCs2e6fUV4tRahHTTnjFzwfA==',0,'ACTIVE',null,'admin',0);
Insert into SIMBA_USER (ID,VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) values (2,1,0,TIMESTAMP('2011-12-11'),'Guest',null,1,'nl_NL','Guest','TqXiRtMAW6CRV/qCs2e6fUV4tRahHTTnjFzwfA==',0,'ACTIVE',null,'guest',0);
Insert into SIMBA_USER (ID,VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) values (3,1,0,TIMESTAMP('2011-12-11'),'John',null,0,'nl_NL','Senior','TqXiRtMAW6CRV/qCs2e6fUV4tRahHTTnjFzwfA==',0,'ACTIVE',null,'johns',0);
Insert into SIMBA_USER (ID,VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) values (4,1,0,TIMESTAMP('2011-12-11'),'Jeff',null,0,'nl_NL','Junior','TqXiRtMAW6CRV/qCs2e6fUV4tRahHTTnjFzwfA==',0,'ACTIVE',null,'jeffj',0);
Insert into SIMBA_USER (ID,VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) values (5,1,0,TIMESTAMP('2011-12-11'),'simbaht',null,0,'nl_NL','simbaht','$apr1$q5fpwm63$5qK4aaNGxPKt7qGl/GzWB/',0,'ACTIVE',null,'simbaht',0);
Insert into SIMBA_USER (ID,VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) values (6,1,0,TIMESTAMP('2011-12-11'),'Groupie',null,0,'nl_NL','Groupie','TqXiRtMAW6CRV/qCs2e6fUV4tRahHTTnjFzwfA==',0,'ACTIVE',null,'groupie',0);
Insert into SIMBA_USER (ID,VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) values (7,1,1,TIMESTAMP('2011-12-11'),'DummyFirstName1',null,0,'nl_NL','DummyLastName1','TqXiRtMAW6CRV/qCs2e6fUV4tRahHTTnjFzwfA==',1,'ACTIVE',null,'changePwdUser1',0);
Insert into SIMBA_USER (ID,VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) values (8,1,1,TIMESTAMP('2011-12-11'),'DummyFirstName2',null,0,'nl_NL','DummyLastName2','TqXiRtMAW6CRV/qCs2e6fUV4tRahHTTnjFzwfA==',1,'ACTIVE',null,'changePwdUser2',0);

Insert into SIMBA_GROUP (ID, VERSION,NAME, CN) values (1, 0,'rockBand', 'CN=GL-Cegeka-Medewerkers,OU=Global,OU=Beheer,OU=Groups,OU=BE,OU=Cegeka');
Insert into SIMBA_GROUP (ID, VERSION,NAME, CN) values (2, 0,'Ventouris', 'CN=Ventouris Vernieuwing Team,OU=Global,OU=Distribution Lists,OU=Groups,OU=BE,OU=Cegeka');

Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (1,1);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (1,2);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (1,3);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (1,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (2,2);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (2,3);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (2,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (3,2);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (3,3);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (3,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (4,2);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (4,3);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (4,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (5,3);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (5,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (6,3);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (6,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (7,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (8,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (10,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (11,2);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (11,3);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (11,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (12,1);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (13,4);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (14,2);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (14,3);
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) values (14,4);



Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (1,1);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (1,4);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (2,1);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (3,1);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (7,1);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (8,1);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (3,3);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (4,1);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (4,2);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (5,1);
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) values (5,4);

Insert into SIMBA_GROUP_ROLE (GROUP_ID,ROLE_ID) values (1,4);
Insert into SIMBA_GROUP_ROLE (GROUP_ID,ROLE_ID) values (2,4);
Insert into SIMBA_USER_GROUP (USER_ID,GROUP_ID) values (6,1);
