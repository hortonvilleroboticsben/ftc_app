package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "TeleOpComp", group ="Comp")
public class TeleOpComp extends OpMode {

    Robot r;
    public boolean auto = false;
    StateMachine m = new StateMachine();
    double theta1 = 0;
    boolean OSCollection, OSLift, OSConveyor, runningConveyor, OSClamp, openClamp, OSRotator, openRotator = false;
    int levelEncoders = 600; //may have to adjust
    double collectionSpeed = .6;
    double liftSpeed = .6;
    double clampOpen = 0.4;
    double clampClosed = 0.175;;

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

            //If not rotating
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
                double hyp = Math.sqrt(x * x + y * y);
                boolean motorBand = Math.abs(x) > .05 || Math.abs(y)> .05;
                double speedControl = gamepad1.right_bumper ? .75 : gamepad1.right_trigger > .5 ? .25 : .5;
                r.setPower(r.wheelSet1[0], motorBand ?  hyp * Math.cos(theta2) * speedControl : 0);
                r.setPower(r.wheelSet2[0], motorBand ? -hyp * Math.sin(theta2) * speedControl : 0);
                r.setPower(r.wheelSet1[1], motorBand ?  hyp * Math.cos(theta2) * speedControl : 0);
                r.setPower(r.wheelSet2[1], motorBand ? -hyp * Math.sin(theta2) * speedControl : 0);

            }else {
                r.setPower(r.wheelSetL[0], gamepad1.right_stick_x);
                r.setPower(r.wheelSetL[1], gamepad1.right_stick_x);
                r.setPower(r.wheelSetR[0], -gamepad1.right_stick_x);
                r.setPower(r.wheelSetR[1], -gamepad1.right_stick_x);
            }

//          ***************************Collector Controls*******************************************

            if(gamepad1.left_bumper && !OSCollection){
                OSCollection = true;
                r.setPower("mtrCollectionLeft", collectionSpeed);
                r.setPower("mtrCollectionRight", collectionSpeed);
            } else if(!gamepad1.left_bumper) {
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
            //Make lift function that tracks level instead of encoder counts and raises or lowers accordingly
            //up
            if(gamepad2.dpad_up && !(levelEncoders>17000) && !OSLift){
                OSLift = true;
                r.initRunToTarget("mtrLift", levelEncoders+=8000,liftSpeed);
            } else if (!gamepad2.dpad_up) {
                OSLift = false;
            }

            //down
            if(gamepad2.dpad_down && !(levelEncoders<0) && !OSLift){
                OSLift = true;
                r.initRunToTarget("mtrLift", levelEncoders-=8000,liftSpeed);
            } else if(!gamepad2.dpad_down) {
                OSLift = false;
            }

//          *************************************Toggles********************************************

            //Clamp.................................................................................

            //open
            if(gamepad2.a && !gamepad2.start && !OSClamp){
                OSClamp = true;
                openClamp = !openClamp;
            } else if(!gamepad2.a) OSClamp = false;

            if(openClamp){
                r.setServoPosition("srvClamp",.6);
            } else {
                r.setServoPosition("srvClamp",.2);
            }

            //Conveyor..............................................................................

            //on
            if(gamepad2.y && !OSConveyor) {
                OSConveyor = true;
                runningConveyor = !runningConveyor;
            } else if (!gamepad2.y) {
                OSConveyor = false;
            }
            if(runningConveyor){
                r.setServoPower("srvConveyor",.6 );
            } else {
                r.setServoPower("srvConveyor",0 );

            }


            //Rotator...............................................................................

            //x - in
            if(gamepad2.x && !OSRotator){
                OSRotator = true;
                openRotator = !openRotator;
            } else if(!gamepad2.x) OSRotator = false;

            if(openRotator){
                r.setServoPosition("srvRotator", .9);
            } else {
                r.setServoPosition("srvRotator", .2);
            }

//          **********************************Stick Adjustments*************************************


            //clamp adjustment......................................................................
            if(Math.abs(gamepad2.left_stick_y) > 0.05){
                r.setServoPosition("flip", Math.abs(gamepad2.left_stick_y));
            } else {
                r.setServoPosition("flip",0);
            }

            //lift adjustment.......................................................................
            if(Math.abs(gamepad2.right_stick_y) > 0.05){
                r.setPower("mtrLift",-gamepad2.right_stick_y);
            } else {
                r.setPower("mtrLift",0);
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

        telemetry.addData("Clamp",((Servo) r.servos.get("srvClamp")).getPosition());
        telemetry.addData("Rotator",((Servo) r.servos.get("srvRotator")).getPosition());
        telemetry.addData("Flip",((Servo) r.servos.get("flip")).getPosition());

        telemetry.addData("theta1", theta1 * 180 / Math.PI);
        telemetry.addData("SIP", m.state_in_progress);
        telemetry.addData("Auto", auto);
    }
}
