package com.company.restapi.security;

import com.company.restapi.entity.Customer;
import io.jmix.rest.security.role.RestMinimalRole;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;

@ResourceRole(name = "AnonymousRestRole", code = AnonymousRestRole.CODE, scope = "API")
public interface AnonymousRestRole extends RestMinimalRole {

    String CODE = "anonymous-rest-role";

    @EntityAttributePolicy(entityClass = Customer.class,
            attributes = "*",
            action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Customer.class,
            actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void customer();
}