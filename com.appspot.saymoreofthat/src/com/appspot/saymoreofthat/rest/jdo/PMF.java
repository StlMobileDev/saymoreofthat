package com.appspot.saymoreofthat.rest.jdo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class PMF {
	private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {}

	
	public static PersistenceManager getPersistenceManager() {
		return pmfInstance.getPersistenceManager();
	}
}
