package org.simbasecurity.client.filter.action;

/**
 * Extension interface allowing to plug in an extension into the
 * {@link DoFilterExtension} class.
 */
public interface DoFilterExtension {
    /**
     * Method called before the doFilter in called
     */
    void before();
    void after();
}
