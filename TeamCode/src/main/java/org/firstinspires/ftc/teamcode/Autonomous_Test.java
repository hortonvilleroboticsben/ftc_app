package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Automanis", group = "Testing")
public class Autonomous_Test extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();
    String skyCase = "left";
    boolean foundationSide, skyStone = false;
    double safeSpeed = .7;
    int redThresh = 100;

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void init_loop(){
        sm.initializeMachine();

        if(sm.next_state_to_execute()) {
            telemetry.addData("Foundation Side", "A For Yes : B For No");
            if(gamepad1.a && !gamepad1.start && !foundationSide){
                foundationSide = true;
            } else if(!gamepad1.a){
                sm.incrementState();
            }
        }
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
                case "right":
                    try {
                        sm.translate(-26, safeSpeed, 22);
                        sm.pause(500);
                        laps(13);

                    } catch (Exception e) {
                        Log.e("LEFT SKYCASE", "Failure");
                    }
                    break;
                case "left":
                    try {
                        sm.translate(24, safeSpeed, 21);
                        sm.pause(500);
                        laps(0);
                    } catch (Exception e) {
                        Log.e("RIGHT SKYCASE", "Failure");
                    }
                    break;
                default:
                    try {
                        sm.translate(-10, safeSpeed, 20);
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
