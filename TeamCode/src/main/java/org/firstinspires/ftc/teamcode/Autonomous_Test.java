package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Automanis", group = "Testing")
public class Autonomous_Test extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();

    String skyCase = "right";
    double safeSpeed = .5;

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);

    }

    @Override
    public void loop() {
        sm.initializeMachine();

//        switch(skyCase){
//
//            case "left":
//                sm.translate(154,safeSpeed,22);
//                sm.translate(-90,safeSpeed,83);
//
//                break;
//
//            case "center":
//                sm.translate(174.5,safeSpeed,20);
//                sm.translate(-90,safeSpeed,75);
//                break;
//
//            case "right":
//                sm.translate(-159,safeSpeed,21);
//                sm.translate(-90,safeSpeed,67);
//                break;
//
//        }

        sm.translate(0,.5,100);

        telemetry.addData("mtrLeftFront", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrRightFront", r.getEncoderCounts("mtrRightFront"));
        telemetry.addData("mtrLeftBack", r.getEncoderCounts("mtrLeftBack"));
        telemetry.addData("mtrRightBack", r.getEncoderCounts("mtrRightBack"));
    }
}
