package com.dimidych.policydbworker.mvp;

//p->m
public interface IGetCertModelOps extends IModelOps {
    String getCertificate();
    boolean updateCertificate(String certificate);
}
