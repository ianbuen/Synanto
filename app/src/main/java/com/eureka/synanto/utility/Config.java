package com.eureka.synanto.utility;

public class Config {

//    public static final String IP_ADDRESS = "192.168.8.11";
//    public static final String IP_ADDRESS = "192.168.0.11";
    public static final String IP_ADDRESS = "192.168.43.83";

    public static final String MAIN_URL = "http://" + IP_ADDRESS + "/synanto/";

    public static final String LOGIN_URL = MAIN_URL + "login.php";
    public static final String REGISTER_URL = MAIN_URL + "register.php";

    public static final String NEW_EVENT_URL = MAIN_URL + "new_event.php";
    public static final String GET_USERS_URL = MAIN_URL + "get_users.php";
    public static final String ADD_MEMBER_URL = MAIN_URL + "add_member.php";
    public static final String ALL_EVENTS_URL = MAIN_URL + "get_all_events.php";
    public static final String EVENT_DETAILS = MAIN_URL + "get_event_details.php";
    public static final String GET_JOIN_LIST = MAIN_URL + "get_join_list.php";
    public static final String JOIN_EVENT = MAIN_URL + "join_event.php";
    public static final String GET_MEMBERS = MAIN_URL + "get_members.php";

    public static final String REQUEST_ASTAR = MAIN_URL + "astar/index.php";
    public static final String UPDATE_LOCATION = MAIN_URL + "update_loc.php";
    public static final String GET_LOCATION = MAIN_URL + "get_loc.php";
    public static final String GET_VENUE = MAIN_URL + "get_venue.php";

    public static final String REGISTER_TOKEN = MAIN_URL + "firebase/token.php";
    public static final String CHECK_TOKEN = MAIN_URL + "firebase/check_token.php";
    public static final String SEND_NOTIF = MAIN_URL + "firebase/send.php";
    public static final String GET_TOKENS = MAIN_URL + "firebase/get_tokens.php";
    public static final String GET_NAME = MAIN_URL + "firebase/get_name.php";
    public static final String GET_EVENT = MAIN_URL + "firebase/get_event.php";
}