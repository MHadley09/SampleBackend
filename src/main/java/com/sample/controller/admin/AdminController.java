package com.sample.controller.admin;

import com.sample.controller.exceptions.InvalidGuestRequestException;
import com.sample.controller.exceptions.NotFoundException;
import com.sample.model.Account;
import com.sample.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AccountService accountService;

    @Autowired
    public AdminController(AccountService accountService){
        this.accountService = accountService;
    }

    //TODO we should track all admin calls by the user who made them
    @RequestMapping(value = "/role/add", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Void> addRole(@RequestBody RoleRequest roleRequest){
        Optional<Account> maybeAccountToUpdate = accountService.fetchAccountByUsername(roleRequest.getUsername());

        if(maybeAccountToUpdate.isEmpty()){
            throw new NotFoundException("Username does not exist");
        }

        if(maybeAccountToUpdate.map(Account::getGuestAccount).orElseGet(()->true)){
            throw new InvalidGuestRequestException("Guests cannot have additional roles");
        }

        accountService.addRole(maybeAccountToUpdate.map(Account::getId).get(), roleRequest.getRole());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/role/remove", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Void> removeRole(@RequestBody RoleRequest roleRequest){
         Optional<Account> maybeAccountToUpdate = accountService.fetchAccountByUsername(roleRequest.getUsername());

        if(maybeAccountToUpdate.isEmpty()){
            throw new NotFoundException("Username does not exist");
        }

        if(maybeAccountToUpdate.map(Account::getGuestAccount).orElseGet(()->true)){
            throw new InvalidGuestRequestException("Guests cannot have additional roles");
        }

        accountService.removeRole(maybeAccountToUpdate.map(Account::getId).get(), roleRequest.getRole());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
