package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Automanis", group = "Testing")
public class Autonomous_Test extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();
    StateMachine lazy = new StateMachine();

    String skyCase = "center";
    boolean foundationSide, n  = false;
    double safeSpeed = .7;

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void init_loop(){
        lazy.initializeMachine();
        if(lazy.next_state_to_execute()) {
            telemetry.addData("Foundation Side", "A For Yes : B For No");
            if ((gamepad1.a ^ gamepad1.b) && !gamepad1.start && !gamepad2.start) {
                foundationSide = gamepad1.a;
                n = true;
            }
            if (n && !gamepad1.a && !gamepad1.b) {
                n = false;
                lazy.incrementState();
            }
        }
        telemetry.addData("Foundation Side", foundationSide+"");
    }

    @Override
    public void loop() {
        sm.initializeMachine();

        /*
        Insert Image Processing Code In This Section
         */

        if (!foundationSide) {
            /*
            Objective:  Travel to correct SkyStone, grab it and take it across the Bridge

                        Place near/on the foundation

                        **Travel back, grab the second SkyStone, and take across Bridge IT TIME

                        **Move Foundation to corner IF TIME

                        Place and park under Bridge
        */
            switch (skyCase) {
                case "left":
                    try {
                        sm.translate(154, safeSpeed, 22); //Travel to and Grab SkyStone
//                    sm.wait(500);
                        sm.translate(-90, safeSpeed, 83); //Travel to Foundation and Place SkyStone
//                    sm.wait(500);
                        sm.translate(90, safeSpeed, 102); //Travel back for second stone
//                    sm.wait(500); //Grab second SkyStone
                        sm.translate(-90, safeSpeed, 102); //Travel to Foundation & Place Second SkyStone
//                    sm.wait(500);
                        sm.translate(90, safeSpeed, 40); //Park Under Bridge
                    } catch (Exception e) {
                        Log.e("LEFT SKYCASE", "Failure");
                    }
                    break;
                case "right":
                    try {
                        sm.translate(-159, safeSpeed, 21); //Travel to and Grab SkyStone
//                    sm.wait(500);
                        sm.translate(-90, safeSpeed, 67); //Travel to Foundation and Place SkyStone
//                    sm.wait(500);
                        sm.translate(90, safeSpeed, 90); //Travel back for second stone
//                    sm.wait(500); //Grab second SkyStone
                        sm.translate(-90, safeSpeed, 90); //Travel to Foundation & Place Second SkyStone
//                    sm.wait(500);
                        sm.translate(90, safeSpeed, 40); //Park Under Bridge
                    } catch (Exception e) {
                        Log.e("RIGHT SKYCASE", "Failure");
                    }
                    break;
                default:
                    try {
                        sm.translate(174.5, safeSpeed, 20); //Travel to and Grab SkyStone
//                    sm.wait(500);
                        sm.translate(-90, safeSpeed, 75); //Travel to Foundation and Place SkyStone
//                    sm.wait(500);
                        sm.translate(90, safeSpeed, 98); //Travel back for second stone
//                    sm.wait(500); //Grab second SkyStone
                        sm.translate(-90, safeSpeed, 98); //Travel to Foundation & Place Second SkyStone
//                    sm.wait(500);
                        sm.translate(90, safeSpeed, 40);
                    } catch (Exception e) {
                        Log.e("CENTER/DEFAULT SKYCASE", "Failure");
                    }
                    break;
            }

        } else {

        }


        telemetry.addData("foundationSide", foundationSide+"");
        telemetry.addData("mtrLeftFront", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrRightFront", r.getEncoderCounts("mtrRightFront"));
        telemetry.addData("mtrLeftBack", r.getEncoderCounts("mtrLeftBack"));
        telemetry.addData("mtrRightBack", r.getEncoderCounts("mtrRightBack"));

    }
}
