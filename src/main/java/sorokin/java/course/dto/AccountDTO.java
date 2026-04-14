package sorokin.java.course.dto;

public class AccountDTO {

    private int id;
    private UserDTO userDTO;
    private int moneyAmount;

    public AccountDTO(int id, int moneyAmount) {
        this.id = id;
        this.moneyAmount = moneyAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(int moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", moneyAmount=" + moneyAmount +
                '}';
    }
}
