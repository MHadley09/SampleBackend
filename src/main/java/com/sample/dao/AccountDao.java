package com.sample.dao;

import com.sample.dao.mapper.AccountMapper;
import com.sample.model.Account;
import com.sample.model.HashAndSalt;
import com.sample.model.Role;
import com.sample.model.RoleType;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.jooq.sample.db.Tables.*;

@Repository
public class AccountDao {
    private final DSLContext jooq;

    @Autowired
    public AccountDao(DSLContext jooq) {
        this.jooq = jooq;
    }

    public List<String> fetchUsernames() {
        return jooq.fetch(ACCOUNTS).map(record -> record.getUsername());
    }

    public Optional<Account> fetchUser(String username) {
        return jooq.selectFrom(ACCOUNTS)
                .where(ACCOUNTS.USERNAME.equalIgnoreCase(username))
                .stream().findAny().map(AccountMapper::map);
    }

    public Optional<Account> fetchUserById(Long id) {
        return jooq.selectFrom(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(id))
                .stream().findAny().map(AccountMapper::map);
    }

    public Optional<Account> fetchUserByUsername(String username) {
        return jooq.selectFrom(ACCOUNTS)
                .where(ACCOUNTS.USERNAME.eq(username))
                .stream().findAny().map(AccountMapper::map);
    }

    public List<Role> fetchUserRoles(Long userId) {
        return jooq.select(ACCOUNT_ROLES.ROLE)
                .from(ACCOUNT_ROLES)
                .where(ACCOUNT_ROLES.ACCOUNT_ID.eq(userId))
                .stream()
                .map(record -> Role.builder().role(record.value1()).build())
                .collect(toImmutableList());
    }

    public Long insertNewAccount(String username, String displayName, byte[] hashedPassword, byte[] salt) {
        Long accountId = insertAccount(username, displayName, false);

        jooq.insertInto(ACCOUNT_PASSWORDS, ACCOUNT_PASSWORDS.ACCOUNT_ID,
                        ACCOUNT_PASSWORDS.PASSWORD_HASH, ACCOUNT_PASSWORDS.PASSWORD_SALT)
                .values(accountId, hashedPassword, salt).execute();

        jooq.insertInto(ACCOUNT_ROLES, ACCOUNT_ROLES.ACCOUNT_ID, ACCOUNT_ROLES.ROLE)
                .values(accountId, RoleType.USER.getValue()).execute();

        return accountId;
    }
    public Long insertNewGuestAccount(String username, String displayName) {
        Long accountId = insertAccount(username, displayName, true);

        jooq.insertInto(ACCOUNT_ROLES, ACCOUNT_ROLES.ACCOUNT_ID, ACCOUNT_ROLES.ROLE)
                .values(accountId, RoleType.USER.getValue()).execute();

        return accountId;
    }

    private Long insertAccount(String username, String displayName, boolean guestAccount) {
        return jooq.insertInto(ACCOUNTS, ACCOUNTS.USERNAME, ACCOUNTS.DISPLAY_NAME, ACCOUNTS.GUEST_ACCOUNT)
                .values(username, displayName, guestAccount)
                .returningResult(ACCOUNTS.ID)
                .fetchOne().value1();
    }

    public Optional<HashAndSalt> fetchHashAndSalt(String username) {
        return jooq.select(ACCOUNT_PASSWORDS.PASSWORD_HASH, ACCOUNT_PASSWORDS.PASSWORD_SALT)
                .from(ACCOUNT_PASSWORDS)
                .join(ACCOUNTS).on(ACCOUNTS.ID.eq(ACCOUNT_PASSWORDS.ACCOUNT_ID))
                .where(ACCOUNTS.USERNAME.equalIgnoreCase(username))
                .fetchOptional()
                .map(record2 -> new HashAndSalt(record2.value1(), record2.value2()));
    }
}
