package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Automanis", group = "Idk")
public class Automanis extends OpMode {

    StateMachine sm = new StateMachine();

    @Override
    public void init() {
        sm.initializeMachine();
    }

    @Override
    public void loop() {

        if(sm.next_state_to_execute()) {

            sm.translate(0, .5, 10);
            sm.rotate(45,.5);

            sm.incrementState();
        }
    }
}
