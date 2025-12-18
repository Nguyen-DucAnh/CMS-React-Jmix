package com.company.restapi.security;

import com.company.restapi.entity.Customer;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;

@ResourceRole(name = "CustomerReadRole", code = CustomerReadRole.CODE, scope = "API")
public interface CustomerReadRole {
    String CODE = "customer-read-role";

    @EntityAttributePolicy(entityClass = Customer.class,
            attributes = "*",
            action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Customer.class,
            actions = {EntityPolicyAction.ALL, EntityPolicyAction.UPDATE})
    void customer();
}