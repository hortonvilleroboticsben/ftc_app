package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "MecanumTest", group ="test")
public class MechanumWheelTestTeleOp extends OpMode {

    Robot r;
    StateMachine m = new StateMachine();

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void loop() {
        double theta1 = ((Math.atan(gamepad1.left_stick_y/gamepad1.left_stick_x)));
        double x = gamepad1.left_stick_x;
        double y = gamepad1.left_stick_y;
        if(gamepad1.left_stick_x == 0 && gamepad1.left_stick_y > 0){
            theta1 = Math.PI/2;
        } else if (gamepad1.left_stick_x ==0 && gamepad1.left_stick_y < 0){
            theta1 = -Math.PI/2;
        } else if(gamepad1.left_stick_x < 0){
            //theta1 = Math.atan(gamepad1.left_stick_y/gamepad1.left_stick_x) + Math.PI;
        } else if(gamepad1.left_stick_x > 0){
            theta1 = (Math.atan(gamepad1.left_stick_y/gamepad1.left_stick_x) + Math.PI);
        }
        double theta2 = -3*Math.PI/4-theta1;
        m.initializeMachine();

        r.setPower(r.wheelSet1[0], Math.abs(gamepad1.left_stick_x) > .05 || Math.abs(gamepad1.left_stick_y) > .05 ? Math.sqrt(x*x+y*y)*Math.cos(theta2) : 0);
        r.setPower(r.wheelSet2[0], Math.abs(gamepad1.left_stick_x) > .05 || Math.abs(gamepad1.left_stick_y) > .05 ? -Math.sqrt(x*x+y*y)*Math.sin(theta2): 0);
        r.setPower(r.wheelSet1[1], Math.abs(gamepad1.left_stick_x) > .05 || Math.abs(gamepad1.left_stick_y) > .05 ? Math.sqrt(x*x+y*y)*Math.cos(theta2) : 0);
        r.setPower(r.wheelSet2[1], Math.abs(gamepad1.left_stick_x) > .05 || Math.abs(gamepad1.left_stick_y) > .05 ? -Math.sqrt(x*x+y*y)*Math.sin(theta2): 0);
    }
}
