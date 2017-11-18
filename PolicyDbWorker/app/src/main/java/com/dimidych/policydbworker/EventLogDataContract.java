package com.dimidych.policydbworker;

import android.support.annotation.Nullable;

import java.util.Date;

public class EventLogDataContract {
    public long EventLogId;
    public Date EventLogDate;
    public int EventId;
    @Nullable
    public long DocumentId;
    public String Message;
    public String Device;
    public String Login;
    public String EventName;

    public EventLogDataContract() {
    }

    public EventLogDataContract(long documentId, String message, String login, String eventName) {
        EventLogDate = new Date();
        DocumentId = documentId;
        Message = message;
        Login = login;
        EventName = eventName;
    }

    public EventLogDataContract(long eventLogId, Date eventLogDate, int eventId, long documentId,
                                String message, String device, String login, String eventName) {
        EventLogId = eventLogId;
        EventLogDate = eventLogDate;
        EventId = eventId;
        DocumentId = documentId;
        Message = message;
        Device = device;
        Login = login;
        EventName = eventName;
    }
}
