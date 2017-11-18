package com.dimidych.policydbworker.mvp;

import com.dimidych.policydbworker.PolicySetDataContract;

//m->p
public interface ICheckPolicyPresenterRequiredOps extends IPresenterRequiredOps{
    void onAddPolicySetToDb(PolicySetDataContract policySet);
    void onUpdatePolicySet(PolicySetDataContract servicePolicySet, PolicySetDataContract dbPolicySet);
}
