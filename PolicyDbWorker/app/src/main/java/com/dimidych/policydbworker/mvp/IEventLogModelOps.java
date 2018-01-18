package com.dimidych.policydbworker.mvp;

import com.dimidych.policydbworker.EventLogDataContract;

//p->m
public interface IEventLogModelOps  extends IModelOps {
    EventLogDataContract[] getEventLog(String fromDate, String toDate, long documentId, String eventName);
    boolean setEventLog(EventLogDataContract log);
    boolean dropEventLogTbl();
}
