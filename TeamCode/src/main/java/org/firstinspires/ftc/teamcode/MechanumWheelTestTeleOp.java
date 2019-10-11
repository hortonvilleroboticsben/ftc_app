package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "MecanumTest", group ="test")
public class MechanumWheelTestTeleOp extends OpMode {

    Robot r;
    public boolean auto = false;
    StateMachine m = new StateMachine();
    double theta1 = 0;

    @Override
    public void init() {
        m.state_in_progress = 99;
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void loop() {

        m.initializeMachine();

        if (!auto) {
            if (Math.abs(gamepad1.right_stick_x) < 0.05) {

                double x = gamepad1.left_stick_x;
                double y = -gamepad1.left_stick_y;

                theta1 = ((Math.atan(y / x)));

                if (x == 0 && y > 0) {
                    theta1 = Math.PI / 2;
                } else if (x == 0 && y < 0) {
                    theta1 = 3 * Math.PI / 2;
                } else if (x < 0) {
                    theta1 = Math.atan(y / x) + Math.PI;
                }
                double theta2 = Math.PI / 4 - theta1;

                r.setPower(r.wheelSet1[0], Math.abs(x) > .05 || Math.abs(y) > .05 ? Math.sqrt(x * x + y * y) * Math.cos(theta2) : 0);
                r.setPower(r.wheelSet2[0], Math.abs(x) > .05 || Math.abs(y) > .05 ? -Math.sqrt(x * x + y * y) * Math.sin(theta2) : 0);
                r.setPower(r.wheelSet1[1], Math.abs(x) > .05 || Math.abs(y) > .05 ? Math.sqrt(x * x + y * y) * Math.cos(theta2) : 0);
                r.setPower(r.wheelSet2[1], Math.abs(x) > .05 || Math.abs(y) > .05 ? -Math.sqrt(x * x + y * y) * Math.sin(theta2) : 0);


            }else {
                r.setPower(r.wheelSetL[0], gamepad1.right_stick_x);
                r.setPower(r.wheelSetL[1], gamepad1.right_stick_x);
                r.setPower(r.wheelSetR[0], -gamepad1.right_stick_x);
                r.setPower(r.wheelSetR[1], -gamepad1.right_stick_x);
            }
        }

        if(gamepad1.a && !gamepad1.start){
            auto = true;
            m.reset();

        }

        if(m.next_state_to_execute() && auto && gamepad1.b){
            m.translate(45, .5, 24);
            auto = false;
            m.incrementState();
        }



        telemetry.addData("mFL", r.getEncoderCounts("mtrFrontLeft"));
        //stays 0
        telemetry.addData("mFR", r.getEncoderCounts("mtrFrontRight"));
        //starts at -1, goes back and forth between 1 and 0
        telemetry.addData("mBR", r.getEncoderCounts("mtrBackLeft"));
        telemetry.addData("mBL", r.getEncoderCounts("mtrBackRight"));

            telemetry.addData("theta1", theta1 * 180 / Math.PI);
            telemetry.addData("SIP", m.state_in_progress);
            telemetry.addData("Auto", auto);
    }
}
