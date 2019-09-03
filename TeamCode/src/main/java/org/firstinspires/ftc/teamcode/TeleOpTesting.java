package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "GrabbyArmyTele-Op", group = "TeleOp")
public class TeleOpTesting extends OpMode {

    DcMotor left, right, armLift;
    Servo armLeft, armRight;
    boolean closed = false;
    boolean closedOS = true;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("mtrLeftDrive");
        right = hardwareMap.dcMotor.get("mtrRightDrive");
        armLift = hardwareMap.dcMotor.get("mtrLift");

        right.setDirection(DcMotorSimple.Direction.REVERSE);

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        armLeft = hardwareMap.servo.get("srvLeft");
        armRight = hardwareMap.servo.get("srvRight");
        armRight.setDirection(Servo.Direction.REVERSE);

    }

    @Override
    public void loop() {

        left.setPower(Math.abs(gamepad1.left_stick_y) < 0.05 ? 0 : gamepad1.right_trigger > .5 ? gamepad1.left_stick_y/2 : gamepad1.left_stick_y);
        right.setPower(Math.abs(gamepad1.right_stick_y) < 0.05 ? 0 : gamepad1.right_trigger > .5 ? gamepad1.right_stick_y/2 : gamepad1.right_stick_y);


        //arm controls
        telemetry.addData("ARMY", armLift.getCurrentPosition());
        //ben fucking sucks... screw you ben
        //              1)..............................................   1t & 2)..   2t).   2f & 3)...   3t)   3f)..   1f)..
        armLift.setPower(armLift.getCurrentPosition() < 0 || gamepad1.y ? gamepad1.y ? -0.5 : gamepad1.a ? 0.5 : -0.10 : -0.10);

        if(gamepad1.right_bumper && closedOS){
            closedOS = false;
            closed = !closed;
        }else if(!gamepad1.right_bumper) closedOS = true;

        if(closed){
            armLeft.setPosition(0.9);
            armRight.setPosition(0.9);
        } else {
            armLeft.setPosition(0);
            armRight.setPosition(0);
        }

//        armLeft.setPosition(gamepad1.left_trigger);
//        armRight.setPosition(gamepad1.left_trigger);
//        left.setPower(gamepad1.left_stick_y);
//        right.setPower(-gamepad1.right_stick_y);
    }
}
