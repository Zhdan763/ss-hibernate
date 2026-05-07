package sorokin.java.course.user;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;
import sorokin.java.course.TransactionHelper;
import sorokin.java.course.dto.DTOMapper;
import sorokin.java.course.dto.UserDTO;

import java.util.List;

@Component
public class UserService {

    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;
    private final DTOMapper dtoMapper;

    public UserService(SessionFactory sessionFactory, TransactionHelper transactionHelper, DTOMapper dtoMapper) {
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
        this.dtoMapper = dtoMapper;
    }

    public User createUser(String login) {
        validateLogin(login);

        return transactionHelper.executeInTransactionOrJoin(session -> {

            User user = new User(login);
            try {
                session.persist(user);
                return user;
            } catch (ConstraintViolationException e) {
                throw new IllegalArgumentException(
                        "User already exists with login=%s".formatted(login)
                );
            }
        });

    }

    public List<UserDTO> findAll() {
        try (Session session = sessionFactory.openSession()) {

            var userList = session.createQuery("SELECT u FROM User u", User.class).list();
            return dtoMapper.userListToUserDTOList(userList);
        }

    }

    private void validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login must not be blank");
        }
    }
}
