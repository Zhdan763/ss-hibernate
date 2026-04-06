package sorokin.java.course.operations;

public interface OperationCommand {
    void execute();
    ConsoleOperationType getOperationType();
}
