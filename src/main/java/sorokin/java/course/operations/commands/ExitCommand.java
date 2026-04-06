package sorokin.java.course.operations.commands;

import org.springframework.stereotype.Component;
import sorokin.java.course.operations.ConsoleOperationType;
import sorokin.java.course.operations.OperationCommand;

@Component
public class ExitCommand implements OperationCommand {

    @Override
    public void execute() {
        System.out.println("MiniBank stopped.");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.EXIT;
    }
}
