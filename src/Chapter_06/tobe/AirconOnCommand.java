package Chapter_06.tobe;

import Chapter_06.asis.AirConditioner;

public class AirconOnCommand implements Command {

    private final AirConditioner aircon;

    public AirconOnCommand(AirConditioner aircon) {
        this.aircon = aircon;
    }

    @Override
    public void execute() {
        aircon.turnOn();
    }

    @Override
    public void undo() {
        aircon.turnOff();
    }
}
