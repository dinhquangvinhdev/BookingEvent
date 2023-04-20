package com.vdev.bookingevent.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MConst {
    public static final int FRAGMENT_DASHBOARD = 0;
    public static final int FRAGMENT_ADD_EVENT = 1;
    public static final int FRAGMENT_SEARCH_EVENT = 2;
    public static final int FRAGMENT_ACCOUNT = 3;
    public static final int FRAGMENT_DASHBOARD_MONTH = 0;
    public static final int FRAGMENT_DASHBOARD_WEEK = 1;
    public static final int FRAGMENT_DASHBOARD_DAY = 2;
    public static final String ROLE_PARTICIPANT = "participant";
    public static final String ROLE_HOST = "host";
    public static final List<String> titleOptionAccount = new ArrayList<>(Arrays.asList("Detail Account", "Logout"));
    public static final List<String> titleTabDashboard = new ArrayList<>(Arrays.asList("Month", "Week", "Day"));
    public static final String FORMAT_TIME = "%02d:%02d";
}
