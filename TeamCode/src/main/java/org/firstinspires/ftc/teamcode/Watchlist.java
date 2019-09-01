package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp (name = "WatchList", group = "like")
public class Watchlist extends OpMode {

    DcMotor tl;
    DcMotor tr;
    DcMotor bl;
    DcMotor br;

    @Override
    public void init() {
        tl = hardwareMap.dcMotor.get("1");
        tr= hardwareMap.dcMotor.get("2");
        bl = hardwareMap.dcMotor.get("3");
        br= hardwareMap.dcMotor.get("4");
    }

    @Override
    public void loop() {
        telemetry.addData("X-Value", gamepad1.left_stick_x);
        telemetry.addData("Y-Value", -gamepad1.left_stick_y);

        if(gamepad1.left_stick_x == 0){
            tl.setPower(10 * gamepad1.left_stick_y);
            tr.setPower(10 * gamepad1.left_stick_y);
            bl.setPower(10 * gamepad1.left_stick_y);
            br.setPower(10 * gamepad1.left_stick_y);
        } else if(gamepad1.left_stick_y == 0 ){
            tl.setPower(-10 * gamepad1.left_stick_x);
            tr.setPower(10 * gamepad1.left_stick_x);
            bl.setPower(10 * gamepad1.left_stick_x);
            br.setPower(-10 * gamepad1.left_stick_x);
        } else {
            tl.setPower(gamepad1.left_stick_x);
            br.setPower(gamepad1.left_stick_x);
            tr.setPower(gamepad1.left_stick_y);
            bl.setPower(gamepad1.left_stick_y);
        }

    }


}
