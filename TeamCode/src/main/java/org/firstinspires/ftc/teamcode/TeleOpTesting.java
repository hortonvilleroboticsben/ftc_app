package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp (name = "TeleopTesting", group = "lmao")
public class TeleOpTesting extends OpMode {

    DcMotor left;
    DcMotor right;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("mtrLeftDrive");
        right = hardwareMap.dcMotor.get("mtrRightDrive");
    }

    @Override
    public void loop() {
        left.setPower(gamepad1.left_stick_y);
        right.setPower(-gamepad1.right_stick_y);
    }
}
