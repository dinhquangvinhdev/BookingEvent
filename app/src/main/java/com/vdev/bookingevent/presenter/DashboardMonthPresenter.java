package com.vdev.bookingevent.presenter;



public class DashboardMonthPresenter implements DashboardMonthContract.Presenter{
    private DashboardMonthContract.View view;

    public DashboardMonthPresenter(DashboardMonthContract.View view) {
        this.view = view;
    }
}
