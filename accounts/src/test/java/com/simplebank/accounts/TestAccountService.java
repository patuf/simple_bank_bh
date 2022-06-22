package com.simplebank.accounts;

import com.simplebank.accounts.acc.Account;
import com.simplebank.accounts.acc.AccountRepository;
import com.simplebank.accounts.acc.AccountService;
import com.simplebank.accounts.acc.AccountStatus;
import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommand;
import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommandRepository;
import com.simplebank.accounts.customer.Customer;
import com.simplebank.accounts.customer.CustomerDataProvider;
import com.simplebank.accounts.exception.AccountNotFoundException;
import com.simplebank.accounts.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestAccountService {

    @Mock
    private AccountRepository accRepo;
    @Mock
    private CreateTransactionCommandRepository troutRepo;
    @Mock
    private CustomerDataProvider customerDataProvider;
    @InjectMocks
    private AccountService accService;

    private Account account;
    private Customer customer;
    private CreateTransactionCommand ctCommand0, ctCommand200;

    @BeforeEach
    public void setup() {
        account = new Account(2L, LocalDateTime.now(), AccountStatus.ACTIVE);
        account.setAccountId(1L);
        customer = new Customer(2L, "User1", "Surname1");
        ctCommand0 = new CreateTransactionCommand(2L, 1L, 0., account.getTimeCreated());
        ctCommand200 = new CreateTransactionCommand(2L, 1L, 200., account.getTimeCreated());
    }

    @DisplayName("Finding an account by its ID - success")
    @Test
    public void testFindOne_Success() {
        given(accRepo.findById(1L)).willReturn(Optional.of(account));

        assertThat(accService.findOne(1L)).isNotNull();
    }

    @DisplayName("Finding an account by its ID - no such account")
    @Test
    public void testFindOne_Fail() {
        given(accRepo.findById(1L)).willReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accService.findOne(1L);
        });
    }

    @DisplayName("Creating an account with zero balance")
    @Test
    public void testCreateAccount() {

        given(customerDataProvider.findById(2L)).willReturn(Optional.of(customer));
        given(accRepo.save(any(Account.class))).willReturn(account);

        verify(troutRepo, never()).save(any(CreateTransactionCommand.class));

        assertThat(accService.createAccount(account.getCustomerId(), 0., account.getTimeCreated())).isEqualTo(account);
    }

    @DisplayName("Creating an account with non-zero balance")
    @Test
    public void testCreateAccountAndTransaction() {
        given(customerDataProvider.findById(2L)).willReturn(Optional.of(customer));
        given(accRepo.save(any(Account.class))).willReturn(account);
        given(troutRepo.save(ctCommand200)).willReturn(ctCommand200);

        assertThat(accService.createAccount(ctCommand200.getCustomerId(), ctCommand200.getAmount(), ctCommand200.getTimeCreated())).isEqualTo(account);

        verify(troutRepo, times(1)).save(ctCommand200);
    }

    @DisplayName("Creating an account with a non existing user")
    @Test
    public void testCreateAccountNoUser() {
        given(customerDataProvider.findById(customer.getCustomerId())).willReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> accService.createAccount(ctCommand200.getCustomerId(), ctCommand200.getAmount(), ctCommand200.getTimeCreated())
        );

        verify(accRepo, never()).save(any(Account.class));
        verify(troutRepo, never()).save(any(CreateTransactionCommand.class));
    }
}
