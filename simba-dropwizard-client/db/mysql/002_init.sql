delete from SIMBA_SESSION;
delete from SIMBA_USER_ROLE;
delete from SIMBA_POLICY_SIMBA_ROLE;
delete from SIMBA_RULE;
delete from SIMBA_ROLE;
delete from SIMBA_POLICY;
delete from SIMBA_USER;

commit;

/* accept any string, since we validate e-mail elsewhere */
update SIMBA_PARAMETER set PARAMETER_VALUE = '.*' where PARAMETER_KEY = 'USERNAME_REGEX';
update SIMBA_PARAMETER set PARAMETER_VALUE = '80' where PARAMETER_KEY = 'USERNAME_MAX_LENGTH';

/* Simba stuff */
Insert into SIMBA_POLICY (VERSION,NAME) values (0,'simba manager url');
Insert into SIMBA_POLICY (VERSION,NAME) values (0,'simba messagebroker url');
Insert into SIMBA_POLICY (VERSION,NAME) values (0,'Simba Manager REST API');
Insert into SIMBA_POLICY (VERSION,NAME) values (0,'simba mgmt read');
Insert into SIMBA_POLICY (VERSION,NAME) values (0,'simba mgmt write');

/* Simba manager rules */
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE)
	select 0,'Simba Manager Services','*/simba/manager/*',p.id,1,1,'URL'
	from SIMBA_POLICY p
	where name = 'Simba Manager REST API';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) 
	select 0,'management roles write Permission','manage-roles',p.id,1,1,1,1,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba mgmt write';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) 
	select 0,'management users write Permission','manage-users',p.id,1,1,1,1,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba mgmt write';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) 
	select 0,'management policies write Permission','manage-policies',p.id,1,1,1,1,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba mgmt write';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) 
	select 0,'management groups write Permission','manage-groups',p.id,1,1,1,1,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba mgmt write';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) 
	select 0,'management roles read Permission','manage-roles',p.id,0,0,1,0,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba mgmt read';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) 
	select 0,'management users read Permission','manage-users',p.id,0,0,1,0,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba mgmt read';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) 
	select 0,'management policies read Permission','manage-policies',p.id,0,0,1,0,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba mgmt read';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE) 
	select 0,'management groups read Permission','manage-groups',p.id,0,0,1,0,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba mgmt read';

Insert into SIMBA_ROLE (VERSION,NAME) values (0,'simba-admin');

/* Other Simba rules */
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE)
	select 0,'SIMBA MANAGER','*/simba-manager/*',p.id,1,1,'URL'
	from SIMBA_POLICY p
	where name = 'simba manager url';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,GET_ALLOWED,POST_ALLOWED,RULE_TYPE)
	select 0,'SIMBA messagebroker','*/messagebroker/*',p.id,1,1,'URL'
	from SIMBA_POLICY p
	where name = 'simba messagebroker url';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE)
	select 0, 'session tab visible','sessiontabvisible',p.id,1,1,1,1,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba manager url';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE)
	select 0, 'configuration tab visible','configurationtabvisible',p.id,1,1,1,1,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba manager url';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE)
	select 0, 'session tab visible messagebroker','sessiontabvisible',p.id,1,1,1,1,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba messagebroker url';
Insert into SIMBA_RULE (VERSION,NAME,RESOURCENAME,POLICY_ID,CREATE_ALLOWED,DELETE_ALLOWED,READ_ALLOWED,WRITE_ALLOWED,RULE_TYPE)
	select 0, 'configuration tab visible messagebroker','configurationtabvisible',p.id,1,1,1,1,'RESOURCE'
	from SIMBA_POLICY p
	where name = 'simba messagebroker url';

/* Bootstrapped users */
Insert into SIMBA_USER (VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) 
values (0,0,SYSDATE(),'App User',null,0,'en_US','App User','kfgeBjyH2whHVozi5JU1zoXGxwc2BisEjNaFlQ==',0,'ACTIVE',null,'appUser',0);

/* Users with the simba-manager role are allowed to access the Simba Manager URL */
Insert into SIMBA_POLICY_SIMBA_ROLE (POLICY_ID,ROLE_ID) 
	select p.id, r.id
	from SIMBA_POLICY p, SIMBA_ROLE r
	where p.name in ('Simba Manager REST API','simba mgmt read','simba mgmt write')
	and r.name in ('simba-admin');
	
/* All appUsers that are allowed to access the Simba Manager REST services */
Insert into SIMBA_USER_ROLE (USER_ID,ROLE_ID) 
	select u.id, r.id 
	from SIMBA_USER u, SIMBA_ROLE r
	where r.name='simba-admin'
	and u.username in ('appUser');

commit;
