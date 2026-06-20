package ch.chattrix.shared.rabbitmq;

public class RoutingKeys {
    public static final String AUTH_REGISTER = "auth.register";
    public static final String AUTH_LOGIN = "auth.login";
    public static final String AUTH_LOGOUT = "auth.logout";
    public static final String AUTH_REFRESH = "auth.refresh";
    public static final String AUTH_GET_EMAIL = "auth.get.email";
    public static final String AUTH_EDIT_CREDENTIAL = "auth.edit.credential";
    public static final String USER_REGISTER = "user.register";
    public static final String USER_GET_ALL = "user.get.all";
    public static final String USER_EDIT_USERNAME = "user.edit.username";
    public static final String USER_GET_BASE_DATA = "user.get.base.data";
    public static final String AUTH_RESULT_REGISTER = "auth.result.register";
    public static final String AUTH_RESULT_LOGIN = "auth.result.login";
    public static final String AUTH_DELETE = "auth.delete";
    public static final String USER_DELETE = "user.delete";
    public static final String AUTH_RESULT_LOGOUT = "auth.result.logout";
    public static final String AUTH_RESULT_REFRESH = "auth.result.refresh";
    public static final String AUTH_RESULT_GET_EMAIL = "auth.result.get.email";
    public static final String AUTH_RESULT_EDIT_CREDENTIAL = "auth.result.edit.credential";
    public static final String USER_RESULT_REGISTER = "user.result.register";
    public static final String USER_RESULT_GET_ALL = "user.result.get.all";
    public static final String USER_RESULT_EDIT_USERNAME = "user.result.edit.username";
    public static final String USER_RESULT_DELETE = "user.result.delete";
    public static final String AUTH_RESULT_DELETE = "auth.result.delete";
    public static final String USER_RESULT_GET_BASE_DATA = "user.result.get.base.data";
}