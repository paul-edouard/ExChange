package com.coherentlogic.coherent.data.model.core.builders;

/**
 * Specification for the HTTP methods that are supported by implementation of
 * this interface.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 *
 * @see <a href="http://docs.oracle.com/javaee/6/api/javax/servlet/http/
 *  HttpServlet.html">javax.servlet.http.HttpServlet</a>
 *
 * @see <a href="http://static.springsource.org/spring/docs/3.0.x/api/org/
 *  springframework/web/client/RestTemplate.html">RestTemplate</a>
 */
public interface HttpMethodsSpecification {

    /**
     * Method executes an HTTP get and returns the result.
     *
     * @param type The domain class type to return. 
     *
     * @return An instance of type <i>type</i>.
     */
    <T> T doGet (Class<T> type);
}
