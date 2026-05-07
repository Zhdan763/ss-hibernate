package sorokin.java.course.account;

import org.springframework.stereotype.Component;
import sorokin.java.course.TransactionHelper;
import sorokin.java.course.user.User;

@Component
public class AccountService {

    private final TransactionHelper transactionHelper;
    private final AccountProperties accountProperties;

    public AccountService(TransactionHelper transactionHelper, AccountProperties accountProperties) {
        this.transactionHelper = transactionHelper;
        this.accountProperties = accountProperties;
    }

    public Account createAccount(Integer userId) {
        validatePositiveId(userId, "user id");
        return transactionHelper.executeInTransactionOrJoin(session -> {
            var user = session.get(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("No such user: id=%s".formatted(userId));
            }
            Account account = new Account(user, accountProperties.getDefaultAmount());
            session.persist(account);
            return account;
        });
    }

    public void withdraw(Integer fromAccountId, Integer amount) {
        validatePositiveId(fromAccountId, "account id");
        validatePositiveAmount(amount);

        transactionHelper.executeInTransactionOrJoin(session -> {
            var account = session.get(Account.class, fromAccountId);
            if (account == null) {
                throw new IllegalArgumentException("No such account: id=%s".formatted(fromAccountId));
            }
            var accountAmount = account.getMoneyAmount();
            if (amount > accountAmount) {
                throw new IllegalArgumentException(
                        "insufficient funds on account id=%s, moneyAmount=%s, attempted withdraw=%s"
                                .formatted(account.getId(), account.getMoneyAmount(), amount));
            }
            account.setMoneyAmount(accountAmount - amount);
            session.persist(account);
        });

    }

    public void deposit(Integer toAccountId, Integer amount) {
        validatePositiveId(toAccountId, "account id");
        validatePositiveAmount(amount);
        transactionHelper.executeInTransactionOrJoin(session -> {
            var account = session.get(Account.class, toAccountId);
            if (account == null) {
                throw new IllegalArgumentException("No such account: id=%s".formatted(toAccountId));
            }
            account.setMoneyAmount(amount);
            session.persist(account);
        });
    }

    public void closeAccount(Integer accountId) {
        validatePositiveId(accountId, "account id");
        transactionHelper.executeInTransactionOrJoin(session -> {
            var account = session.get(Account.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("No such account: id=%s".formatted(accountId));
            }
            var user = account.getUser();
            if (user.getAccountList().size() == 1) {
                throw new IllegalStateException("Can't close the only one account");
            }
            var accountToTransferMoney = user.getAccountList().stream().
                    filter(it -> it.getId() != accountId)
                    .findFirst()
                    .orElseThrow();
            var newAmount = accountToTransferMoney.getMoneyAmount() + account.getMoneyAmount();
            accountToTransferMoney.setMoneyAmount(newAmount);
            session.remove(account);

        });

    }

    public void transfer(int fromAccountId, int toAccountId, int amount) {
        validatePositiveId(fromAccountId, "source account id");
        validatePositiveId(toAccountId, "target account id");
        validatePositiveAmount(amount);
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("source and target account id must be different");
        }
        transactionHelper.executeInTransactionOrJoin(session -> {
            var accountFrom = session.get(Account.class, fromAccountId);
            if (accountFrom == null) {
                throw new IllegalArgumentException("No such account: id=%s".formatted(fromAccountId));
            }
            var accountTo = session.get(Account.class, toAccountId);
            if (accountTo == null) {
                throw new IllegalArgumentException("No such account: id=%s".formatted(toAccountId));
            }
            var amountFrom = accountFrom.getMoneyAmount();
            var amountTo = accountTo.getMoneyAmount();
            if (amount > amountFrom) {
                throw new IllegalArgumentException(
                        "insufficient funds on account id=%s, moneyAmount=%s, attempted transfer=%s"
                                .formatted(accountFrom.getId(), amountFrom, amount)
                );
            }
            accountFrom.setMoneyAmount(amountFrom - amount);
            var amountToTransfer = accountTo.getUser().getId() == accountFrom.getUser().getId()
                    ? amount
                    : (int) Math.round(amount * (1 - accountProperties.getTransferCommission()));

            accountTo.setMoneyAmount(amountTo + amountToTransfer);
        });
    }

    private void validatePositiveId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be > 0");
        }
    }

    private void validatePositiveAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }
}
