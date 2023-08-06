package com.sample.dao.mapper;

import com.sample.model.Account;
import com.sample.model.AccountProfile;
import org.jooq.sample.db.tables.records.AccountProfilesRecord;
import org.jooq.sample.db.tables.records.AccountsRecord;

import java.util.Optional;

public class AccountMapper {

    public static Account map(AccountsRecord accountsRecord, AccountProfilesRecord accountProfilesRecord){
        AccountProfile profile = AccountProfileMapper.map(accountProfilesRecord);

        return
                Account.builder()
                        .id(accountsRecord.getId())
                        .displayName(accountsRecord.getDisplayName())
                        .username(accountsRecord.getUsername())
                        .guestAccount(accountsRecord.getGuestAccount())
                        .profile(Optional.of(profile))
                        .build();
    }
    public static Account map(AccountsRecord record) {
        return
                Account.builder()
                        .id(record.getId())
                        .displayName(record.getDisplayName())
                        .username(record.getUsername())
                        .guestAccount(record.getGuestAccount())
                        .profile(Optional.empty())
                        .build();
    }
}
