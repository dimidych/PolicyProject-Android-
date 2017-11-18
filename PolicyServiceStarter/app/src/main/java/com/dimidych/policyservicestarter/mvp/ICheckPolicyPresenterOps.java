package com.dimidych.policyservicestarter.mvp;

import com.dimidych.policydbworker.PolicySetDataContract;

public interface ICheckPolicyPresenterOps extends IPresenterOps {
    PolicySetDataContract[] getPolicySetFromService();
}
