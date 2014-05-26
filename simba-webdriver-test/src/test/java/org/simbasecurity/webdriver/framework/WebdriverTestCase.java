package org.simbasecurity.webdriver.framework;

import junit.framework.TestCase;

import static org.simbasecurity.webdriver.SimbaUrl.DEFAULT_POSTDEPLOY_URL;
import static org.simbasecurity.webdriver.SimbaUrl.SIMBA_ZOO;


public abstract class WebdriverTestCase extends TestCase {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                quitBrowser();
            }
        }));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Browser.open();
    }

    protected String getSimbaZooURL() {
        return DEFAULT_POSTDEPLOY_URL + SIMBA_ZOO;
    }

    @Override
    protected void tearDown() throws Exception {
        quitBrowser();
        super.tearDown();
    }

    private static void quitBrowser() {
        sleepAWhileToAllowPendingCommandsToFinish();
        Browser.quit();
    }

    private static void sleepAWhileToAllowPendingCommandsToFinish() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignore) {
        }
    }
}