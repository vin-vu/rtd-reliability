package com.vince.rtd_reliability.interfaces;

public interface OtpStatsView {
    long getTotal();
    long getEarly();
    long onTime();
    long getLate();
    double getOnTimePct();
}
