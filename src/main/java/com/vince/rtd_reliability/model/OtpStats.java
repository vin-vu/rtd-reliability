package com.vince.rtd_reliability.model;

public record OtpStats(long total, long early, long onTime, long late, double onTimePct) {}
