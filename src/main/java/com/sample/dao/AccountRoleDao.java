package com.sample.dao;

import com.sample.model.RoleType;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.sample.db.Tables.ACCOUNT_ROLES;

@Repository
public class AccountRoleDao {
    private final DSLContext jooq;

    @Autowired
    public AccountRoleDao(DSLContext jooq) {
        this.jooq = jooq;
    }

    public void addRole(Long accountId, RoleType role){
        jooq.insertInto(ACCOUNT_ROLES, ACCOUNT_ROLES.ACCOUNT_ID, ACCOUNT_ROLES.ROLE)
                .values(accountId, role.getValue())
                .onDuplicateKeyIgnore()
                .execute();
    }

    public void removeRole(Long accountId, RoleType role){
        jooq.deleteFrom(ACCOUNT_ROLES)
                .where(ACCOUNT_ROLES.ACCOUNT_ID.eq(accountId)
                        .and(ACCOUNT_ROLES.ROLE.eq(role.getValue())))
                .execute();
    }
}
