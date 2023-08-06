package com.sample.dao.mapper;

import com.sample.model.AccountProfile;
import org.jooq.sample.db.tables.records.AccountProfilesRecord;

import java.util.Optional;

public class AccountProfileMapper {

    public static AccountProfile map(AccountProfilesRecord result){
        return
            AccountProfile.builder()
                .accountId(result.getAccountId())
                .firstName(Optional.ofNullable(result.getFirstName()))
                .lastName(Optional.ofNullable(result.getLastName()))
                .phoneNumber(Optional.ofNullable(result.getPhone()))
                .email(Optional.ofNullable(result.getEmail()))
                .build();
    }

    public static AccountProfilesRecord map(Long id, AccountProfile profile){
        AccountProfilesRecord record = new AccountProfilesRecord();

        record.setAccountId(id);
        record.setFirstName(profile.getFirstName().orElseGet(() -> ""));
        record.setLastName(profile.getLastName().orElseGet(() -> ""));
        record.setEmail(profile.getEmail().orElseGet(() -> ""));
        record.setPhone(profile.getPhoneNumber().orElseGet(() -> ""));

        return record;
    }}
