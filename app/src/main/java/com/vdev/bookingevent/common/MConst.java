package com.vdev.bookingevent.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MConst {
    public static final int FRAGMENT_DASHBOARD = 0;
    public static final int FRAGMENT_SEARCH_EVENT = 1;
    public static final int FRAGMENT_ACCOUNT = 2;

    public static final int FRAGMENT_DASHBOARD_MONTH = 0;
    public static final int FRAGMENT_DASHBOARD_WEEK = 1;
    public static final int FRAGMENT_DASHBOARD_DAY = 2;

    public static final List<String> titleOptionAccount = new ArrayList<>(Arrays.asList("Detail Account", "Logout"));
    public static final List<String> titleTabDashboard = new ArrayList<>(Arrays.asList("Month", "Week", "Day"));
}
