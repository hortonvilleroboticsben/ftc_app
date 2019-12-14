package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "TeleOpComp", group ="Comp")
public class TeleOpComp extends OpMode {

    Robot r;
    public boolean auto = false;
    StateMachine m = new StateMachine();
    double theta1 = 0;
    boolean OSConveyor, runningConveyor, OSClamp, openClamp, OSRotator, openRotator = false;

    @Override
    public void init() {
        m.state_in_progress = 99;
        r = Robot.getInstance();
        r.initialize(this);
        r.setRunMode("mtrCollectionLeft", DcMotor.RunMode.RUN_USING_ENCODER);
        r.setRunMode("mtrCollectionRight", DcMotor.RunMode.RUN_USING_ENCODER);
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
            if (Math.abs(gamepad1.right_stick_x) < 0.075) {
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
                double speedControl = gamepad1.right_bumper ? 1 : gamepad1.right_trigger > .5 ? .25 : .5;
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

            if(gamepad1.left_bumper) {
                r.setPower("mtrCollectionLeft", .72);
                r.setPower("mtrCollectionRight", .72);
            }else if(gamepad1.left_trigger > 0.5){
                r.setPower("mtrCollectionLeft",-.6);
                r.setPower("mtrCollectionRight",-.6);
            } else {
                r.setPower("mtrCollectionLeft",0);
                r.setPower("mtrCollectionRight",0);
            }
//            if(gamepad1.left_trigger > .05){
//                r.setPower("mtrCollectionLeft",-.6);
//                r.setPower("mtrCollectionRight",-.6);
//            } else {
//                r.setPower("mtrCollectionLeft",0);
//                r.setPower("mtrCollectionRight",0);
//            }

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

            if(gamepad2.left_bumper){
                r.resetEncoder("mtrLift");
            }

//            if(gamepad2.dpad_up && !(levelEncoders>17000) && !OSLift){
//                OSLift = true;
//                r.initRunToTarget("mtrLift", levelEncoders+=8000,.6);
//            } else if (!gamepad2.dpad_up) {
//                OSLift = false;
//            }
//
//            if(gamepad2.dpad_down && !(levelEncoders<0) && !OSLift){
//                OSLift = true;
//                r.initRunToTarget("mtrLift", levelEncoders-=8000,.6);
//            } else if(!gamepad2.dpad_down) {
//                OSLift = false;
//            }

//          *************************************Toggles********************************************

            if(gamepad2.a && !OSClamp){
                OSClamp = true;
                openClamp = !openClamp;
            } else if(!gamepad2.a) OSClamp = false;

            if(openClamp){
                r.setServoPosition("srvClamp",.6);
            } else {
                r.setServoPosition("srvClamp",.1);
            }

            //Conveyor..............................................................................
            //.1 power is pull inwards, as you approach .5, it slows down to a stop. as you approach .9 pulls outward towards a stop
            if(gamepad2.y && !OSConveyor) {
                OSConveyor = true;
                runningConveyor = !runningConveyor;
            } else if (!gamepad2.y) {
                OSConveyor = false;
            }
            if(runningConveyor){
                r.setServoPower("srvConveyor",-.9 );
            } else {
                r.setServoPower("srvConveyor",0 );

            }
            //Rotator...............................................................................

            if(gamepad2.x && !OSRotator){
                OSRotator = true;
                openRotator = !openRotator;
            } else if(!gamepad2.x) OSRotator = false;

            if(openRotator){
                r.setServoPosition("srvRotator", 1);
            } else {
                r.setServoPosition("srvRotator", 0);
            }

//          **********************************Stick Adjustments*************************************


//            //clamp adjustment......................................................................
//            if(Math.abs(gamepad2.left_stick_y) > 0.05){
//                r.setServoPosition("srvClamp", Math.abs(gamepad2.left_stick_y));
//            } else if(Math.abs(gamepad1)) {
//                r.setServoPosition("srvClamp",0);
//            }



            if(gamepad2.left_stick_button){
                r.setServoPosition("srvFlip",.2);
            }

            //lift adjustment.......................................................................
            if(Math.abs(gamepad2.right_stick_y) > 0.05){
                r.setPower("mtrLift",gamepad2.right_stick_y);
            } else {
                r.setPower("mtrLift",0);
            }

        }
// Automous In TeleOp
//        if(gamepad1.a && !gamepad1.start){
//            auto = true;
//            m.reset();
//            r.resetDriveEncoders();
//        }
//
//
//        //place autonomous code for teleop here
//
//        if(m.next_state_to_execute() && auto){
//            auto = false;
//            m.incrementState();
//        }

        telemetry.addData("mtrFrontLeft", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrFrontRight", r.getEncoderCounts("mtrFrontRight"));
        telemetry.addData("mtrBackLeft", r.getEncoderCounts("mtrBackLeft"));
        telemetry.addData("mtrBackRight", r.getEncoderCounts("mtrBackRight"));

        telemetry.addData("Clamp Position:",((Servo)r.servos.get("srvClamp")).getPosition());
        telemetry.addData("Rotator Position:",((Servo)r.servos.get("srvRotator")).getPosition());
        //                                                                                                                                                                          telemetry.addData("Conveyor Power:",((CRServo)r.servos.get("srvConveyor")).getPower());

        telemetry.addData("theta1", theta1 * 180 / Math.PI);
        telemetry.addData("SIP", m.state_in_progress);
        telemetry.addData("Auto", auto);
    }
}
