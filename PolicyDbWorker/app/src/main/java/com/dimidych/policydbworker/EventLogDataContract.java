package com.dimidych.policydbworker;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;

public class EventLogDataContract implements Parcelable{
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

    protected EventLogDataContract(Parcel in) {
        EventLogId = in.readLong();
        EventId = in.readInt();
        DocumentId = in.readLong();
        Message = in.readString();
        Device = in.readString();
        Login = in.readString();
        EventName = in.readString();
    }

    public static final Creator<EventLogDataContract> CREATOR = new Creator<EventLogDataContract>() {
        @Override
        public EventLogDataContract createFromParcel(Parcel in) {
            return new EventLogDataContract(in);
        }

        @Override
        public EventLogDataContract[] newArray(int size) {
            return new EventLogDataContract[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(EventLogId);
        dest.writeInt(EventId);
        dest.writeLong(DocumentId);
        dest.writeString(Message);
        dest.writeString(Device);
        dest.writeString(Login);
        dest.writeString(EventName);
    }
}
