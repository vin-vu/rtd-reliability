package com.vince.rtd_reliability.interfaces;

public interface OtpStatsView {
    long getTotal();
    long getEarly();
    long getOnTime();
    long getLate();
    double getOnTimePct();
}
