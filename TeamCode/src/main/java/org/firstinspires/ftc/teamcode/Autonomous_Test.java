package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Automanis", group = "Testing")
public class Autonomous_Test extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();
    String skyCase = "left";
    boolean foundationSide, n  = false;
    double safeSpeed = .7;
    long wait = 0;
    boolean waitOS, confirmOS = false;
    Timer t = new Timer();

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
            if ((gamepad1.a ^ gamepad1.b) && !gamepad1.start) {
                foundationSide = gamepad1.a;
                n = true;
            }
            if (n && !gamepad1.a && !gamepad1.b) {
                n = false;
                sm.incrementState();
            }
        }
        if(sm.next_state_to_execute()){
            telemetry.addData("Pause?","Dpad Down: -1 Second, Dpad Up: +1 Second");
            if(gamepad1.dpad_up && !waitOS){
                waitOS = true;
                wait+=1000;
            } else if (gamepad1.dpad_down && !waitOS){
                waitOS = true;
                wait-=1000;
            } else if (!gamepad1.dpad_up && !gamepad1.dpad_down){
                waitOS = false;
            } else if (gamepad1.a && !gamepad1.start && !confirmOS){
                confirmOS = true;
                sm.incrementState();
            } else if (!gamepad1.a){
                confirmOS = false;
            }
            telemetry.addData("Pause Amount:",wait);
        }
        telemetry.addData("Foundation Side", foundationSide+"");
    }

    @Override
    public void loop() {
        sm.initializeMachine();
        sm.pause(wait);
        if (!foundationSide) {
            /*
            Objective:  Travel to correct SkyStone, grab it and take it across the Bridge

                        Place near/on the foundation

                        **Travel back, grab the second SkyStone, and take across Bridge IT TIME

                        **Move Foundation to corner IF TIME

                        Place and park under Bridge
            */
            //sm.skyStone();

            switch (sm.pos) {
                case 2:
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
                case 0:
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
