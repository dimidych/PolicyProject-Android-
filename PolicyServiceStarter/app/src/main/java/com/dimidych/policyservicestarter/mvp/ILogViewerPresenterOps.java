package com.dimidych.policyservicestarter.mvp;

import com.dimidych.policydbworker.EventLogDataContract;

public interface ILogViewerPresenterOps extends IPresenterOps {
    EventLogDataContract[] getEventLog(String fromDate, String toDate, String eventName);
}
