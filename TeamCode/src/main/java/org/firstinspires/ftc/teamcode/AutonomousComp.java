package org.firstinspires.ftc.teamcode;


import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;

@Autonomous (name = "Competition Autonomous", group = "Testing")
public class AutonomousComp extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();
    StateMachine vision = new StateMachine();
    StateMachine multitask = new StateMachine();
    boolean OSFound, OSColor = false;
    double safeSpeed = .65;
    boolean waitOS, confirmOS = false;
    boolean selOS = false;
    Timer t = new Timer();

    JSONObject settings;
    File rFolder;
    ArrayList<String> fList = new ArrayList<>();
    int fIndex = 0;

    //    String allianceColor, returnPath, apFoundationOrientation = "No Change";
    String allianceColor = "blue"; //default
    String returnPath = "wall"; //default
    String apFoundationOrientation = "|"; //default
    boolean loadingSideStart = true; //default
    boolean apMoveFoundation = true; //default
    long wait = 0; //default
    int skyStones = 2; //default

    int[] vsData = null;

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);

        r.setServoPosition("srvFound", .85);

        rFolder = new File(Environment.getExternalStorageDirectory() + "/JSONConfigs");
        if (!rFolder.exists())
            rFolder.mkdir();
        for (File f : rFolder.listFiles()) {
            if (f.getName().substring(f.getName().length() - 5).equals(".json"))
                fList.add(f.getName());
        }

    }

    @Override
    public void init_loop() {
        sm.initializeMachine();
        if (!confirmOS) {
            if (fList.size() != 0) {
                telemetry.addData("Please select a file", fList.get(fIndex));

                if (gamepad1.dpad_down && !selOS) {
                    fIndex++;
                    selOS = true;
                }
                if (gamepad1.dpad_up && !selOS) {
                    fIndex--;
                    selOS = true;
                }
                if (!gamepad1.dpad_up && !gamepad1.dpad_down) selOS = false;

                fIndex = fIndex > fList.size() - 1 ? fList.size() - 1 : fIndex < 0 ? 0 : fIndex;

                if (gamepad1.a && !gamepad1.start && !gamepad2.start){
                    confirmOS = true;
                    telemetry.addData("File Selected",""+fList.get(fIndex));
                }
            } else {
                telemetry.addData("ERROR", "No files available.");
            }
        } else {
            File rPath = new File(rFolder, fList.get(fIndex));
            try {
                InputStream fin = new FileInputStream(rPath);
                byte[] raw_inp = new byte[fin.available()];
                fin.read(raw_inp);
                String om = new String(raw_inp);
                settings = new JSONObject(om);
                allianceColor = settings.getString("alliance");
                loadingSideStart = settings.getBoolean("loadingSideStart");
                wait = settings.getLong("pauseTime");
                returnPath = settings.getString("returnPath");
                skyStones = settings.getInt("skyStones");
                apMoveFoundation = settings.getBoolean("apMoveFoundation");
                apFoundationOrientation = settings.getString("apFoundationOrientation");
            } catch (Exception e) {
                e.printStackTrace();
                telemetry.addData("TRY CATCH ERROR", e.getCause());
            }

        }
    }

    @Override
    public void start() {
        sm.reset();
    }

    @Override
    public void loop() {
        sm.initializeMachine();
        vision.initializeMachine();
        multitask.initializeMachine();
//
        int[] temp = vision.getVisionData();
        vsData = temp == null ? vsData : temp;
        final int placement =1;
        telemetry.addData("Placement: ", placement + "");

        vision.SetFlag(sm, "Vision Done");


        sm.pause(wait);

        vision.WaitForFlag("VisionDone");

        if (loadingSideStart) {
            if (allianceColor.equals("blue")) {

                sm.translate(90, safeSpeed, 26);
                sm.rotate(-90, safeSpeed);

                sm.setServoPosition("srvClampLeft", .1);
                sm.setServoPosition("srvClampRight", .6);
                sm.setServoPower("srvRotator", .2);

                switch (placement) {
                    case 1:
                        sm.translate(-140, safeSpeed, 8);
                        break;
                    case 2:
                        sm.translate(170, safeSpeed, 7);
                        break;
                    case 3:
                        sm.translate(110, safeSpeed, 12);
                        break;
                }// End of Switch Statement

                // INSERT SERVO & LIFT CODE



                sm.pause(750);

                sm.setServoPosition("srvClampRight", .1);
                sm.setServoPosition("srvClampLeft", .3);

                sm.pause(750);

                if (returnPath.equals("not_wall")) {
                    //Distance to move from the stones to get to not_wall position
                    sm.translate(-87, safeSpeed, 73);
                    if (apMoveFoundation) {
                        //Distance to move to the foundation
                    }
                } else { //returnPath.equals("wall");
                    sm.translate(0, safeSpeed, 27);
                    sm.translate(-87, safeSpeed, 67); //TO BUILDING SIDE -90 is good
                    sm.SetFlag(multitask,"Start");

                    if (!apMoveFoundation) {
                        sm.translate(180, safeSpeed, 20); //DRIVE TO FOUNDATION
                    }
                }

                //BOTH DO AFTER REACHING FOUNDATION
                multitask.WaitForFlag("Start");

                telemetry.addData("flag", multitask.flag);
                telemetry.addData("state",multitask.state_in_progress);
                telemetry.addData("target", r.motors.get("mtrLift").getTargetPosition());
                telemetry.addData("pw", r.getPower("mtrLift"));
                telemetry.addData("rm", r.motors.get("mtrLift").getMode());


                if(multitask.next_state_to_execute()){
                    r.resetEncoder("mtrLift");
                    r.setTarget("mtrLift", -200);
                    r.setPower("mtrLift", 0.5);
                    r.setRunMode("mtrLift", DcMotor.RunMode.RUN_TO_POSITION);
                    multitask.incrementState();
                }

                telemetry.addData("enc",r.getEncoderCounts("mrtLift"));

                sm.translate(180, safeSpeed-.1, 6);

                sm.setServoPosition("srvClampLeft", .1);
                sm.setServoPosition("srvClampRight", .6);

                if(multitask.next_state_to_execute()){
                    if(r.hasMotorEncoderReached("mtrLift", -200)){
                        r.setPower("mtrLift", 0);
                        multitask.incrementState();
                    }
                }

                multitask.SetFlag(sm, "Done");

                sm.WaitForFlag("Done");

                sm.translate(0, safeSpeed, 2);

                if (!apMoveFoundation) {
                    sm.rotate(-95, safeSpeed);
                    sm.translate(-90,safeSpeed,2);
                    sm.translate(0, safeSpeed, 15.5);
                    sm.setServoPower("srvFound", .2);
                    sm.pause(500);
                    sm.translate(90, .75, 50);
                    sm.setServoPower("srvFound", .8);
                }

                if(multitask.next_state_to_execute()){
                    r.resetEncoder("mtrLift");
                    r.setTarget("mtrLift", 0);
                    r.setPower("mtrLift", 0.5);
                    r.setRunMode("mtrLift", DcMotor.RunMode.RUN_TO_POSITION);
                    multitask.incrementState();
                }

                if(multitask.next_state_to_execute()){
                    if(r.hasMotorEncoderReached("mtrLift", 0)){
                        r.setPower("mtrLift", 0);
                        multitask.incrementState();
                    }
                }

                multitask.SetFlag(sm, "Done");

                sm.WaitForFlag("Done");

//

//                    when have working color sensors

//                    if(sm.next_state_to_execute()) {
//                        int frontBlue = r.getColorValue("colorFront", "blue");
//                        int backBlue = r.getColorValue("colorBack", "blue");
//                        if(frontBlue<11){
//                            //WheelSetL is static, might cause problem
//                            r.setPower(Robot.wheelSet1[0],.2);
//                            r.setPower(Robot.wheelSet2[0],-.2);
//                        }else{
//                            r.setPower(Robot.wheelSet1[0],-.3);
//                            r.setPower(Robot.wheelSet2[0],.3);
//                        }
//                        if(backBlue<11){
//                            r.setPower(Robot.wheelSet1[1],.2);
//                            r.setPower(Robot.wheelSet2[1],-.2);
//                        }else{
//                            r.setPower(Robot.wheelSet1[1],-.3);
//                            r.setPower(Robot.wheelSet2[1],.3);
//                        }
//                        if(frontBlue >= 11 && backBlue >= 11){
//                            r.setDrivePower(0,0);
//                            sm.incrementState();
//                        }
//                    }



                //cut drive short... not under bridge.... close clamps, translate right 24, translate 180, open clamps



                if (returnPath.equals("wall")) { //This reaches to the tape line
//                    sm.translate(175, safeSpeed, 52.5);

                    sm.translate(175, safeSpeed, 36);
                    sm.setServoPosition("srvClampRight", .1);
                    sm.setServoPosition("srvClampLeft", .3);
                    sm.translate(-90, safeSpeed, 24);

                    sm.translate(180, safeSpeed, 5);

                    sm.setServoPosition("srvClampRight", .6);
                    sm.setServoPosition("srvClampLeft", .1);


                } else if (returnPath.equals("not_wall")) {
                    //Move from building site to not_wall position
                }

                if (skyStones == 2) { //GOING FOR SECOND SKYSTONE
                    switch (placement) {
                        case 1:
                            sm.translate(175, safeSpeed, 52.5);
                            if (returnPath.equals("wall")) {
                                sm.translate(-90, safeSpeed, 5);
                                sm.rotate(90, safeSpeed);
                                sm.translate(180, safeSpeed, 25);
                            } else {
                                //Get enough space to rotate into the stone
                            }
                            break;
                        case 2:

                            break;

                        case 3:

                            break;
                    }
                }

            } else { //ALLIANCE COLOR === RED, LOADING SIDE
                //TODO: Edit these values
                sm.translate(90, safeSpeed, 26);
                sm.rotate(-90, safeSpeed);

                switch (placement) {
                    case 1:
                        sm.translate(140, safeSpeed, 8);
                        break;
                    case 2:
                        sm.translate(170, safeSpeed, 7);
                        break;
                    case 3:
                        sm.translate(110, safeSpeed, 12);
                        break;
                }// End of Switch Statement

                // INSERT SERVO & LIFT CODE

                if (returnPath.equals("not_wall")) {
                    //Distance to move from the stones to get to not_wall position
                    sm.translate(87, safeSpeed, 73);
                    if (apMoveFoundation) {
                        //Distance to move to the foundation
                    }
                } else { //returnPath.equals("wall");
                    sm.translate(0, safeSpeed, 27);
                    sm.translate(-87, safeSpeed, 73); //TO BUILDING SIDE -90 is good
                    if (apMoveFoundation) {
                        sm.translate(180, safeSpeed, 20); //DRIVE TO FOUNDATION
                    }
                }
//                        if(sm.next_state_to_execute()){
//                            r.initRunToTarget("mtrLift", 3000, safeSpeed);
//                            if(r.hasMotorEncoderReached("mtrLift",3010)){
//                                r.initRunToTarget("mtrLift", 0, safeSpeed);
//                                if(r.hasMotorEncoderReached("mtrLift",0+200)){
//                                    r.setPower("mtrLift",0);
//                                }
//                            }
//                            r.setServoPosition("srvClampLeft", .1);
//                            r.setServoPosition("srvClampRight", .6);
//
//                            sm.translate(180, .2, 4);
//
//                            r.setServoPosition("srvClampRight", .1);
//
//
//                            sm.translate(0, .2, 4);
//
//                            sm.incrementState();
//                        }

//                        sm.translate(90, safeSpeed, 24); //travelling to wall

                //BOTH DO AFTER REACHING FOUNDATION

                if (apMoveFoundation) {
                    sm.rotate(-90, safeSpeed);


                    sm.translate(180, safeSpeed, 15.5);

                    if (sm.next_state_to_execute()) { //FOUNDATION GRABBER DOWN
                        r.setServoPosition("srvFound", .8);
                        sm.incrementState();
                    }

                    sm.pause(500);

                    sm.translate(90, .75, 50);

                    if (sm.next_state_to_execute()) { //FOUNDATION GRABBER UP
                        r.setServoPosition("srvFound", .2);
                        sm.incrementState();
                    }
                }

//

//                    when have working color sensors

//                    if(sm.next_state_to_execute()) {
//                        int frontBlue = r.getColorValue("colorFront", "blue");
//                        int backBlue = r.getColorValue("colorBack", "blue");
//                        if(frontBlue<11){
//                            //WheelSetL is static, might cause problem
//                            r.setPower(Robot.wheelSet1[0],.2);
//                            r.setPower(Robot.wheelSet2[0],-.2);
//                        }else{
//                            r.setPower(Robot.wheelSet1[0],-.3);
//                            r.setPower(Robot.wheelSet2[0],.3);
//                        }
//                        if(backBlue<11){
//                            r.setPower(Robot.wheelSet1[1],.2);
//                            r.setPower(Robot.wheelSet2[1],-.2);
//                        }else{
//                            r.setPower(Robot.wheelSet1[1],-.3);
//                            r.setPower(Robot.wheelSet2[1],.3);
//                        }
//                        if(frontBlue >= 11 && backBlue >= 11){
//                            r.setDrivePower(0,0);
//                            sm.incrementState();
//                        }
//                    }
                if (returnPath.equals("wall")) { //This reaches to the tape line
                    sm.translate(175, safeSpeed, 52.5);
                } else if (returnPath.equals("not_wall")) {
                    //Move from building site to not_wall position
                }

                if (skyStones == 2) { //GOING FOR SECOND SKYSTONE
                    switch (placement) {
                        case 1:
                            sm.translate(175, safeSpeed, 52.5);
                            if (returnPath.equals("wall")) {
                                sm.translate(-90, safeSpeed, 5);
                                sm.rotate(90, safeSpeed);
                                sm.translate(180, safeSpeed, 25);
                            } else {
                                //Get enough space to rotate into the stone
                            }
                            break;
                        case 2:

                            break;

                        case 3:

                            break;
                    }
                }

            }

        } else { //BUILDING SIDE
            if (allianceColor.equals("blue")) {

                if (apMoveFoundation) {
                    sm.translate(-90, safeSpeed, 28);

                    if (sm.next_state_to_execute()) { //FOUNDATION GRABBER DOWN
                        r.setServoPosition("srvFound", .8);
                        sm.incrementState();
                    }

                    sm.pause(500);

                    sm.translate(90, .75, 50);

                    if (sm.next_state_to_execute()) { //FOUNDATION GRABBER UP
                        r.setServoPosition("srvFound", .2);
                        sm.incrementState();
                    }

                }

                if (returnPath.equals("not_wall")) {

                    sm.translate(175, safeSpeed, 25);
                    sm.translate(-135, safeSpeed, 25);
                    sm.translate(180, safeSpeed, 9);

                } else { //returnPath.equals("wall");
                    sm.translate(175, safeSpeed, 50.5);
                }

            } else { // ALLIANCE COLOR === RED

                sm.translate(90, safeSpeed, 26);
                sm.rotate(-90, safeSpeed);

                if (returnPath.equals("not_wall")) {


                } else { //returnPath.equals("wall");
//

//                    when have working color sensors

//                    if(sm.next_state_to_execute()) {
//                        int frontBlue = r.getColorValue("colorFront", "blue");
//                        int backBlue = r.getColorValue("colorBack", "blue");
//                        if(frontBlue<11){
//                            //WheelSetL is static, might cause problem
//                            r.setPower(Robot.wheelSet1[0],.2);
//                            r.setPower(Robot.wheelSet2[0],-.2);
//                        }else{
//                            r.setPower(Robot.wheelSet1[0],-.3);
//                            r.setPower(Robot.wheelSet2[0],.3);
//                        }
//                        if(backBlue<11){
//                            r.setPower(Robot.wheelSet1[1],.2);
//                            r.setPower(Robot.wheelSet2[1],-.2);
//                        }else{
//                            r.setPower(Robot.wheelSet1[1],-.3);
//                            r.setPower(Robot.wheelSet2[1],.3);
//                        }
//                        if(frontBlue >= 11 && backBlue >= 11){
//                            r.setDrivePower(0,0);
//                            sm.incrementState();
//                        }
//                    }
                }
            }

            telemetry.addData("mtrLeftFront", r.getEncoderCounts("mtrFrontLeft"));
            telemetry.addData("mtrRightFront", r.getEncoderCounts("mtrRightFront"));
            telemetry.addData("mtrLeftBack", r.getEncoderCounts("mtrLeftBack"));
            telemetry.addData("mtrRightBack", r.getEncoderCounts("mtrRightBack"));

            telemetry.addData("RED Front", r.getColorValue("colorFront", "red"));
            telemetry.addData("BLUE", r.getColorValue("colorFront", "blue"));
            telemetry.addData("GREEN", r.getColorValue("colorFront", "green"));

            telemetry.addData("RED Back", r.getColorValue("colorBack", "red"));
            telemetry.addData("BLUE", r.getColorValue("colorBack", "blue"));
            telemetry.addData("GREEN", r.getColorValue("colorBack", "green"));
        }
    }
}
