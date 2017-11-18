package com.dimidych.policydbworker;

import android.support.annotation.Nullable;

public class PolicySetDataContract {
    public long PolicySetId;
    public int PolicyId;
    @Nullable
    public long LoginId;
    @Nullable
    public int GroupId;
    @Nullable
    public boolean Selected;
    public String PolicyName;
    public String PolicyInstruction;
    public boolean PolicyEnabled;
    @Nullable
    public short PlatformId;
    @Nullable
    public String PolicyParam;
}
