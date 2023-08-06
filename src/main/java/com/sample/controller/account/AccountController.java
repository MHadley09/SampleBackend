package com.sample.controller.account;

import com.sample.component.AuthenticatedAccountFacade;
import com.sample.component.TokenManager;
import com.sample.controller.exceptions.InvalidLoginException;
import com.sample.controller.exceptions.NotFoundException;
import com.sample.controller.exceptions.UsernameAlreadyInUseException;
import com.sample.model.Account;
import com.sample.model.AccountProfile;
import com.sample.model.Role;
import com.sample.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;

@RestController
public class AccountController {
    private final AccountService accountService;
    private final TokenManager tokenManager;
    private final AuthenticatedAccountFacade authenticatedAccountFacade;

    @Autowired
    public AccountController(AccountService accountService, TokenManager tokenManager, AuthenticatedAccountFacade authenticatedAccountFacade) {
        this.accountService = accountService;
        this.tokenManager = tokenManager;
        this.authenticatedAccountFacade = authenticatedAccountFacade;
    }

    @RequestMapping(value = "/user/new", method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<AuthResponse> createNewUserAccount(@RequestBody NewUserAccountRequest userAccountRequest) {
        if (accountService.doesUserExist(userAccountRequest.getUsername())) {
            throw new UsernameAlreadyInUseException(userAccountRequest.getUsername() + "is in use");
        }

        accountService.saveNewUser(userAccountRequest.getUsername(), userAccountRequest.getPassword(), userAccountRequest.getDisplayName(),
                AccountProfile.builder()
                        .firstName(Optional.ofNullable(userAccountRequest.getFirstName()))
                        .lastName(Optional.ofNullable(userAccountRequest.getLastName()))
                        .email(Optional.ofNullable(userAccountRequest.getEmailAddress()))
                        .phoneNumber(Optional.ofNullable(userAccountRequest.getPhoneNumber()))
                        .build());

        Account account = accountService.authenticateUser(userAccountRequest.getUsername(), userAccountRequest.getPassword())
                .orElseThrow(InvalidLoginException::new);

        return buildAuthResponseForAccount(account);
    }

    //Guest accounts should be deleted if they haven't been used in over 30 days
    //Need to add security to restrict guest accounts to only being player/user accounts
    @RequestMapping(value = "/guest/new", method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<AuthResponse> createNewUserAccount(@RequestBody GuestAccountRequest guestAccountRequest) {
        if (accountService.doesUserExist(guestAccountRequest.getUsername())) {
            throw new UsernameAlreadyInUseException(guestAccountRequest.getUsername() + "is in use");
        }

        accountService.saveNewGuest(guestAccountRequest.getUsername(), guestAccountRequest.getDisplayName(),
                AccountProfile.builder()
                        .firstName(Optional.ofNullable(guestAccountRequest.getFirstName()))
                        .lastName(Optional.ofNullable(guestAccountRequest.getLastName()))
                        .email(Optional.ofNullable(guestAccountRequest.getEmailAddress()))
                        .phoneNumber(Optional.ofNullable(guestAccountRequest.getPhoneNumber()))
                        .build());

        Account account = accountService.authenticateGuest(guestAccountRequest.getUsername())
                .orElseThrow(InvalidLoginException::new);

        return buildAuthResponseForAccount(account);
    }

    @RequestMapping(value = "/guestAuth", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<AuthResponse> loginGuest(@RequestBody AuthRequest authRequest) {
        Account account = accountService.authenticateGuest(authRequest.getUsername())
                .orElseThrow(InvalidLoginException::new);

        return buildAuthResponseForAccount(account);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        Account account = accountService.authenticateUser(authRequest.getUsername(), authRequest.getPassword())
                .orElseThrow(InvalidLoginException::new);

        return buildAuthResponseForAccount(account);
    }

    @RequestMapping(value = "/refreshToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<AuthResponse> refreshToken(){
        Account account = authenticatedAccountFacade.getCurrentAccount();

        if(account == null){
            throw new InvalidLoginException();
        }

        return buildAuthResponseForAccount(account);
    }

    private ResponseEntity<AuthResponse> buildAuthResponseForAccount(Account account) {
        //TODO return display name in token
        List<Role> roles = accountService.loadAccountRoles(account.getId());

        Date expiry = tokenManager.generateExpiry();

        final String jwtToken = tokenManager.generateJwtToken(account, roles, expiry);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(jwtToken)
                        .username(account.getUsername())
                        .displayName(account.getDisplayName())
                        .roles(roles.stream().map(Role::getRole).collect(toImmutableList()))
                        .expiry(expiry)
                        .build());
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<AccountResponse> getAccountByUsername(@RequestParam("username") String username) {
        Optional<Account> account = accountService.fetchAccountByUsername(username);

        if(account.isEmpty()){
            throw new NotFoundException("Username does not exist");
        }

        Optional<AccountProfile> profile = account.get().getProfile();

        String firstName = null;
        String lastName = null;

        if(profile.isPresent()) {
            firstName = profile.get().getFirstName().orElseGet(()-> null);
            lastName = profile.get().getLastName().orElseGet(()-> null);
        }

        return ResponseEntity.ok(
          AccountResponse.builder()
                  .accountId(account.get().getId())
                  .username(account.get().getUsername())
                  .firstName(firstName)
                  .lastName(lastName)
                  .build()
        );
    }

}
