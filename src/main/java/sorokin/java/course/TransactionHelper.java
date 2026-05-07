package sorokin.java.course;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TransactionHelper {

    private final SessionFactory sessionFactory;

    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeInTransactionOrJoin(Function<Session, T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.getTransaction();
        boolean owner = tx.getStatus() == TransactionStatus.NOT_ACTIVE;
        if (owner) {
            tx = session.beginTransaction();
        }
        try {
            T result = action.apply(session);
            if (owner) {
                tx.commit();
            }
            return result;
        } catch (RuntimeException e) {
            if (owner) {
                tx.rollback();

            }
            throw e;
        } finally {
            if (owner) {
                session.close();
            }
        }
    }

    public void executeInTransactionOrJoin(Consumer<Session> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.getTransaction();
        boolean owner = tx.getStatus() == TransactionStatus.NOT_ACTIVE;
        if (owner) {
            tx = session.beginTransaction();
        }
        try {
            action.accept(session);
            if (owner) {
                tx.commit();
            }

        } catch (RuntimeException e) {
            if (owner) {
                tx.rollback();

            }
            throw e;
        } finally {
            if (owner) {
                session.close();
            }
        }
    }

}
