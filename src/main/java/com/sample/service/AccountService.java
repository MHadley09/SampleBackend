package com.sample.service;

import com.sample.controller.exceptions.InvalidLoginException;
import com.sample.dao.AccountDao;
import com.sample.dao.AccountProfileDao;
import com.sample.dao.AccountRoleDao;
import com.sample.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountService {
    Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountDao accountDao;
    private final AccountProfileDao accountProfileDao;
    private final AccountRoleDao accountRoleDao;
    private final Random random = new SecureRandom();
    private final String DIGEST_ALGORITHM = "SHA-512";
    private final Charset CHARACTER_SET = StandardCharsets.UTF_8;

    @Autowired
    public AccountService(AccountDao accountDao, AccountProfileDao accountProfileDao, AccountRoleDao accountRoleDao) {
        this.accountDao = accountDao;
        this.accountProfileDao = accountProfileDao;
        this.accountRoleDao = accountRoleDao;
    }

    public List<String> fetchUsernames() {
        return accountDao.fetchUsernames();
    }

    public boolean doesUserExist(String username) {
        return accountDao.fetchUser(username).isPresent();
    }

    public Optional<Account> loadAccountById(Long id) {
        Optional<Account> account = accountDao.fetchUserById(id);
        return account;
    }

    public Optional<Account> fetchAccountByUsername(String username) {
        return accountDao.fetchUserByUsername(username);
    }

    public List<Role> loadAccountRoles(Long userId) {
        return accountDao.fetchUserRoles(userId);
    }

    private byte[] generateSalt() {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return saltBytes;
    }

    private byte[] getHashedPassword(String password, byte[] salt) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }

        digest.update(salt);

        return digest.digest(password.getBytes(CHARACTER_SET));
    }

    @Transactional
    public void saveNewUser(String username, String password, String displayName, AccountProfile profile) {
        byte[] salt = generateSalt();

        byte[] hashedPassword = getHashedPassword(password, salt);

        Long id = accountDao.insertNewAccount(username, displayName, hashedPassword, salt);

        accountProfileDao.insertOrUpdateAccountProfile(id, profile);
    }

    @Transactional
    public void saveNewGuest(String username,  String displayName, AccountProfile accountProfile) {
         Long id = accountDao.insertNewGuestAccount(username, displayName);
         accountProfileDao.insertOrUpdateAccountProfile(id, accountProfile);
    }

    public Optional<Account> authenticateUser(String username, String password) {
        HashAndSalt hashAndSalt =
                accountDao.fetchHashAndSalt(username).orElseThrow(InvalidLoginException::new);

        byte[] hashedPasswordInput = getHashedPassword(password, hashAndSalt.getSalt());

        if (!Arrays.equals(hashedPasswordInput, hashAndSalt.getHash())) {
            throw new InvalidLoginException();
        }

        return accountDao.fetchUser(username);
    }

    public Optional<Account> authenticateGuest(String username){
        Optional<Account> maybeUser = accountDao.fetchUser(username);

        //If an account is not explicitly a guest account we need to reject as invalid login
        if(!maybeUser.map(Account::getGuestAccount).orElseGet(() -> false)){
            throw new InvalidLoginException();
        }

        return maybeUser;
    }

    public void addRole(Long accountId, RoleType role){
        accountRoleDao.addRole(accountId, role);
    }
    public void removeRole(Long accountId, RoleType role){
        accountRoleDao.removeRole(accountId, role);
    }
}
