package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Automanis", group = "Testing")
public class Autonomous_Test extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();
    StateMachine lazy = new StateMachine();
    StateMachine lazy2 = new StateMachine();

    String skyCase = "left";
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
        lazy2.initializeMachine();

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

        //This following code until the telemetry will not need to be implemented once the vision code is implemented
        if(lazy2.next_state_to_execute()) {
            telemetry.addData("SkyStone", "Left Trigger For Left : Left Bumper For Center : Right Trigger For Right");
            if ((gamepad1.a ^ gamepad1.left_trigger > .05) && !gamepad1.start && !gamepad2.start && !n) {
                skyCase = "left";
                n = true;
            }

            if((gamepad1.a ^ gamepad1.left_bumper) && !gamepad1.start && !gamepad2.start && !n){
                skyCase = "center";
                n=true;
            }

            if ((gamepad1.a ^ gamepad1.right_trigger > .05) && !gamepad1.start && !gamepad2.start && !n) {
                skyCase = "right";
                n = true;
            }

            if (n && !gamepad1.a && !gamepad1.b && gamepad1.right_trigger < 0.05 && !gamepad1.left_bumper && gamepad1.left_trigger < 0.05) {
                n = false;
                lazy2.incrementState();
            }
        }
        telemetry.addData("Foundation Side", foundationSide+"");
        telemetry.addData("SkyCase", skyCase+"");
    }

    @Override
    public void loop() {
        sm.initializeMachine();



        if (!foundationSide) {
            /*
            Objective:  Travel to correct SkyStone, grab it and take it across the Bridge

                        Place near/on the foundation

                        **Travel back, grab the second SkyStone, and take across Bridge IT TIME

                        **Move Foundation to corner IF TIME

                        Place and park under Bridge
        */
            switch (skyCase) {
                case "right":
                    try {
                        sm.translate(-26, safeSpeed, 22);
                        sm.initRunToTarget("mtrCollectionLeft",5000,.6);
                        sm.initRunToTarget("mtrCollectionRight", -5000, .6);
                        sm.pause(500);
                        laps(13);

                    } catch (Exception e) {
                        Log.e("LEFT SKYCASE", "Failure");
                    }
                    break;
                case "left":
                    try {
                        sm.translate(24, safeSpeed, 21);
                        sm.initRunToTarget("mtrCollectionLeft",5000,.6);
                        sm.initRunToTarget("mtrCollectionRight", -5000, .6);
                        sm.pause(500);
                        laps(0);
                    } catch (Exception e) {
                        Log.e("RIGHT SKYCASE", "Failure");
                    }
                    break;
                default:
                    try {
                        sm.translate(-10, safeSpeed, 20);
                        sm.initRunToTarget("mtrCollectionLeft",5000,.6);
                        sm.initRunToTarget("mtrCollectionRight", -5000, .6);
                        sm.pause(500);
                        laps(8);
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

    public void laps(double moreDistance){
        sm.translate(90, safeSpeed, 67+moreDistance);
        //Travel to Foundation and Place SkyStone
        sm.pause(500);
        sm.translate(-90, safeSpeed, 90+moreDistance);
        //Travel back for second stone
        sm.pause(500);
        //Grab second SkyStone
        sm.translate(90, safeSpeed, 90+moreDistance);
        //Travel to Foundation & Place Second SkyStone
        sm.pause(500);
        sm.translate(-90, safeSpeed, 40);
        //Park under bridge, ^ no need to change, 40 is same for all
    }
}
