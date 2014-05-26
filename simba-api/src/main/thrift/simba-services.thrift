namespace java   org.simbasecurity.api.service.thrift
namespace php    thrift.simba
namespace csharp org.simbasecurity.api.service.thrift

enum ActionType {
    MAKE_COOKIE                 = 1,
    DELETE_COOKIE               = 2,
    ADD_PARAMETER_TO_TARGET     = 3,
    REDIRECT                    = 4,
    DO_FILTER_AND_SET_PRINCIPAL = 5
}

struct SSOToken {
    1: required string token
}

struct ActionDescriptor {
    1: set<ActionType> actionTypes,
    2: map<string, string> parameterMap,
    3: SSOToken ssoToken,
    4: string redirectURL,
    5: string principal
}

struct RequestData {
    1:  map<string, string> requestParameters,
    2:  map<string, string> requestHeaders,
    3:  string requestURL,
    4:  string simbaWebURL,
    5:  SSOToken ssoToken,
    6:  string clientIPAddress,
    7:  bool logoutRequest,
    8:  bool loginRequest,
    9:  bool ssoTokenMappingKeyProvided,
    10: bool changePasswordRequest,
    11: bool showChangePasswordRequest,
    12: string requestMethod,
    13: string hostServerName,
    14: string loginToken
}

struct PolicyDecision {
    1: required bool allowed,
    2: required i64 expirationTimestamp
}

service AuthenticationFilterService {
    ActionDescriptor processRequest(1: RequestData requestData, 2: string chainCommand);
}

service AuthorizationService {
    PolicyDecision isResourceRuleAllowed(1: string username, 2: string resourceName, 3: string operation);
    PolicyDecision isURLRuleAllowed(1: string username, 2: string resourceName, 3: string method);
    PolicyDecision isUserInRole(1: string username, 2: string roleName)
}
