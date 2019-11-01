package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "Collection Test", group = "TeleOp")
public class CollectionTest extends OpMode {

    Servo rotator, flapArm;
    boolean closedFlap = false;
    boolean closedFlapOS = true;
    boolean closedRotator = false;
    boolean closedRotatorOS = false;

    final double flapClosedPos = -0.1;
    final double flapOpenPos = 0.1;


    @Override
    public void init() {

        rotator = hardwareMap.servo.get("rotator");
        flapArm = hardwareMap.servo.get("flapArm");
//        flapArm.setDirection(Servo.Direction.REVERSE);

    }

    @Override
    public void loop() {

        //Flap Control - OS Toggle
        if(gamepad1.a && closedFlapOS){
            closedFlapOS = false;
            closedFlap = !closedFlap;
            telemetry.addData("CLOSED FLAP", closedFlap);
        } else if(!gamepad1.a) closedFlapOS = true;

        if(closedFlap){
            flapArm.setPosition(flapClosedPos);
        } else {
            flapArm.setPosition(flapOpenPos);
        }

        //Rotator Control - OS Toggle
        if(gamepad1.b && closedRotatorOS){
            closedRotatorOS = false;
            closedRotator = !closedRotator;
            telemetry.addData("CLOSED ROTATOR", closedRotator);
        } else if(!gamepad1.b) closedRotatorOS = true;

        if(closedRotator){
            rotator.setPosition(.8);
        } else {
//            rotator.setPosition(0);
        }

    }
}