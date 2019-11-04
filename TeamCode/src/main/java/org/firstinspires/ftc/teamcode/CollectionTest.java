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

    double flapClosedPos = 0.2;
    double flapOpenPos = 0.29;


    @Override
    public void init() {

        rotator = hardwareMap.servo.get("rotator");
        flapArm = hardwareMap.servo.get("flapArm");
        telemetry.addData("FLAP-ARM INIT POS",flapOpenPos);
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
            telemetry.addData("FLAP-ARM CLOSE",flapArm.getPosition());
        } else {
            flapArm.setPosition(flapOpenPos);
            telemetry.addData("FLAP-ARM OPEN",flapArm.getPosition());
        }

        //Rotator Control - OS Toggle
        if(gamepad1.b && closedRotatorOS){
            closedRotatorOS = false;
            closedRotator = !closedRotator;
            telemetry.addData("CLOSED ROTATOR", closedRotator);
        } else if(!gamepad1.b) closedRotatorOS = true;

        if(closedRotator){
            rotator.setPosition(.5);
            telemetry.addData("ROTATE idk",rotator.getPosition());
        } else {
            rotator.setPosition(0);
            telemetry.addData("ROTATE otherIDK",rotator.getPosition());
        }

    }
}