package com.sample.dao;

import com.sample.dao.mapper.AccountProfileMapper;
import com.sample.model.AccountProfile;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.sample.db.Tables.ACCOUNT_PROFILES;

@Repository
public class AccountProfileDao {
    private final DSLContext jooq;

    @Autowired
    public AccountProfileDao(DSLContext jooq) {
        this.jooq = jooq;
    }

    public Optional<AccountProfile> fetchProfileByAccountId(Long accountId){
        return jooq.selectFrom(ACCOUNT_PROFILES)
                .where(ACCOUNT_PROFILES.ACCOUNT_ID.eq(accountId))
                .stream().findAny().map(AccountProfileMapper::map);
    }

    public void insertOrUpdateAccountProfile(Long accountId, AccountProfile profile){
        if(fetchProfileByAccountId(accountId).isPresent()){
            jooq.update(ACCOUNT_PROFILES).set(AccountProfileMapper.map(accountId, profile)).where(ACCOUNT_PROFILES.ACCOUNT_ID.eq(accountId)).execute();
        }
        else{
            jooq.insertInto(ACCOUNT_PROFILES).set(AccountProfileMapper.map(accountId, profile)).execute();
        }
    }

}
