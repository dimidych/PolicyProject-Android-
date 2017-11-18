package com.dimidych.policydbworker.mvp;

import com.dimidych.policydbworker.PolicySetDataContract;

//p->m
public interface ICheckPolicyModelOps extends IModelOps {
    boolean addPolicySetToDb(PolicySetDataContract policySet);
    boolean updatePolicySet(PolicySetDataContract servicePolicySet, PolicySetDataContract dbPolicySet);
    PolicySetDataContract getSinglePolicySetFromDb(long policyId);
    PolicySetDataContract[] getPolicySetFromDb();
}
