package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "TeleOpComp", group ="Comp")
public class TeleOpComp extends OpMode {

    Robot r;
    public boolean auto = false;
    StateMachine m = new StateMachine();
    double theta1 = 0;
    boolean OSCollection, OSLift, OSConveyor, OSClamp, OSRotator = false;
    int levelEncoders = 600; //may have to adjust
    double collectionSpeed = .6;
    double liftSpeed = .6;
    double conveyorSpeed = .6;
    double clampOpen = 0.4;
    double clampClosed = 0.175;
    double rotatorIn = 1;
    double rotatorOut = 0;

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

//          ****************************************************************************************
            //            Gamepad One Controls
            //right trigger     25% speed
            //right button      72% speed
            //regular           50% speed
            //left button       toggle collector


//          ****************************Driving Controls********************************************

            //No turning while translating
            if (Math.abs(gamepad1.right_stick_x) < 0.05) {

                double x = gamepad1.left_stick_x;
                double y = -gamepad1.left_stick_y;

                theta1 = ((Math.atan(y / x)));
                //This series of if statements prevents us from dividing by 0
                //Because we divide by X, X != 0
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
                double x = gamepad1.right_stick_x;
                double y = gamepad1.right_stick_y;
                r.setPower(r.wheelSetL[0], gamepad1.right_trigger > .5 ? x*.25 : gamepad1.right_bumper ? x*.75: x *.5);
                r.setPower(r.wheelSetL[1], gamepad1.right_trigger > .5 ? x*.25 : gamepad1.right_bumper ? x*.75: x *.5);
                r.setPower(r.wheelSetR[0], -gamepad1.right_trigger > .5 ? x*.25 : gamepad1.right_bumper ? x*.75: x *.5);
                r.setPower(r.wheelSetR[1], -gamepad1.right_trigger > .5 ? x*.25 : gamepad1.right_bumper ? x*.75: x *.5);
            }

//          ***************************Collector Controls*******************************************

            if(gamepad1.left_bumper && !OSCollection){
                OSCollection = true;
                r.setPower("mtrCollectionLeft", collectionSpeed);
                r.setPower("mtrCollectionRight", -collectionSpeed);
            }
            if(!gamepad1.left_bumper && OSCollection){
                OSCollection = false;
                r.setPower("mtrCollectionLeft",0);
                r.setPower("mtrCollectionRight",0);
            }


//          ****************************************************************************************
//            Gamepad Two Controls
            //left bumper       lift encoder reset
            //dpad up           level up
            //dpad down         level down
            //left stick        adjust clamp
            //right stick       adjust lift
            //a                 toggle clamp
            //y                 toggle conveyor
            //x                 toggle angle in
            //b                 toggle angle out

//          ********************************Lift Controls*******************************************

            //reset lift encoders to zero...........................................................
            if(gamepad2.left_bumper){
                r.resetEncoder("mtrLift");
            }

            //up and down functionality.............................................................

            //up
            if(gamepad2.dpad_up && !(levelEncoders>17000) && !OSLift){
                OSLift = true;
                r.initRunToTarget("mtrLift", levelEncoders+=200,liftSpeed);
            }
            if(!gamepad2.dpad_up && OSLift){OSLift = false;}

            //down
            if(gamepad2.dpad_down && !(levelEncoders<0) && !OSLift){
                OSLift = false;
                r.initRunToTarget("mtrLift", levelEncoders-=200,liftSpeed);
            }
            if(!gamepad2.dpad_down && OSLift){OSLift = false;}

//          *************************************Toggles********************************************

            //Clamp.................................................................................

            //open
            if(gamepad2.a && !gamepad2.start && !gamepad1.start && !OSClamp){
                OSClamp = true;
                r.setServoPosition("srvClamp", clampOpen);
            }

            //closed
            if(gamepad2.a && !gamepad2.start && !gamepad1.start && OSClamp){
                OSClamp = false;
                r.setServoPosition("srvClamp", clampClosed);
            }

            //Conveyor..............................................................................

            //on
            if(gamepad2.y && !OSConveyor){
                OSConveyor = true;
                r.setPower("mtrConveyor",conveyorSpeed);
            }

            //off
            if(!gamepad2.y && OSConveyor){
                OSConveyor = false;
                r.setPower("mtrConveyor", 0);
            }

            //Rotator...............................................................................

            //x - in
            if(gamepad2.x && !OSRotator){
                OSRotator = true;
                r.setServoPosition("srvRotator",rotatorIn);
            }

            //b - out
            if(gamepad2.b && OSRotator){
                OSRotator = false;
                r.setServoPosition("srvRotator", rotatorOut);
            }

//          **********************************Stick Adjustments*************************************


            //clamp adjustment......................................................................
            if(gamepad2.left_stick_y > 0.05){
                r.setServoPosition("srvClamp", gamepad2.left_stick_y);
            }

            //lift adjustment.......................................................................
            if(gamepad2.right_stick_y > 0.05){
                r.setPower("mtrLift",gamepad2.right_stick_y);
            }

        }

        if(gamepad1.a && !gamepad1.start){
            auto = true;
            m.reset();
            r.resetDriveEncoders();

        }

        /*
        place autonomous code for teleop here
         */

        if(m.next_state_to_execute() && auto){
            auto = false;
            m.incrementState();
        }

        telemetry.addData("mtrFrontLeft", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrFrontRight", r.getEncoderCounts("mtrFrontRight"));
        telemetry.addData("mtrBackLeft", r.getEncoderCounts("mtrBackLeft"));
        telemetry.addData("mtrBackRight", r.getEncoderCounts("mtrBackRight"));

        telemetry.addData("theta1", theta1 * 180 / Math.PI);
        telemetry.addData("SIP", m.state_in_progress);
        telemetry.addData("Auto", auto);
    }
}
