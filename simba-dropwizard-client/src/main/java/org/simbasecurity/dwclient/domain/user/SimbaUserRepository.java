package org.simbasecurity.dwclient.domain.user;

import static com.google.common.base.Preconditions.*;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.hibernate.AbstractDAO;

/**
 * Has own tx mgmt cuz injecting multiple UnitOfWorkDispatchers impossibru
 */
public class SimbaUserRepository extends AbstractDAO<SimbaUser> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private SessionFactory sessionFactory;

	@Inject
	public SimbaUserRepository(@Named("simbaSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
		this.sessionFactory = sessionFactory;
	}

	public void save(SimbaUser simbaUser) {
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(checkNotNull(simbaUser));
			tx.commit();

		} catch (HibernateException e) {
			logger.error("Could not persist simbaUser", e);
			if (tx != null) {
				tx.rollback();
			}
			throw e;

		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void delete(String simbaId) {
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.createQuery("delete from SimbaUser where userName = :username").setString("username", simbaId).executeUpdate();
			tx.commit();

		} catch (HibernateException e) {
			logger.error("Could not delete simbaUser", e);
			if (tx != null) {
				tx.rollback();
			}
			throw e;

		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public SimbaUser getSimbaUser(String simbaID) {
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			SimbaUser simbaUser = (SimbaUser) session.createQuery("FROM SimbaUser WHERE username = \'" + simbaID + "'").list().get(0);
			return simbaUser;
		} catch (HibernateException e) {
			logger.error("Could not delete simbaUser", e);
			if (tx != null) {
				tx.rollback();
			}
			throw e;

		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

}
