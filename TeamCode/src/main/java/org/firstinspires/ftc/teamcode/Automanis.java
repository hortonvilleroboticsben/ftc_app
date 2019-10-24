package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Automanis", group = "Idk")
public class Automanis extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);

    }

    @Override
    public void loop() {
        sm.initializeMachine();
        //sm.rotate(45,.5);
        sm.translate(-30,.5,10);

        telemetry.addData("mtrLeftFront", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrRightFront", r.getEncoderCounts("mtrRightFront"));
        telemetry.addData("mtrLeftBack", r.getEncoderCounts("mtrLeftBack"));
        telemetry.addData("mtrRightBack", r.getEncoderCounts("mtrRightBack"));
    }
}
