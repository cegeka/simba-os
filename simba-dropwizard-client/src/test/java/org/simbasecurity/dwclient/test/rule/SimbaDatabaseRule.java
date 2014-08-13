package org.simbasecurity.dwclient.test.rule;

import static org.fest.assertions.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import com.google.common.collect.Lists;

public class SimbaDatabaseRule implements MethodRule {

	public static SimbaDatabaseRule create() {
		return new SimbaDatabaseRule();
	}

	private Handle db;

	private SimbaDatabaseRule() {

	}

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before();
				try {
					base.evaluate();
				} finally {
					after();
				}
			}

		};
	}

	private void before() {
		DBI dbi = new DBI("jdbc:mysql://localhost:3306/simba", "simba_local", "simba_local");
		db = dbi.open();
	}

	private void after() {
		db.close();
	}

	public void deleteAllUsersExcept(String... usernames) {
		SimbaDB simbaDB = db.attach(SimbaDB.class);
		simbaDB.deleteUser(Lists.newArrayList(usernames));
	}

	public void createUser(String username) {
		SimbaDB simbaDB = db.attach(SimbaDB.class);
		simbaDB.createUser(username);
	}

	public void assertUserRoleExistsAndCleanUp(String username, String rolename) {
		assertUserRoleExistsInDB(username, rolename, true);
		cleanUpCreatedUserRole(username, rolename);
	}

	private void cleanUpCreatedUserRole(String username, String rolename) {
		db.createStatement(
				" delete from ur"
						+ " using SIMBA_USER_ROLE ur, SIMBA_USER u, SIMBA_ROLE r"
						+ " where ur.user_id = u.id"
						+ " and ur.role_id = r.id"
						+ " and u.username = :username"
						+ " and r.name = :role;"
				)
				.bind("username", username)
				.bind("role", rolename)
				.execute();
	}

	public void assertUserRoleExists(String dummyTestUser, String rolename) {
		assertUserRoleExistsInDB(dummyTestUser, rolename, true);
	}

	public void assertUserRoleDoesNotExist(String dummyTestUser, String rolename) {
		assertUserRoleExistsInDB(dummyTestUser, rolename, false);
	}

	private void assertUserRoleExistsInDB(String username, String rolename, boolean shouldExist) {
		List<Map<String, Object>> userroles = db.createQuery(
				"select ur.*"
						+ " from SIMBA_USER_ROLE ur, SIMBA_USER u, SIMBA_ROLE r"
						+ " where ur.user_id = u.id"
						+ " and ur.role_id = r.id"
						+ " and u.username = :username"
						+ " and r.name = :role;"
				)
				.bind("username", username)
				.bind("role", rolename)
				.list();
		if (shouldExist) {
			assertThat(userroles)
					.describedAs(String.format("Expected role %s, to exist for user %s", rolename, username))
					.isNotEmpty();
		} else {
			assertThat(userroles)
					.describedAs(String.format("Expected role %s, not to exist for user %s", rolename, username))
					.isEmpty();
		}
	}

	public void assertUserDoesNotExist(String username) {
		List<Map<String, Object>> user = db.createQuery(
				"select *"
						+ " from SIMBA_USER"
						+ " where username = :username;"
				)
				.bind("username", username)
				.list();
		assertThat(user)
				.describedAs(String.format("Expected user %s, to not exist", username))
				.isEmpty();
	}

	@UseStringTemplate3StatementLocator
	static interface SimbaDB {

		@SqlUpdate("delete from SIMBA_USER where username not in (<usernames>)")
		public void deleteUser(@BindIn("usernames") List<String> usernames);

		@SqlUpdate("Insert into SIMBA_USER (VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) "
				+ "values (0,0,SYSDATE(),:username,null,0,'en_US',:username,'gaXgdRW18wZZ9dTSRUUB07ShPsk/5d3/Sk+jzg==',0,'ACTIVE',null,:username,0)")
		public void createUser(@Bind("username") String username);
	}

}
