namespace java   org.simbasecurity.api.service.thrift
namespace php    thrift.simba
namespace csharp org.simbasecurity.api.service.thrift

/**
 * An enumeration of possible actions the authentication service can return.
 */
enum ActionType {

    /**
     * The client should create a cookie for the Simba SSO token.
     */
    MAKE_COOKIE                 = 1,

    /**
     * The client should delete the cookie for the Simba SSO token.
     */
    DELETE_COOKIE               = 2,

    /**
     * The client should append the specified parameters to the redirect URL
     */
    ADD_PARAMETER_TO_TARGET     = 3,

    /**
     * The client should redirecto to the specified URL
     */
    REDIRECT                    = 4,

    /**
     * The authentication request succeeded and the client is allowed to set the
     * principal for the request and pass to the actual application.
     */
    DO_FILTER_AND_SET_PRINCIPAL = 5
}

/**
 * The Simba Single Sign-On token. This token is a generated UUID to be stored in a
 * secure HTTP only cookie on the client side and to be passed which each request to
 * identify the user performing the request.
 */
struct SSOToken {

    /**
     * the UUID token string
     */
    1: required string token
}

/**
 * A descriptor containing the result of an authentication request. Client's should use this
 * this descriptor to perform the necessary tasks on the client side.
 */
struct ActionDescriptor {

    /**
     * The set of actions the client should perform.
     */
    1: set<ActionType> actionTypes = {},

    /**
     * A possible map of parameters the client should append to the redirect URL
     */
    2: map<string, string> parameterMap = {},

    /**
     * The resulting SSOToken
     */
    3: SSOToken ssoToken,

    /**
     * An URL to be used as redirect
     */
    4: string redirectURL,

    /**
     * The resulting principal (username)
     */
    5: string principal,

    /**
     * The mapping token
     */
    6: string mappingToken
}

/**
 * The data needed to allow Simba to process an authentication request.
 */
struct RequestData {

    /**
     * A map containing the request parameters
     */
    1:  map<string, string> requestParameters = {},

    /**
     * A map containing the request headers
     */
    2:  map<string, string> requestHeaders = {},

    /**
     * The requested URL
     */
    3:  string requestURL,

    /**
     * The external URL at which Simba is accessible. This is only needed if this
     * differs from the internal URL used to access Simba.
     */
    4:  string simbaWebURL,

    /**
     * The SSOToken from the cookie
     */
    5:  SSOToken ssoToken,

    /**
     * The IP address for the client
     */
    6:  string clientIPAddress,

    /**
     * Identify the request as a logout request.
     */
    7:  bool logoutRequest,

    /**
     * Identify the request as a login request.
     */
    8:  bool loginRequest,

    /**
     * Identify a SSO token mapping provided.
     */
    9:  bool ssoTokenMappingKeyProvided,

    /**
     * Identify the request as a change password request.
     */
    10: bool changePasswordRequest,

    /**
     * Identify the request as a request to display the change password screen.
     */
    11: bool showChangePasswordRequest,

    /**
     * The HTTP method for the request
     */
    12: string requestMethod,

    /**
     * The name of the server processing the request
     */
    13: string hostServerName,

    /**
     * The login token provided
     */
    14: string loginToken,

    /**
     * The success URL for EID
     */
    15: string simbaEidSuccessUrl
}

/**
 * The PolicyDecision is the result of an authorization request.
 */
struct PolicyDecision {
    /**
     * The result of the request. This is true if the user is allowed to perform the action
     * on the resource; false otherwise
     */
    1: required bool allowed,

    /**
     * The timestamp at which the result will become invalid. This timestamp is expressed in
     * milliseconds since epoch (midnight, January 1, 1970 UTC)
     */
    2: required i64 expirationTimestamp
}

/**
 * The AuthenticationFilterService is responsible for checking a request for valid authentication
 * data.
 */
service AuthenticationFilterService {

    /**
     * Process a request using the specified command chain and return a descriptor for the necessary
     * actions to perform.
     *
     * @param requestData  data gathered from the request. This data is used to decide the
     *                     authentication status for the request
     * @param chainCommand a string defining which configured command chain should be used by the
     *                     Simba server to process the request
     * @return an ActionDescriptor containing the actions the calling service should perform
     */
    ActionDescriptor processRequest(1: RequestData requestData, 2: string chainCommand);
}

/**
 * The AuthorizationService is responsible for checking if a user is allowed to perform actions
 * on a specific resource, or if a user has a specific role.
 */
service AuthorizationService {

    /**
     * Check if a user is allowed to perform a specified action on the given resource.
     * @param username     the name of the user performing the action
     * @param resourceName the name of the resource on which the user wants to perform an action
     * @param operation    the operation which the user wants to perform. This should be one of 'read',
     *                     'write', 'create' or 'delete'.
     * @return a PolicyDecision containing the result of the request.
     */
    PolicyDecision isResourceRuleAllowed(1: string username, 2: string resourceName, 3: string operation);

    /**
     * Check if a user is allowed to perform a specified action on the given URL.
     * @param username  the name of the user performing the action
     * @param url       the url on which the user wants to perform an action
     * @param operation the operation which the user wants to perform. This should be one of 'get',
     *                  'post', 'head', 'put', 'delete', 'options' or 'trace'.
     * @return a PolicyDecision containing the result of the request.
     */
    PolicyDecision isURLRuleAllowed(1: string username, 2: string url, 3: string method);

    /**
     * Check if a user is included in the specified role.
     * @param username the name of the user
     * @param roleName the name of the role
     * @return a PolicyDecision containing the result of the request.
     */
    PolicyDecision isUserInRole(1: string username, 2: string roleName)
}

/**
 * Service for managing the internal Simba caches.
 */
service CacheService {

    /**
     * Refresh the entire cache if it is enabled.
     */
    void refreshCacheIfEnabled();

    /**
     * Refresh the cache entries for the specified user, if it is enabled.
     * @param username the name of the user
     */
    void refreshCacheForUserIfEnabled(1: string username);

    /**
     * Check if the internal cache is enabled.
     * @return whether the cache is enabled; or not.
     */
    bool isCacheEnabled();

    /**
     * Enabled or disables the internal cache.
     * @param enable whether the cache should be enabled; or not.
     */
    void setCacheEnabled(bool enable);
}

service ConfigurationService {

    string getValue(1: string parameterName);

    list<string> getListValue(1: string parameterName);

    void changeParameter(1: string parameterName, 2: string value);

    void changeListParameter(1: string parameterName, 2: list<string> values);

    list<string> getConfigurationParameters();

    list<string> getUniqueParameters();

    list<string> getListParameters();
}


struct TSession {
    string ssoToken;
    string userName;
    string clientIpAddress;
    i64 creationTime;
    i64 lastAccessTime;
}

struct TUser {
    i64 id;
    i32 version;
    string userName;
    string name;
    string firstName;
    string inactiveDate;
    string status;
    string successURL;
    string language;
    bool mustChangePassword;
    bool passwordChangeRequired;
}

struct TRole {
    i64 id;
    i32 version;
    string name;
}

struct TPolicy {
    i64 id;
    i32 version;
    string name;
}

struct TGroup {
    i64 id;
    i32 version;
    string name;
    string cn;
}

service SessionService {
    list<TSession> findAllActive();
    void remove(string ssoToken);
    void removeAllBut(string ssoToken);
    TUser getUserFor(string ssoToken);
}

service UserService {
    void addRoles(TUser user, set<TRole> roles);
    TUser create(TUser user);
    TUser createWithRoles(TUser user, list<string> roleNames);
    TUser cloneUser(TUser user, string clonedUsername);

    /**
     * @return the generated password
     */
    string createRestUser(string username);
    list<TUser> findByRole(TRole role);
    list<TUser> findAll();
    list<TGroup> findGroups(TUser user);
    list<TPolicy> findPolicies(TUser user);
    list<TRole> findRoles(TUser user);
    list<TRole> findRolesNotLinked(TUser user);
    TUser refresh(TUser user);

    void removeRole(TUser user, TRole role);
    TUser resetPassword(TUser user);
    list<TUser> search(string searchText);
    TUser update(TUser user);
}