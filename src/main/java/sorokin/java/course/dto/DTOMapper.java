package sorokin.java.course.dto;

import org.springframework.stereotype.Component;
import sorokin.java.course.account.Account;
import sorokin.java.course.user.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class DTOMapper {

    public List<UserDTO> userListToUserDTOList(List<User> userList) {
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User u : userList
        ) {
            UserDTO userDTO = new UserDTO(u.getId(), u.getLogin(), accountListToAccountDTOList(u.getAccountList()));
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    public List<AccountDTO> accountListToAccountDTOList(List<Account> accountList) {
        List<AccountDTO> accountDTOList = new ArrayList<>();
        for (Account a : accountList
        ) {
            AccountDTO accountDTO = new AccountDTO(a.getId(), a.getMoneyAmount());
            accountDTOList.add(accountDTO);
        }
        return accountDTOList;
    }
}
