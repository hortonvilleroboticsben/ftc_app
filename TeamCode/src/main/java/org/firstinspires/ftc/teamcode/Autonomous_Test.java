package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Automanis", group = "Testing")
public class Autonomous_Test extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();

    String skyCase = "right";
    double safeSpeed = .7;

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);
    }

//    @Override
//    public void init_loop(){
//        StateMachine lazy = new StateMachine();
//    }

    @Override
    public void loop() {
        sm.initializeMachine();

        /*
        Insert Image Processing Code In This Section
         */

         /*
            Objective:  Use Image Processing Code to determine SkyStone Placement
                        and set skyCase to the correct orientation.

                        Travel to correct SkyStone, grab it and take it across the Bridge

                        Place near/on the foundation

                        **Travel back, grab the second SkyStone, and take across Bridge IT TIME

                        **Move Foundation to corner IF TIME

                        Place and park under Bridge
        */

        switch(skyCase){



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
                sm.translate(174.5,safeSpeed,20);
                sm.translate(-90,safeSpeed,75);
                break;
        }

        telemetry.addData("mtrLeftFront", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrRightFront", r.getEncoderCounts("mtrRightFront"));
        telemetry.addData("mtrLeftBack", r.getEncoderCounts("mtrLeftBack"));
        telemetry.addData("mtrRightBack", r.getEncoderCounts("mtrRightBack"));
    }
}
